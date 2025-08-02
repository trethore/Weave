package tytoo.weave.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.*;
import net.minecraft.util.Identifier;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.freetype.FT_Face;
import org.lwjgl.util.freetype.FreeType;
import tytoo.weave.WeaveClient;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public final class FontManager {
    private static final Map<String, TextRenderer> FONT_CACHE = new ConcurrentHashMap<>();
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private FontManager() {
    }

    public static Optional<TextRenderer> loadFromIdentifier(Identifier fontId, float size, float oversample) {
        String cacheKey = "id:" + fontId + ":" + size + ":" + oversample;
        if (FONT_CACHE.containsKey(cacheKey)) {
            return Optional.of(FONT_CACHE.get(cacheKey));
        }

        return client.getResourceManager().getResource(fontId).flatMap(resource -> {
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] bytes = inputStream.readAllBytes();
                return loadFromBytes(bytes, size, oversample, fontId, cacheKey);
            } catch (Exception e) {
                WeaveClient.LOGGER.error("Failed to load font from identifier: {}", fontId, e);
                return Optional.empty();
            }
        });
    }

    public static Optional<TextRenderer> loadFromFile(File fontFile, float size, float oversample) {
        if (!fontFile.exists() || !fontFile.canRead()) {
            WeaveClient.LOGGER.error("Font file does not exist or cannot be read: {}", fontFile.getAbsolutePath());
            return Optional.empty();
        }

        String cacheKey = "file:" + fontFile.getAbsolutePath() + ":" + size + ":" + oversample;
        if (FONT_CACHE.containsKey(cacheKey)) {
            return Optional.of(FONT_CACHE.get(cacheKey));
        }

        try {
            byte[] bytes = Files.readAllBytes(fontFile.toPath());
            Identifier dynamicId = Identifier.of("weave", "dynamic/font/" + fontFile.getName().toLowerCase().replaceAll("[^a-z0-9.\\-_]", "_"));
            return loadFromBytes(bytes, size, oversample, dynamicId, cacheKey);
        } catch (Exception e) {
            WeaveClient.LOGGER.error("Failed to load font from file: {}", fontFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    private static Optional<TextRenderer> loadFromBytes(byte[] fontBytes, float size, float oversample, Identifier fontIdForStorage, String cacheKey) {
        ByteBuffer byteBuffer = null;
        FT_Face face = null;
        try {
            byteBuffer = MemoryUtil.memAlloc(fontBytes.length).put(fontBytes).flip();

            try (MemoryStack memoryStack = MemoryStack.stackPush()) {
                PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
                synchronized (FreeTypeUtil.LOCK) {
                    long library = FreeTypeUtil.initialize();
                    FreeTypeUtil.checkFatalError(FreeType.FT_New_Memory_Face(library, byteBuffer, 0, pointerBuffer), "FT_New_Memory_Face");
                    face = FT_Face.create(pointerBuffer.get(0));
                }
            }

            Font font = new TrueTypeFont(byteBuffer, face, size, oversample, 0.0f, 0.0f, "");
            TextRenderer renderer = createTextRenderer(fontIdForStorage, font);
            FONT_CACHE.put(cacheKey, renderer);
            return Optional.of(renderer);
        } catch (Exception e) {
            WeaveClient.LOGGER.error("Failed to load font from bytes for: {}", fontIdForStorage, e);
            if (face != null) try {
                FreeType.FT_Done_Face(face);
            } catch (Exception ignored) {
            }
            if (byteBuffer != null) try {
                MemoryUtil.memFree(byteBuffer);
            } catch (Exception ignored) {
            }
            return Optional.empty();
        }
    }

    private static TextRenderer createTextRenderer(Identifier fontId, Font font) {
        FontStorage storage = new FontStorage(client.getTextureManager(), fontId);
        Font.FontFilterPair pair = new Font.FontFilterPair(font, new FontFilterType.FilterMap(Collections.emptyMap()));
        storage.setFonts(List.of(pair), Set.of());
        return new TextRenderer(id -> storage, true);
    }
}