package tytoo.weave.utils;

import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class ImageManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Map<File, Identifier> fileCache = new HashMap<>();
    private static final Map<URI, CompletableFuture<Identifier>> urlCache = new HashMap<>();
    private static final ExecutorService VIRTUAL_THREAD_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private static Identifier PLACEHOLDER_ID;

    public static Identifier getPlaceholder() {
        if (PLACEHOLDER_ID == null) {
            NativeImage image = new NativeImage(NativeImage.Format.RGBA, 1, 1, false);
            image.setColorArgb(0, 0, 0xFFFFFFFF);
            NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
            PLACEHOLDER_ID = Identifier.of("weave", "placeholder_image");
            client.getTextureManager().registerTexture(PLACEHOLDER_ID, texture);
        }
        return PLACEHOLDER_ID;
    }

    public static Optional<Identifier> getIdentifierForFile(File file) {
        if (fileCache.containsKey(file)) {
            return Optional.of(fileCache.get(file));
        }
        if (!file.exists() || !file.canRead()) {
            return Optional.empty();
        }

        try (InputStream inputStream = new FileInputStream(file)) {
            NativeImage nativeImage = loadImage(inputStream);
            NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);

            Identifier id = Identifier.of("weave", "dynamic/" + UUID.randomUUID());
            client.getTextureManager().registerTexture(id, texture);
            fileCache.put(file, id);
            return Optional.of(id);
        } catch (Exception e) {
            LOGGER.error("Failed to load image from file: {}", file.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    public static CompletableFuture<Identifier> getIdentifierForUrl(URL url) {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            LOGGER.error("Invalid URL syntax: {}", url, e);
            return CompletableFuture.failedFuture(e);
        }

        if (urlCache.containsKey(uri)) {
            return urlCache.get(uri);
        }

        CompletableFuture<Identifier> future = CompletableFuture.supplyAsync(() -> {
            try {
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; Weave-UI-Client/1.0)");
                try (InputStream inputStream = connection.getInputStream()) {
                    return loadImage(inputStream);
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to download or read image from URL: " + url, e);
            }
        }, VIRTUAL_THREAD_EXECUTOR).thenApplyAsync(nativeImage -> {
            NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
            Identifier id = Identifier.of("weave", "dynamic/" + UUID.randomUUID());
            client.getTextureManager().registerTexture(id, texture);
            return id;
        }, client);

        urlCache.put(uri, future);
        return future;
    }

    private static NativeImage loadImage(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        inputStream.transferTo(buffer);
        byte[] imageBytes = buffer.toByteArray();

        try {
            return NativeImage.read(new ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            LOGGER.debug("Could not read image as PNG, falling back to ImageIO.", e);
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (bufferedImage == null) {
                throw new IOException("Failed to read image using ImageIO. Unsupported format?");
            }
            return convertBufferedImageToNativeImage(bufferedImage);
        }
    }

    private static NativeImage convertBufferedImageToNativeImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        if (bufferedImage.getType() != BufferedImage.TYPE_INT_ARGB) {
            BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = tempImage.createGraphics();
            g.drawImage(bufferedImage, 0, 0, null);
            g.dispose();
            bufferedImage = tempImage;
        }

        int[] pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);

        NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
        for (int i = 0; i < pixels.length; i++) {
            nativeImage.setColorArgb(i % width, i / width, pixels[i]);
        }

        return nativeImage;
    }
}