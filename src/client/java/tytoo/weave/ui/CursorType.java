package tytoo.weave.ui;

import org.lwjgl.glfw.GLFW;

@SuppressWarnings("unused")
public enum CursorType {
    ARROW(GLFW.GLFW_ARROW_CURSOR),
    HAND(GLFW.GLFW_HAND_CURSOR),
    I_BEAM(GLFW.GLFW_IBEAM_CURSOR),
    EW_RESIZE(GLFW.GLFW_HRESIZE_CURSOR),
    NS_RESIZE(GLFW.GLFW_VRESIZE_CURSOR),
    CROSSHAIR(GLFW.GLFW_CROSSHAIR_CURSOR),
    NOT_ALLOWED(GLFW.GLFW_NOT_ALLOWED_CURSOR);

    private final int glfwShape;

    CursorType(int glfwShape) {
        this.glfwShape = glfwShape;
    }

    public int getGlfwShape() {
        return glfwShape;
    }
}
