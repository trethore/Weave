package tytoo.weave.component.components.interactive;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.event.mouse.MouseClickEvent;
import tytoo.weave.event.mouse.MouseDragEvent;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.ui.shortcuts.ShortcutRegistry;
import tytoo.weave.utils.InputHelper;

import java.awt.*;

public class TextField extends BaseTextInput<TextField> {
    private int firstCharacterIndex = 0;

    protected TextField() {
        super();
        Stylesheet stylesheet = ThemeManager.getStylesheet();
        float defaultWidth = stylesheet.get(this, StyleProps.DEFAULT_WIDTH, 150f);
        float defaultHeight = stylesheet.get(this, StyleProps.DEFAULT_HEIGHT, 20f);

        this.setWidth(Constraints.pixels(defaultWidth));
        this.setHeight(Constraints.pixels(defaultHeight));

        this.onMouseClick(this::onMouseClick);
        this.onMouseDrag(this::onMouseDragged);
    }

    public static TextField create() {
        return new TextField();
    }

    @Override
    public TextField setText(String text) {
        if (text == null) text = "";
        if (this.getText().equals(text)) return self();
        internalSetText(text);
        setCursorPos(Math.min(getCursorPos(), text.length()), false);
        return self();
    }

    public int getFirstCharacterIndex() {
        return firstCharacterIndex;
    }


    public boolean hasSelection() {
        return getCursorPos() != getSelectionAnchor();
    }

    @Override
    public void draw(DrawContext context) {
        super.draw(context);

        TextRenderer textRenderer = getEffectiveTextRenderer();
        float textY = this.getInnerTop() + (this.getInnerHeight() - (textRenderer.fontHeight - 1)) / 2.0f + 1f;

        boolean hasText = !getText().isEmpty();
        if (hasText || isFocused()) {
            getSelectionRenderer().render(context, this);

            String visibleText = textRenderer.trimToWidth(getText().substring(this.firstCharacterIndex), (int) this.getInnerWidth());
            if (!visibleText.isEmpty()) {
                context.drawText(textRenderer, Text.of(visibleText), (int) this.getInnerLeft(), (int) textY, -1, true);
            }

            getCursorRenderer().render(context, this);
        } else {
            getPlaceholderRenderer().render(context, this);
        }
    }

    public TextField setPlaceholder(String placeholder) {
        return setPlaceholder(placeholder == null ? null : Text.of(placeholder));
    }

    protected void onMouseClick(MouseClickEvent event) {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        String visibleText = getText().substring(this.firstCharacterIndex);
        int i = (int) (event.getX() - this.getInnerLeft());
        int pos = this.firstCharacterIndex + textRenderer.trimToWidth(visibleText, i).length();

        if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            setCursorPos(pos, InputHelper.isShiftDown());
            return;
        }

        int count = registerClickAndGetCount();
        if (count == 1) {
            setCursorPos(pos, InputHelper.isShiftDown());
        } else if (count == 2) {
            Point bounds = getWordBoundsAt(pos);
            setSelectionAnchor(bounds.x);
            setCursorPos(bounds.y, true);
        } else if (count >= 3) {
            setSelectionAnchor(0);
            setCursorPos(getText().length(), true);
        }
    }

    private void onMouseDragged(MouseDragEvent event) {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        String visibleText = getText().substring(this.firstCharacterIndex);
        int i = (int) (event.getX() - this.getInnerLeft());
        setCursorPos(this.firstCharacterIndex + textRenderer.trimToWidth(visibleText, i).length());
        setLastActionTime(System.currentTimeMillis());
        ensureCursorVisible();
    }

    @Override
    protected void ensureCursorVisible() {
        TextRenderer textRenderer = getEffectiveTextRenderer();
        int innerWidth = (int) getInnerWidth();

        if (getCursorPos() < this.firstCharacterIndex) {
            this.firstCharacterIndex = getCursorPos();
        }

        String visibleText = textRenderer.trimToWidth(getText().substring(this.firstCharacterIndex), innerWidth);
        if (getCursorPos() > this.firstCharacterIndex + visibleText.length()) {
            this.firstCharacterIndex = getCursorPos() - visibleText.length();
        }
    }

    @Override
    protected void registerNavigationShortcuts() {
        registerComponentShortcut(input -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_LEFT),
                ctx -> input.moveCursorHorizontal(-1, false, false)));

        registerComponentShortcut(input -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_LEFT).withModifiers(ShortcutRegistry.KeyChord.Modifier.SHIFT),
                ctx -> input.moveCursorHorizontal(-1, false, true)));

        registerComponentShortcut(input -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_LEFT).withModifiers(ShortcutRegistry.KeyChord.Modifier.CONTROL),
                ctx -> input.moveCursorHorizontal(-1, true, false)));

        registerComponentShortcut(input -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_LEFT)
                        .withModifiers(ShortcutRegistry.KeyChord.Modifier.CONTROL, ShortcutRegistry.KeyChord.Modifier.SHIFT),
                ctx -> input.moveCursorHorizontal(-1, true, true)));

        registerComponentShortcut(input -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_RIGHT),
                ctx -> input.moveCursorHorizontal(1, false, false)));

        registerComponentShortcut(input -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_RIGHT).withModifiers(ShortcutRegistry.KeyChord.Modifier.SHIFT),
                ctx -> input.moveCursorHorizontal(1, false, true)));

        registerComponentShortcut(input -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_RIGHT).withModifiers(ShortcutRegistry.KeyChord.Modifier.CONTROL),
                ctx -> input.moveCursorHorizontal(1, true, false)));

        registerComponentShortcut(input -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_RIGHT)
                        .withModifiers(ShortcutRegistry.KeyChord.Modifier.CONTROL, ShortcutRegistry.KeyChord.Modifier.SHIFT),
                ctx -> input.moveCursorHorizontal(1, true, true)));

        registerComponentShortcut(input -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_HOME),
                ctx -> {
                    input.setCursorPos(0, false);
                    return true;
                }));

        registerComponentShortcut(input -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_HOME).withModifiers(ShortcutRegistry.KeyChord.Modifier.SHIFT),
                ctx -> {
                    input.setCursorPos(0, true);
                    return true;
                }));

        registerComponentShortcut(input -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_END),
                ctx -> {
                    input.setCursorPos(input.getText().length(), false);
                    return true;
                }));

        registerComponentShortcut(input -> ShortcutRegistry.Shortcut.of(
                ShortcutRegistry.KeyChord.of(GLFW.GLFW_KEY_END).withModifiers(ShortcutRegistry.KeyChord.Modifier.SHIFT),
                ctx -> {
                    input.setCursorPos(input.getText().length(), true);
                    return true;
                }));
    }

    private boolean moveCursorHorizontal(int direction, boolean byWord, boolean extendSelection) {
        int target = byWord ? super.getWordSkipPosition(direction) : getCursorPos() + direction;
        setCursorPos(target, extendSelection);
        return true;
    }

}
