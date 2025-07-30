package tytoo.weave.component.components.interactive;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.effects.Effect;
import tytoo.weave.event.keyboard.CharTypeEvent;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.event.mouse.MouseClickEvent;
import tytoo.weave.state.State;
import tytoo.weave.utils.InputHelper;
import tytoo.weave.utils.render.Render2DUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class TextField extends BasePanel<TextField> {
    private static final long CURSOR_BLINK_INTERVAL_MS = 500;
    private final List<Consumer<String>> textChangeListeners = new ArrayList<>();
    private final State<ValidationState> validationState = new State<>(ValidationState.NEUTRAL);
    public Color selectionColor = new Color(50, 100, 200, 128);
    public Color validBorderColor = new Color(0, 180, 0);
    public Color invalidBorderColor = new Color(180, 0, 0);
    public Color focusedBorderColor = new Color(160, 160, 160);
    public Color unfocusedBorderColor = new Color(80, 80, 80);
    public Color placeholderColor = new Color(150, 150, 150);
    private String text = "";
    private int cursorPos = 0;
    private int selectionAnchor = 0;
    private int firstCharacterIndex = 0;
    private long lastActionTime = 0;
    private int maxLength = -1;
    @Nullable
    private Predicate<String> charFilter = null;
    @Nullable
    private Predicate<String> validator = null;
    @Nullable
    private Text placeholder = null;

    public TextField() {
        this.setFocusable(true);
        this.setHeight(Constraints.pixels(20));
        this.setWidth(Constraints.pixels(150));
        this.setPadding(4);

        this.getStyle().setColor(new Color(20, 20, 20));

        Effect outlineEffect = new Effect() {
            @Override
            public void afterDraw(DrawContext context, Component<?> component) {
                Color outlineColor = getCurrentOutlineColor();
                Render2DUtils.drawOutline(context, getLeft(), getTop(), getWidth(), getHeight(), 1.0f, outlineColor);
            }
        };

        this.onFocusGained(e -> this.lastActionTime = System.currentTimeMillis());
        this.onMouseClick(this::onMouseClick);
        this.onCharTyped(this::onCharTyped);
        this.onKeyPress(this::onKeyPressed);

        this.addEffect(outlineEffect);
    }

    public static TextField create() {
        return new TextField();
    }

    private Color getCurrentOutlineColor() {
        return switch (validationState.get()) {
            case VALID -> validBorderColor;
            case INVALID -> invalidBorderColor;
            default -> isFocused() ? focusedBorderColor : unfocusedBorderColor;
        };
    }

    public TextField setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (this.maxLength > 0 && this.text.length() > this.maxLength) {
            setText(this.text.substring(0, this.maxLength));
        }
        return this;
    }

    public TextField setCharFilter(@Nullable Predicate<String> charFilter) {
        this.charFilter = charFilter;
        return this;
    }

    public TextField setCharFilter(String regex) {
        if (regex == null) {
            this.charFilter = null;
            return this;
        }
        Pattern pattern = Pattern.compile(regex);
        return setCharFilter(s -> pattern.matcher(s).matches());
    }

    public TextField setValidator(@Nullable Predicate<String> validator) {
        this.validator = validator;
        validate();
        return this;
    }

    public TextField setValidator(@Nullable String regex) {
        if (regex == null) {
            this.validator = null;
            this.validationState.set(ValidationState.NEUTRAL);
            return this;
        }
        Pattern pattern = Pattern.compile(regex);
        return setValidator(s -> pattern.matcher(s).matches());
    }

    public TextField setSelectionColor(Color selectionColor) {
        this.selectionColor = selectionColor;
        return this;
    }

    public TextField setValidBorderColor(Color validBorderColor) {
        this.validBorderColor = validBorderColor;
        return this;
    }

    public TextField setInvalidBorderColor(Color invalidBorderColor) {
        this.invalidBorderColor = invalidBorderColor;
        return this;
    }

    public TextField setFocusedBorderColor(Color focusedBorderColor) {
        this.focusedBorderColor = focusedBorderColor;
        return this;
    }

    public TextField setUnfocusedBorderColor(Color unfocusedBorderColor) {
        this.unfocusedBorderColor = unfocusedBorderColor;
        return this;
    }

    public TextField setPlaceholder(String placeholder) {
        return setPlaceholder(Text.of(placeholder));
    }

    public TextField setPlaceholder(@Nullable Text placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public TextField setPlaceholderColor(Color color) {
        this.placeholderColor = color;
        return this;
    }

    public State<ValidationState> getValidationState() {
        return this.validationState;
    }

    public void setText(String text) {
        if (text == null) text = "";
        if (this.text.equals(text)) return;
        this.text = text;
        this.cursorPos = Math.min(this.cursorPos, text.length());
        updateFirstCharacterIndex();
        notifyListeners();
    }

    private void internalSetText(String text) {
        if (text == null) text = "";
        if (this.text.equals(text)) return;
        this.text = text;
        validate();
        notifyListeners();
    }

    private void validate() {
        if (this.validator == null) {
            this.validationState.set(ValidationState.NEUTRAL);
            return;
        }
        this.validationState.set(this.validator.test(this.text) ? ValidationState.VALID : ValidationState.INVALID);
    }

    private void notifyListeners() {
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

    public TextField onTextChanged(Consumer<String> listener) {
        this.textChangeListeners.add(listener);
        return this;
    }

    private void onCharTyped(CharTypeEvent event) {
        write(Character.toString(event.getCharacter()));
    }

    private void onKeyPressed(KeyPressEvent event) {
        if (InputHelper.isSelectAll()) {
            this.selectionAnchor = 0;
            this.cursorPos = this.text.length();
            this.lastActionTime = System.currentTimeMillis();
            updateFirstCharacterIndex();
            event.cancel();
            return;
        }

        if (InputHelper.isCopy()) {
            MinecraftClient.getInstance().keyboard.setClipboard(getSelectedText());
            event.cancel();
            return;
        }

        if (InputHelper.isPaste()) {
            write(MinecraftClient.getInstance().keyboard.getClipboard());
            event.cancel();
            return;
        }

        if (InputHelper.isCut()) {
            MinecraftClient.getInstance().keyboard.setClipboard(getSelectedText());
            write("");
            event.cancel();
            return;
        }

        boolean shift = InputHelper.isShift();
        if (handleNavigationKeys(event, shift)) {
            event.cancel();
            return;
        }

        if (this.selectionAnchor != this.cursorPos) {
            if (event.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE || event.getKeyCode() == GLFW.GLFW_KEY_DELETE) {
                write("");
                event.cancel();
                return;
            }
        }

        handleDeletion(event);
    }

    @Override
    public void draw(DrawContext context) {
        super.draw(context);

        var textRenderer = getEffectiveTextRenderer();
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

                    float highlightY = textY - 2;
                    float highlightHeight = fontHeight + 1;
                    Render2DUtils.drawRect(context, highlightX1, highlightY, highlightX2 - highlightX1, highlightHeight, this.selectionColor);
                }
            }

            if (!visibleText.isEmpty()) {
                context.drawText(textRenderer, Text.of(visibleText), (int) this.getInnerLeft(), (int) textY, -1, true);
            }

            drawCursor(context, textRenderer, textY);

        } else {
            if (this.placeholder != null) {
                context.drawText(textRenderer, this.placeholder, (int) this.getInnerLeft(), (int) textY, this.placeholderColor.getRGB(), true);
            }
        }
    }

    private void drawCursor(DrawContext context, TextRenderer textRenderer, float textY) {
        long timeSinceLastAction = System.currentTimeMillis() - this.lastActionTime;
        boolean shouldBlink = (System.currentTimeMillis() / CURSOR_BLINK_INTERVAL_MS) % 2 == 0;
        boolean hasSelection = this.cursorPos != this.selectionAnchor;

        if (!hasSelection && this.isFocused() && (timeSinceLastAction < CURSOR_BLINK_INTERVAL_MS || shouldBlink)) {
            String visibleText = textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), (int) getInnerWidth());
            int visibleCursorPos = this.cursorPos - this.firstCharacterIndex;

            if (visibleCursorPos < 0 || visibleCursorPos > visibleText.length()) return;

            String textBeforeCursor = visibleText.substring(0, visibleCursorPos);
            float cursorX = this.getInnerLeft() + textRenderer.getWidth(textBeforeCursor);
            float cursorY = textY - 2;
            float cursorHeight = textRenderer.fontHeight + 1;
            Render2DUtils.drawRect(context, cursorX, cursorY, 1, cursorHeight, Color.LIGHT_GRAY);
        }
    }

    private void setCursorPos(int pos, boolean shiftPressed) {
        this.cursorPos = Math.max(0, Math.min(this.text.length(), pos));
        if (!shiftPressed) {
            this.selectionAnchor = this.cursorPos;
        }
        this.lastActionTime = System.currentTimeMillis();
        updateFirstCharacterIndex();
    }

    public String getSelectedText() {
        int start = Math.min(this.cursorPos, this.selectionAnchor);
        int end = Math.max(this.cursorPos, this.selectionAnchor);
        return this.text.substring(start, end);
    }

    public void write(String newText) {
        if (this.charFilter != null) {
            StringBuilder filteredText = new StringBuilder();
            for (char c : newText.toCharArray()) {
                if (this.charFilter.test(String.valueOf(c))) {
                    filteredText.append(c);
                }
            }
            newText = filteredText.toString();
        }

        int start = Math.min(this.cursorPos, this.selectionAnchor);
        int end = Math.max(this.cursorPos, this.selectionAnchor);

        int selectionLength = end - start;
        int lengthWithoutSelection = this.text.length() - selectionLength;

        if (this.maxLength > 0 && lengthWithoutSelection + newText.length() > this.maxLength) {
            int capacity = this.maxLength - lengthWithoutSelection;
            newText = capacity <= 0 ? "" : newText.substring(0, capacity);
        }

        this.internalSetText(new StringBuilder(this.text).replace(start, end, newText).toString());
        this.setCursorPos(start + newText.length(), false);
    }

    private void onMouseClick(MouseClickEvent event) {
        var textRenderer = getEffectiveTextRenderer();
        String visibleText = this.text.substring(this.firstCharacterIndex);
        int i = (int) (event.getX() - this.getInnerLeft());
        setCursorPos(this.firstCharacterIndex + textRenderer.trimToWidth(visibleText, i).length(), Screen.hasShiftDown());
    }

    private int getWordSkipPosition(int wordOffset) {
        int current = this.cursorPos;
        boolean forward = wordOffset > 0;

        for (int i = 0; i < Math.abs(wordOffset); ++i) {
            if (forward) {
                int len = this.text.length();
                current = this.text.indexOf(' ', current);
                if (current == -1) {
                    current = len;
                } else {
                    while (current < len && this.text.charAt(current) == ' ') {
                        current++;
                    }
                }
            } else {
                while (current > 0 && this.text.charAt(current - 1) == ' ') {
                    current--;
                }
                while (current > 0 && this.text.charAt(current - 1) != ' ') {
                    current--;
                }
            }
        }
        return current;
    }

    private void updateFirstCharacterIndex() {
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

    private boolean handleNavigationKeys(KeyPressEvent event, boolean shift) {
        switch (event.getKeyCode()) {
            case GLFW.GLFW_KEY_LEFT -> {
                if (Screen.hasControlDown()) {
                    setCursorPos(getWordSkipPosition(-1), shift);
                } else {
                    setCursorPos(cursorPos - 1, shift);
                }
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                if (Screen.hasControlDown()) {
                    setCursorPos(getWordSkipPosition(1), shift);
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

    private void handleDeletion(KeyPressEvent event) {
        if (event.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE && cursorPos > 0) {
            this.internalSetText(new StringBuilder(this.text).deleteCharAt(cursorPos - 1).toString());
            setCursorPos(cursorPos - 1, false);
            event.cancel();
        } else if (event.getKeyCode() == GLFW.GLFW_KEY_DELETE && cursorPos < text.length()) {
            this.internalSetText(new StringBuilder(text).deleteCharAt(cursorPos).toString());
            setCursorPos(cursorPos, false);
            event.cancel();
        }
    }

    public enum ValidationState {
        NEUTRAL, VALID, INVALID
    }
}