package tytoo.weave.ui;

import org.lwjgl.glfw.GLFW;
import tytoo.weave.utils.McUtils;

import java.util.EnumMap;
import java.util.Map;

public final class CursorManager {
    private static final Map<CursorType, Long> CURSOR_CACHE = new EnumMap<>(CursorType.class);
    private static CursorType currentCursor = CursorType.ARROW;

    private CursorManager() {
    }

    public static void setCursor(CursorType type) {
        if (type == currentCursor) return;

        McUtils.getMc().ifPresent(client -> {
            long windowHandle = client.getWindow().getHandle();
            long cursorHandle = CURSOR_CACHE.computeIfAbsent(type, t -> GLFW.glfwCreateStandardCursor(t.getGlfwShape()));
            GLFW.glfwSetCursor(windowHandle, cursorHandle);
            currentCursor = type;
        });
    }

    public static void destroy() {
        for (long handle : CURSOR_CACHE.values()) {
            GLFW.glfwDestroyCursor(handle);
        }
        CURSOR_CACHE.clear();
    }
}