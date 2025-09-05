package tytoo.weave.component.components.interactive;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.event.mouse.MouseClickEvent;
import tytoo.weave.event.mouse.MouseDragEvent;
import tytoo.weave.event.mouse.MouseScrollEvent;
import tytoo.weave.style.CommonStyleProperties;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.InputHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TextArea extends BaseTextInput<TextArea> {
    private float scrollY = 0;
    private int lastDesiredCol = -1;

    protected TextArea() {
        super();
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        float defaultWidth = stylesheet.get(this, StyleProps.DEFAULT_WIDTH, 200f);
        float defaultHeight = stylesheet.get(this, StyleProps.DEFAULT_HEIGHT, 100f);

        this.setWidth(Constraints.pixels(defaultWidth));
        this.setHeight(Constraints.pixels(defaultHeight));

        this.onMouseClick(this::onMouseClick);
        this.onMouseScroll(this::onMouseScroll);
        this.onMouseDrag(this::onMouseDragged);
    }

    public static TextArea create() {
        return new TextArea();
    }

    private List<VisualLine> getVisualLines() {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        String t = getText();
        List<VisualLine> result = new ArrayList<>();

        int maxWidth = (int) getInnerWidth();
        if (t.isEmpty()) {
            result.add(new VisualLine("", 0, 0));
            return result;
        }

        int i = 0;
        while (i < t.length()) {
            int newline = t.indexOf('\n', i);
            int paraEnd = newline >= 0 ? newline : t.length();

            if (i == paraEnd) {
                result.add(new VisualLine("", i, i));
            } else if (maxWidth <= 0) {
                result.add(new VisualLine(t.substring(i, paraEnd), i, paraEnd));
            } else {
                int segStart = i;
                while (segStart < paraEnd) {
                    String remaining = t.substring(segStart, paraEnd);
                    String cut = textRenderer.trimToWidth(remaining, maxWidth);
                    int len = cut.length();
                    if (len == 0) {
                        len = Math.min(1, remaining.length());
                    }

                    int absoluteCutEnd = segStart + len;
                    boolean fullyFits = absoluteCutEnd >= paraEnd;

                    if (fullyFits) {
                        result.add(new VisualLine(t.substring(segStart, paraEnd), segStart, paraEnd));
                        segStart = paraEnd;
                    } else {
                        int lastBreak = -1;
                        for (int j = len - 1; j >= 0; j--) {
                            char ch = remaining.charAt(j);
                            if (Character.isWhitespace(ch)) {
                                lastBreak = j;
                                break;
                            }
                        }
                        if (lastBreak > 0) {
                            int lineEnd = segStart + lastBreak;
                            result.add(new VisualLine(t.substring(segStart, lineEnd), segStart, lineEnd));
                            segStart = lineEnd + 1;
                        } else {
                            result.add(new VisualLine(t.substring(segStart, absoluteCutEnd), segStart, absoluteCutEnd));
                            segStart = absoluteCutEnd;
                        }
                    }
                }
            }

            if (newline < 0) break;
            i = paraEnd + 1;
        }
        if (t.charAt(t.length() - 1) == '\n') {
            result.add(new VisualLine("", t.length(), t.length()));
        }
        return result;
    }

    public int getVisualLineCount() {
        return getVisualLines().size();
    }

    public String getVisualLineText(int index) {
        List<VisualLine> lines = getVisualLines();
        index = Math.max(0, Math.min(lines.size() - 1, index));
        return lines.get(index).text();
    }

    public int getVisualLineStartIndex(int index) {
        List<VisualLine> lines = getVisualLines();
        index = Math.max(0, Math.min(lines.size() - 1, index));
        return lines.get(index).startIndex();
    }

    public int getVisualLineEndIndex(int index) {
        List<VisualLine> lines = getVisualLines();
        index = Math.max(0, Math.min(lines.size() - 1, index));
        return lines.get(index).endIndex();
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

            List<VisualLine> lines = getVisualLines();
            float yOffset = getInnerTop() + scrollY + 1;
            for (int i = 0; i < lines.size(); i++) {
                float lineY = yOffset + i * lineHeight;
                if (lineY + lineHeight < getInnerTop() || lineY > getInnerTop() + getInnerHeight()) continue;

                float textY = lineY + 2;
                context.drawText(textRenderer, lines.get(i).text(), (int) getInnerLeft(), (int) textY, -1, true);
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
        boolean shift = InputHelper.isShiftDown();
        switch (event.getKeyCode()) {
            case GLFW.GLFW_KEY_UP: {
                Point pos2d = getCursorPos2D(getCursorPos());
                if (this.lastDesiredCol == -1) this.lastDesiredCol = pos2d.x;
                setCursorPos(getPosFrom2D(pos2d.y - 1, this.lastDesiredCol), shift);
                return true;
            }
            case GLFW.GLFW_KEY_DOWN: {
                Point pos2d = getCursorPos2D(getCursorPos());
                if (this.lastDesiredCol == -1) this.lastDesiredCol = pos2d.x;
                setCursorPos(getPosFrom2D(pos2d.y + 1, this.lastDesiredCol), shift);
                return true;
            }
            case GLFW.GLFW_KEY_LEFT:
                if (InputHelper.isControlDown()) {
                    setCursorPos(getWordSkipPosition(-1), shift);
                } else {
                    setCursorPos(getCursorPos() - 1, shift);
                }
                this.lastDesiredCol = -1;
                return true;
            case GLFW.GLFW_KEY_RIGHT:
                if (InputHelper.isControlDown()) {
                    setCursorPos(getWordSkipPosition(1), shift);
                } else {
                    setCursorPos(getCursorPos() + 1, shift);
                }
                this.lastDesiredCol = -1;
                return true;
            case GLFW.GLFW_KEY_HOME: {
                Point pos2d = getCursorPos2D(getCursorPos());
                setCursorPos(getPosFrom2D(pos2d.y, 0), shift);
                this.lastDesiredCol = -1;
                return true;
            }
            case GLFW.GLFW_KEY_END: {
                Point pos2d = getCursorPos2D(getCursorPos());
                String line = getVisualLineText(pos2d.y);
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

        int lineIndex = (int) ((event.getY() - (getInnerTop() + scrollY + 1)) / lineHeight);
        lineIndex = Math.max(0, Math.min(getVisualLineCount() - 1, lineIndex));

        String line = getVisualLineText(lineIndex);
        int colIndex = textRenderer.trimToWidth(line, (int) (event.getX() - getInnerLeft())).length();

        int newPos = getPosFrom2D(lineIndex, colIndex);

        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            setCursorPos(newPos, InputHelper.isShiftDown());
            this.lastDesiredCol = getCursorPos2D(newPos).x;
            return;
        }

        int count = registerClickAndGetCount();
        if (count == 1) {
            setCursorPos(newPos, InputHelper.isShiftDown());
            this.lastDesiredCol = getCursorPos2D(newPos).x;
        } else if (count == 2) {
            Point bounds = getWordBoundsAt(newPos);
            setSelectionAnchor(bounds.x);
            setCursorPos(bounds.y, true);
            this.lastDesiredCol = getCursorPos2D(bounds.y).x;
        } else if (count >= 3) {
            int lineStart = getPosFrom2D(lineIndex, 0);
            int lineEnd = getPosFrom2D(lineIndex, line.length());
            setSelectionAnchor(lineStart);
            setCursorPos(lineEnd, true);
            this.lastDesiredCol = getCursorPos2D(lineEnd).x;
        }
    }

    private void onMouseDragged(MouseDragEvent event) {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        float lineHeight = textRenderer.fontHeight + 1;

        int lineIndex = (int) ((event.getY() - (getInnerTop() + scrollY + 1)) / lineHeight);
        lineIndex = Math.max(0, Math.min(getVisualLineCount() - 1, lineIndex));

        String line = getVisualLineText(lineIndex);
        int colIndex = textRenderer.trimToWidth(line, (int) (event.getX() - getInnerLeft())).length();

        int newPos = getPosFrom2D(lineIndex, colIndex);
        setCursorPos(Math.max(0, Math.min(getText().length(), newPos)), true);
        setLastActionTime(System.currentTimeMillis());
        ensureCursorVisible();
    }

    private void onMouseScroll(MouseScrollEvent event) {
        float amount = ThemeManager.getStylesheet().get(this, CommonStyleProperties.SCROLL_AMOUNT, 10f);
        scrollY += (float) (event.getScrollY() * amount);
        clampScroll();
    }

    public Point getCursorPos2D(int pos) {
        List<VisualLine> lines = getVisualLines();
        if (pos <= 0) return new Point(0, 0);
        if (pos >= getText().length()) {
            VisualLine last = lines.getLast();
            return new Point(last.endIndex() - last.startIndex(), lines.size() - 1);
        }
        for (int i = 0; i < lines.size(); i++) {
            VisualLine vl = lines.get(i);
            if (pos <= vl.endIndex()) {
                int col = Math.max(0, pos - vl.startIndex());
                return new Point(col, i);
            }
        }
        VisualLine last = lines.getLast();
        return new Point(last.endIndex() - last.startIndex(), lines.size() - 1);
    }

    private int getPosFrom2D(int line, int col) {
        List<VisualLine> lines = getVisualLines();
        if (lines.isEmpty()) return 0;
        line = Math.max(0, Math.min(lines.size() - 1, line));
        VisualLine target = lines.get(line);
        int len = target.endIndex() - target.startIndex();
        col = Math.max(0, Math.min(len, col));
        return target.startIndex() + col;
    }

    protected void clampScroll() {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        float lineHeight = textRenderer.fontHeight + 1;
        float contentHeight = getVisualLines().size() * lineHeight;
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
        Point pos2d = getCursorPos2D(getCursorPos());
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

    private record VisualLine(String text, int startIndex, int endIndex) {
    }
}
