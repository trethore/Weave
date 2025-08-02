package tytoo.weave.component.components.interactive;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.event.mouse.MouseClickEvent;
import tytoo.weave.event.mouse.MouseDragEvent;
import tytoo.weave.style.renderer.textfield.CursorRenderer;
import tytoo.weave.style.renderer.textfield.DefaultCursorRenderer;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;

public class TextField extends BaseTextInput<TextField> {
    private int firstCharacterIndex = 0;
    private CursorRenderer cursorRenderer = new DefaultCursorRenderer();

    protected TextField() {
        super();
        this.setHeight(Constraints.pixels(20));
        this.setWidth(Constraints.pixels(150));

        this.onMouseClick(this::onMouseClick);
        this.onMouseDrag(this::onMouseDragged);
    }

    public static TextField create() {
        return new TextField();
    }

    public String getText() {
        return this.text;
    }

    @Override
    public TextField setText(String text) {
        if (text == null) text = "";
        if (this.text.equals(text)) return self();
        internalSetText(text);
        setCursorPos(Math.min(this.cursorPos, text.length()), false);
        return self();
    }

    public int getCursorPos() {
        return cursorPos;
    }

    public int getFirstCharacterIndex() {
        return firstCharacterIndex;
    }

    public boolean hasSelection() {
        return this.cursorPos != selectionAnchor;
    }

    public CursorRenderer getCursorRenderer() {
        return cursorRenderer;
    }

    public TextField setCursorRenderer(CursorRenderer cursorRenderer) {
        this.cursorRenderer = cursorRenderer;
        return this;
    }

    public TextField setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (this.maxLength > 0 && this.text.length() > this.maxLength) {
            setText(this.text.substring(0, this.maxLength));
        }
        return this;
    }

    @Override
    public void draw(DrawContext context) {
        super.draw(context);

        TextRenderer textRenderer = getEffectiveTextRenderer();
        int fontHeight = textRenderer.fontHeight;
        float textY = this.getInnerTop() + (this.getInnerHeight() - (fontHeight - 1)) / 2.0f + 1f;

        boolean hasText = !text.isEmpty();
        if (hasText || isFocused()) {
            String visibleText = textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), (int) this.getInnerWidth());

            int selectionStart = Math.min(this.cursorPos, this.selectionAnchor);
            int selectionEnd = Math.max(this.cursorPos, this.selectionAnchor);
            if (selectionStart != selectionEnd) {
                int visibleSelectionStart = Math.max(0, selectionStart - this.firstCharacterIndex);
                int visibleSelectionEnd = Math.min(visibleText.length(), selectionEnd - this.firstCharacterIndex);

                if (visibleSelectionStart < visibleSelectionEnd) {
                    String preSelection = visibleText.substring(0, visibleSelectionStart);
                    float highlightX1 = this.getInnerLeft() + textRenderer.getWidth(preSelection);

                    String selected = visibleText.substring(visibleSelectionStart, visibleSelectionEnd);
                    float highlightX2 = highlightX1 + textRenderer.getWidth(selected);
                    Color selectionColor = ThemeManager.getStylesheet().get(this.getClass(), StyleProps.SELECTION_COLOR, new Color(50, 100, 200, 128));

                    float highlightY = textY - 2;
                    float highlightHeight = fontHeight + 1;
                    if (selectionColor != null) {
                        Render2DUtils.drawRect(context, highlightX1, highlightY, highlightX2 - highlightX1, highlightHeight, selectionColor);
                    }
                }
            }

            if (!visibleText.isEmpty()) {
                context.drawText(textRenderer, Text.of(visibleText), (int) this.getInnerLeft(), (int) textY, -1, true);
            }

            if (this.cursorRenderer != null) {
                this.cursorRenderer.render(context, this);
            }

        } else {
            if (this.placeholder != null) {
                Color placeholderColor = ThemeManager.getStylesheet().get(this.getClass(), StyleProps.PLACEHOLDER_COLOR, new Color(150, 150, 150));
                if (placeholderColor != null) {
                    context.drawText(textRenderer, this.placeholder, (int) this.getInnerLeft(), (int) textY, placeholderColor.getRGB(), true);
                }
            }
        }
    }

    @Override
    protected void setCursorPos(int pos, boolean shiftPressed) {
        super.setCursorPos(pos, shiftPressed);
    }

    public TextField setPlaceholder(String placeholder) {
        return setPlaceholder(Text.of(placeholder));
    }

    public TextField setPlaceholder(@Nullable Text placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    protected void onMouseClick(MouseClickEvent event) {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        String visibleText = this.text.substring(this.firstCharacterIndex);
        int i = (int) (event.getX() - this.getInnerLeft());
        setCursorPos(this.firstCharacterIndex + textRenderer.trimToWidth(visibleText, i).length(), Screen.hasShiftDown());
    }

    private void onMouseDragged(MouseDragEvent event) {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        String visibleText = this.text.substring(this.firstCharacterIndex);
        int i = (int) (event.getX() - this.getInnerLeft());
        this.cursorPos = this.firstCharacterIndex + textRenderer.trimToWidth(visibleText, i).length();
        this.lastActionTime = System.currentTimeMillis();
        ensureCursorVisible();
    }

    @Override
    protected void ensureCursorVisible() {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        int innerWidth = (int) getInnerWidth();

        if (this.cursorPos < this.firstCharacterIndex) {
            this.firstCharacterIndex = this.cursorPos;
        }

        String visibleText = textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), innerWidth);
        if (this.cursorPos > this.firstCharacterIndex + visibleText.length()) {
            this.firstCharacterIndex = this.cursorPos - visibleText.length();
        }
    }

    @Override
    protected boolean handleNavigation(KeyPressEvent event) {
        boolean shift = Screen.hasShiftDown();
        switch (event.getKeyCode()) {
            case GLFW.GLFW_KEY_LEFT -> {
                if (Screen.hasControlDown()) {
                    setCursorPos(super.getWordSkipPosition(-1), shift);
                } else {
                    setCursorPos(cursorPos - 1, shift);
                }
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                if (Screen.hasControlDown()) {
                    setCursorPos(super.getWordSkipPosition(1), shift);
                } else {
                    setCursorPos(cursorPos + 1, shift);
                }
                return true;
            }
            case GLFW.GLFW_KEY_HOME -> {
                setCursorPos(0, shift);
                return true;
            }
            case GLFW.GLFW_KEY_END -> {
                setCursorPos(text.length(), shift);
                return true;
            }
        }
        return false;
    }

}