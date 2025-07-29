package tytoo.weave.component.components.interactive;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effect;
import tytoo.weave.event.keyboard.CharTypeEvent;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.state.State;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TextField extends BasePanel<TextField> {
    private final List<Consumer<String>> textChangeListeners = new ArrayList<>();
    private String text = "";
    private int cursorPos = 0;

    public TextField() {
        this.setFocusable(true);
        this.setHeight(Constraints.pixels(20));
        this.setWidth(Constraints.pixels(150));
        this.setPadding(4);

        this.getStyle().setColor(new Color(20, 20, 20));
        this.addEffect(new Effect() {
            @Override
            public void afterDraw(DrawContext context, Component<?> component) {
                Color outlineColor = isFocused() ? new Color(160, 160, 160) : new Color(80, 80, 80);
                Render2DUtils.drawOutline(context, getLeft(), getTop(), getWidth(), getHeight(), 1.0f, outlineColor);
            }
        });

        this.onCharTyped(this::onCharTyped);
        this.onKeyPress(this::onKeyPressed);
    }

    public static TextField create() {
        return new TextField();
    }

    public void setText(String text) {
        if (text == null) text = "";
        if (this.text.equals(text)) return;

        this.text = text;
        this.cursorPos = Math.min(this.cursorPos, text.length());
        for (Consumer<String> listener : textChangeListeners) {
            listener.accept(this.text);
        }
    }

    public TextField bindText(State<String> state) {
        state.bind(this::setText);
        this.onTextChanged(newText -> {
            if (!state.get().equals(newText)) {
                state.set(newText);
            }
        });
        return this;
    }

    public void onTextChanged(Consumer<String> listener) {
        this.textChangeListeners.add(listener);
    }

    private void onCharTyped(CharTypeEvent event) {
        String newText = new StringBuilder(this.text).insert(this.cursorPos, event.getCharacter()).toString();
        this.setText(newText);
        this.cursorPos++;
    }

    private void onKeyPressed(KeyPressEvent event) {
        if (event.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE) {
            if (cursorPos > 0) {
                String newText = new StringBuilder(this.text).deleteCharAt(cursorPos - 1).toString();
                this.setText(newText);
                cursorPos--;
            }
        } else if (event.getKeyCode() == GLFW.GLFW_KEY_DELETE) {
            if (cursorPos < text.length()) {
                setText(new StringBuilder(text).deleteCharAt(cursorPos).toString());
            }
        } else if (event.getKeyCode() == GLFW.GLFW_KEY_LEFT) {
            cursorPos = Math.max(0, cursorPos - 1);
        } else if (event.getKeyCode() == GLFW.GLFW_KEY_RIGHT) {
            cursorPos = Math.min(text.length(), cursorPos + 1);
        } else if (event.getKeyCode() == GLFW.GLFW_KEY_HOME) {
            cursorPos = 0;
        } else if (event.getKeyCode() == GLFW.GLFW_KEY_END) {
            cursorPos = text.length();
        }
    }

    @Override
    public void draw(DrawContext context) {
        super.draw(context);

        var textRenderer = ThemeManager.getTheme().getTextRenderer();
        float textY = this.getInnerTop() + (this.getInnerHeight() - 8) / 2.0f;

        context.drawText(textRenderer, Text.of(this.text), (int) this.getInnerLeft(), (int) textY, -1, true);

        drawCursor(context, textRenderer, textY);
    }

    private void drawCursor(DrawContext context, TextRenderer textRenderer, float textY) {
        if (this.isFocused() && (System.currentTimeMillis() / 500) % 2 == 0) {
            String textBeforeCursor = this.text.substring(0, this.cursorPos);
            float cursorX = this.getInnerLeft() + textRenderer.getWidth(textBeforeCursor);
            Render2DUtils.drawRect(context, cursorX, textY - 1, 1, 10, Color.LIGHT_GRAY);
        }
    }
}