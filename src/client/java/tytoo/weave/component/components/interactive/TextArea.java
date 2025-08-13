package tytoo.weave.component.components.interactive;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.event.mouse.MouseClickEvent;
import tytoo.weave.event.mouse.MouseDragEvent;
import tytoo.weave.event.mouse.MouseScrollEvent;

import java.util.Arrays;
import java.util.List;

public class TextArea extends BaseTextInput<TextArea> {
    private float scrollY = 0;
    private int lastDesiredCol = -1;

    protected TextArea() {
        super();
        this.setHeight(Constraints.pixels(100));
        this.setWidth(Constraints.pixels(200));

        this.onMouseClick(this::onMouseClick);
        this.onMouseScroll(this::onMouseScroll);
        this.onMouseDrag(this::onMouseDragged);
    }

    public static TextArea create() {
        return new TextArea();
    }

    public List<String> getLines() {
        return Arrays.asList(getText().split("\n", -1));
    }

    @Override
    public void draw(DrawContext context) {
        super.draw(context);

        context.getMatrices().push();
        context.enableScissor((int) getInnerLeft(), (int) getInnerTop(), (int) (getInnerLeft() + getInnerWidth()), (int) (getInnerTop() + getInnerHeight()));

        TextRenderer textRenderer = getEffectiveTextRenderer();
        int fontHeight = textRenderer.fontHeight;
        float lineHeight = fontHeight + 1;

        if (getText().isEmpty() && !isFocused() && getPlaceholder() != null) {
            getPlaceholderRenderer().render(context, this);
        } else {
            getSelectionRenderer().render(context, this);

            List<String> lines = getLines();
            float yOffset = getInnerTop() + scrollY + 1;
            for (int i = 0; i < lines.size(); i++) {
                float lineY = yOffset + i * lineHeight;
                if (lineY + lineHeight < getInnerTop() || lineY > getInnerTop() + getInnerHeight()) continue;

                float textY = lineY + 2;
                context.drawText(textRenderer, lines.get(i), (int) getInnerLeft(), (int) textY, -1, true);
            }

            getCursorRenderer().render(context, this);
        }

        context.disableScissor();
        context.getMatrices().pop();
    }

    @Override
    public void write(String newText) {
        super.write(newText);
        this.lastDesiredCol = -1;
    }

    @Override
    protected boolean handleNavigation(KeyPressEvent event) {
        boolean shift = Screen.hasShiftDown();
        switch (event.getKeyCode()) {
            case GLFW.GLFW_KEY_UP: {
                java.awt.Point pos2d = getCursorPos2D(getCursorPos());
                if (this.lastDesiredCol == -1) this.lastDesiredCol = pos2d.x;
                setCursorPos(getPosFrom2D(pos2d.y - 1, this.lastDesiredCol), shift);
                return true;
            }
            case GLFW.GLFW_KEY_DOWN: {
                java.awt.Point pos2d = getCursorPos2D(getCursorPos());
                if (this.lastDesiredCol == -1) this.lastDesiredCol = pos2d.x;
                setCursorPos(getPosFrom2D(pos2d.y + 1, this.lastDesiredCol), shift);
                return true;
            }
            case GLFW.GLFW_KEY_LEFT:
                if (Screen.hasControlDown()) {
                    setCursorPos(getWordSkipPosition(-1), shift);
                } else {
                    setCursorPos(getCursorPos() - 1, shift);
                }
                this.lastDesiredCol = -1;
                return true;
            case GLFW.GLFW_KEY_RIGHT:
                if (Screen.hasControlDown()) {
                    setCursorPos(getWordSkipPosition(1), shift);
                } else {
                    setCursorPos(getCursorPos() + 1, shift);
                }
                this.lastDesiredCol = -1;
                return true;
            case GLFW.GLFW_KEY_HOME: {
                java.awt.Point pos2d = getCursorPos2D(getCursorPos());
                setCursorPos(getPosFrom2D(pos2d.y, 0), shift);
                this.lastDesiredCol = -1;
                return true;
            }
            case GLFW.GLFW_KEY_END: {
                java.awt.Point pos2d = getCursorPos2D(getCursorPos());
                String line = getLines().get(pos2d.y);
                setCursorPos(getPosFrom2D(pos2d.y, line.length()), shift);
                this.lastDesiredCol = -1;
                return true;
            }
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER:
                write("\n");
                this.lastDesiredCol = -1;
                return true;
        }
        return false;
    }

