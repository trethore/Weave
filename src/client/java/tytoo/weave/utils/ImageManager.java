package tytoo.weave.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import tytoo.weave.WeaveClient;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class ImageManager {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final ConcurrentHashMap<File, Identifier> fileCache = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<URI, Identifier> urlToIdentifier = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<URI, CompletableFuture<Identifier>> urlOperations = new ConcurrentHashMap<>();
    private static final ExecutorService VIRTUAL_THREAD_EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private static Identifier PLACEHOLDER_ID;

    public static Identifier getPlaceholder() {
        if (PLACEHOLDER_ID == null) {
            int imageSize = 16;
            NativeImage image = new NativeImage(NativeImage.Format.RGBA, imageSize, imageSize, false);
            int magenta = 0xFFFF00FF;
            int black = 0xFF000000;

            for (int y = 0; y < imageSize; y++) {
                for (int x = 0; x < imageSize; x++) {
                    boolean isMagenta = ((x / 8) + (y / 8)) % 2 == 0;
                    image.setColorArgb(x, y, isMagenta ? magenta : black);
                }
            }

            NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
            texture.setFilter(false, false);
            PLACEHOLDER_ID = Identifier.of(WeaveClient.MOD_ID, "missing.png");
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

            Identifier id = Identifier.of(WeaveClient.MOD_ID, "dynamic/" + UUID.randomUUID());
            client.getTextureManager().registerTexture(id, texture);
            fileCache.put(file, id);
            return Optional.of(id);
        } catch (Exception e) {
            WeaveClient.LOGGER.error("Failed to load image from file: {}", file.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    public static CompletableFuture<Identifier> getIdentifierForUrl(URL url) {
        return getOrFetch(url, false);
    }

    public static CompletableFuture<Identifier> forceFetchIdentifierForUrl(URL url) {
        return getOrFetch(url, true);
    }

    private static CompletableFuture<Identifier> getOrFetch(URL url, boolean force) {
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            WeaveClient.LOGGER.error("Invalid URL syntax: {}", url, e);
            return CompletableFuture.failedFuture(e);
        }

        if (!force) {
            CompletableFuture<Identifier> existingFuture = urlOperations.get(uri);
            if (existingFuture != null) {
                return existingFuture;
            }
        }

        CompletableFuture<Identifier> future = CompletableFuture.supplyAsync(() -> {
            try {
                URLConnection connection = url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                try (InputStream inputStream = connection.getInputStream()) {
                    return loadImage(inputStream);
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to download or read image from URL: " + url, e);
            }
        }, VIRTUAL_THREAD_EXECUTOR).thenApplyAsync(nativeImage -> {
            Identifier id = urlToIdentifier.computeIfAbsent(uri, u -> Identifier.of(WeaveClient.MOD_ID, "url/" + UUID.randomUUID()));
            NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
            client.getTextureManager().registerTexture(id, texture);
            return id;
        }, client);

        urlOperations.put(uri, future);
        return future;
    }

    private static NativeImage loadImage(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        inputStream.transferTo(buffer);
        byte[] imageBytes = buffer.toByteArray();

        try {
            return NativeImage.read(new ByteArrayInputStream(imageBytes));
        } catch (IOException e) {
            WeaveClient.LOGGER.debug("Could not read image as PNG, falling back to ImageIO. Reason: {}", e.getMessage());
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