    private void onMouseClick(MouseClickEvent event) {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        float lineHeight = textRenderer.fontHeight + 1;

        List<String> lines = getLines();
        int lineIndex = (int) ((event.getY() - (getInnerTop() + scrollY + 1)) / lineHeight);
        lineIndex = Math.max(0, Math.min(lines.size() - 1, lineIndex));

        String line = lines.get(lineIndex);
        int colIndex = textRenderer.trimToWidth(line, (int) (event.getX() - getInnerLeft())).length();

        int newPos = getPosFrom2D(lineIndex, colIndex);
        setCursorPos(newPos, Screen.hasShiftDown());
        this.lastDesiredCol = getCursorPos2D(newPos).x;
    }

    private void onMouseDragged(MouseDragEvent event) {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        float lineHeight = textRenderer.fontHeight + 1;

        List<String> lines = getLines();
        int lineIndex = (int) ((event.getY() - (getInnerTop() + scrollY + 1)) / lineHeight);
        lineIndex = Math.max(0, Math.min(lines.size() - 1, lineIndex));

        String line = lines.get(lineIndex);
        int colIndex = textRenderer.trimToWidth(line, (int) (event.getX() - getInnerLeft())).length();

        int newPos = getPosFrom2D(lineIndex, colIndex);
        setCursorPos(Math.max(0, Math.min(getText().length(), newPos)), true);
        setLastActionTime(System.currentTimeMillis());
        ensureCursorVisible();
    }

    private void onMouseScroll(MouseScrollEvent event) {
        scrollY += (float) (event.getScrollY() * 10);
        clampScroll();
    }

    public java.awt.Point getCursorPos2D(int pos) {
        int charCount = 0;
        List<String> lines = getLines();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (pos <= charCount + line.length()) {
                return new java.awt.Point(pos - charCount, i);
            }
            charCount += line.length() + 1;
        }
        String lastLine = lines.getLast();
        return new java.awt.Point(lastLine.length(), lines.size() - 1);
    }

    private int getPosFrom2D(int line, int col) {
        List<String> lines = getLines();
        if (line >= lines.size()) {
            return getText().length();
        }
        line = Math.max(0, Math.min(lines.size() - 1, line));
        String targetLine = lines.get(line);
        col = Math.max(0, Math.min(targetLine.length(), col));

        int charCount = 0;
        for (int i = 0; i < line; i++) {
            charCount += lines.get(i).length() + 1;
        }
        return charCount + col;
    }

    protected void clampScroll() {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        float lineHeight = textRenderer.fontHeight + 1;
        float contentHeight = getLines().size() * lineHeight;
        float viewHeight = getInnerHeight();

        if (contentHeight <= viewHeight) {
            scrollY = 0;
        } else {
            float maxScroll = contentHeight - viewHeight;
            scrollY = Math.max(-maxScroll, Math.min(0, scrollY));
        }
    }

    @Override
    protected void ensureCursorVisible() {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        float lineHeight = textRenderer.fontHeight + 1;
        java.awt.Point pos2d = getCursorPos2D(getCursorPos());
        float cursorY = pos2d.y * lineHeight;
        if (cursorY + scrollY < 0) {
            scrollY = -cursorY;
        }
        float cursorYBottom = cursorY + lineHeight;
        if (cursorYBottom + scrollY > getInnerHeight()) {
            scrollY = getInnerHeight() - cursorYBottom;
        }
        clampScroll();
    }

    @Override
    public TextArea setText(String text) {
        if (text == null) text = "";
        if (getText().equals(text)) return self();
        internalSetText(text);
        setCursorPos(Math.min(getCursorPos(), text.length()), false);
        setSelectionAnchor(this.getCursorPos());
        clampScroll();
        return self();
    }

    @Override
    public void arrange(float x, float y) {
        super.arrange(x, y);
        clampScroll();
        ensureCursorVisible();
    }

    public float getScrollY() {
        return scrollY;
    }
}