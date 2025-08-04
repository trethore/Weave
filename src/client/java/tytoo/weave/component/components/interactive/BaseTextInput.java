package tytoo.weave.component.components.interactive;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.effects.Effects;
import tytoo.weave.effects.implementations.OutlineEffect;
import tytoo.weave.event.keyboard.CharTypeEvent;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.state.State;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.theme.ThemeManager;
import tytoo.weave.utils.InputHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public abstract class BaseTextInput<T extends BaseTextInput<T>> extends InteractiveComponent<T> {
    private static final int MAX_HISTORY_SIZE = 100;
    protected final List<Consumer<String>> textChangeListeners = new ArrayList<>();
    protected final State<ValidationState> validationState = new State<>(ValidationState.NEUTRAL);
    protected final OutlineEffect outlineEffect;
    private final List<HistoryState> undoStack = new ArrayList<>();
    private final List<HistoryState> redoStack = new ArrayList<>();
    protected String text = "";
    protected int cursorPos = 0;
    protected int selectionAnchor = 0;
    protected long lastActionTime = 0;
    protected int maxLength = -1;
    @Nullable
    protected Predicate<String> charFilter = null;
    @Nullable
    protected Predicate<String> validator = null;
    @Nullable
    protected Text placeholder = null;
    private boolean isUpdatingFromState = false;

    protected BaseTextInput() {
        this.setPadding(4);
        this.getStyle().setColor(new Color(20, 20, 20));

        this.outlineEffect = (OutlineEffect) Effects.outline(Color.BLACK, 1.0f);
        this.addEffect(this.outlineEffect);

        this.onCharTyped(this::onCharTyped);
        this.onKeyPress(this::onKeyPressed);

        this.onFocusGained(e -> this.lastActionTime = System.currentTimeMillis());

        updateVisualState();
    }

    @Override
    protected void updateVisualState() {
        if (this.outlineEffect == null) return;

        Color outlineColor = switch (validationState.get()) {
            case VALID -> ThemeManager.getStylesheet()
                    .get(this.getClass(), StyleProps.BORDER_COLOR_VALID, new Color(0, 180, 0));
            case INVALID -> ThemeManager.getStylesheet()
                    .get(this.getClass(), StyleProps.BORDER_COLOR_INVALID, new Color(180, 0, 0));
            default -> isFocused()
                    ? ThemeManager.getStylesheet().get(this.getClass(), StyleProps.BORDER_COLOR_FOCUSED, new Color(160, 160, 160))
                    : ThemeManager.getStylesheet().get(this.getClass(), StyleProps.BORDER_COLOR_UNFOCUSED, new Color(80, 80, 80));
        };
        this.outlineEffect.setColor(outlineColor);
    }

    protected abstract boolean handleNavigation(KeyPressEvent event);

    protected abstract void ensureCursorVisible();

    private void onCharTyped(CharTypeEvent event) {
        write(Character.toString(event.getCharacter()));
    }

    private void onKeyPressed(KeyPressEvent event) {
        if (handleKeyboardInput(event)) {
            event.cancel();
        }
    }

    protected boolean handleKeyboardInput(KeyPressEvent event) {
        if (InputHelper.isUndo()) {
            undo();
            return true;
        }

        if (InputHelper.isRedo()) {
            redo();
            return true;
        }

        if (InputHelper.isSelectAll()) {
            this.selectionAnchor = 0;
            this.cursorPos = this.text.length();
            this.lastActionTime = System.currentTimeMillis();
            ensureCursorVisible();
            return true;
        }

        if (InputHelper.isCopy()) {
            MinecraftClient.getInstance().keyboard.setClipboard(getSelectedText());
            return true;
        }

        if (InputHelper.isPaste()) {
            write(MinecraftClient.getInstance().keyboard.getClipboard());
            return true;
        }

        if (InputHelper.isCut()) {
            MinecraftClient.getInstance().keyboard.setClipboard(getSelectedText());
            write("");
            return true;
        }

        if (event.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE || event.getKeyCode() == GLFW.GLFW_KEY_DELETE) {
            if (cursorPos != selectionAnchor) {
                write("");
            } else if (event.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE && cursorPos > 0) {
                this.selectionAnchor = cursorPos - 1;
                write("");
            } else if (event.getKeyCode() == GLFW.GLFW_KEY_DELETE && cursorPos < text.length()) {
                this.selectionAnchor = cursorPos + 1;
                write("");
            }
            return true;
        }

        return handleNavigation(event);
    }

    protected void beforeWriteAction() {
        saveStateToHistory();
    }

    private void saveStateToHistory() {
        redoStack.clear();
        HistoryState currentState = new HistoryState(this.text, this.cursorPos, this.selectionAnchor);
        if (!undoStack.isEmpty() && undoStack.getLast().equals(currentState)) {
            return;
        }
        undoStack.add(currentState);
        if (undoStack.size() > MAX_HISTORY_SIZE) {
            undoStack.removeFirst();
        }
    }

    private void applyHistoryState(HistoryState state) {
        internalSetText(state.text());
        this.cursorPos = Math.min(state.cursorPos(), this.text.length());
        this.selectionAnchor = Math.min(state.selectionAnchor(), this.text.length());
        ensureCursorVisible();
        this.lastActionTime = System.currentTimeMillis();
    }

    private void undo() {
        if (undoStack.isEmpty()) return;
        redoStack.add(new HistoryState(this.text, this.cursorPos, this.selectionAnchor));
        HistoryState stateToApply = undoStack.removeLast();
        applyHistoryState(stateToApply);
    }

    private void redo() {
        if (redoStack.isEmpty()) return;
        undoStack.add(new HistoryState(this.text, this.cursorPos, this.selectionAnchor));
        HistoryState stateToApply = redoStack.removeLast();
        applyHistoryState(stateToApply);
    }

    public void write(String newText) {
        beforeWriteAction();

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

    protected int getWordSkipPosition(int direction) {
        if (direction == 0) return this.cursorPos;

        int pos = this.cursorPos;
        int len = this.text.length();

        if (direction > 0) {
            while (pos < len && !Character.isWhitespace(this.text.charAt(pos))) {
                pos++;
            }
            while (pos < len && Character.isWhitespace(this.text.charAt(pos))) {
                pos++;
            }
        } else {
            while (pos > 0 && Character.isWhitespace(this.text.charAt(pos - 1))) {
                pos--;
            }
            while (pos > 0 && !Character.isWhitespace(this.text.charAt(pos - 1))) {
                pos--;
            }
        }
        return pos;
    }

    protected void setCursorPos(int pos, boolean shiftPressed) {
        this.cursorPos = Math.max(0, Math.min(this.text.length(), pos));
        if (!shiftPressed) {
            this.selectionAnchor = this.cursorPos;
        }
        this.lastActionTime = System.currentTimeMillis();
        ensureCursorVisible();
    }

    public String getSelectedText() {
        int start = Math.min(this.cursorPos, this.selectionAnchor);
        int end = Math.max(this.cursorPos, this.selectionAnchor);
        return this.text.substring(start, end);
    }

    protected void internalSetText(String newText) {
        if (Objects.equals(this.text, newText)) return;
        this.text = newText;
        validate();
        for (Consumer<String> listener : textChangeListeners) {
            listener.accept(this.text);
        }
    }

    private void validate() {
        if (this.validator == null) {
            this.validationState.set(ValidationState.NEUTRAL);
            return;
        }
        this.validationState.set(this.validator.test(this.text) ? ValidationState.VALID : ValidationState.INVALID);
    }

    public T onTextChanged(Consumer<String> listener) {
        this.textChangeListeners.add(listener);
        return self();
    }

    public T setCharFilter(@Nullable Predicate<String> charFilter) {
        this.charFilter = charFilter;
        return self();
    }

    public T setCharFilter(String regex) {
        if (regex == null) {
            this.charFilter = null;
            return self();
        }
        Pattern pattern = Pattern.compile(regex);
        return setCharFilter(s -> pattern.matcher(s).matches());
    }

    public T setValidator(@Nullable Predicate<String> validator) {
        this.validator = validator;
        validate();
        return self();
    }

    public T setValidator(@Nullable String regex) {
        if (regex == null) {
            this.validator = null;
            this.validationState.set(ValidationState.NEUTRAL);
            return self();
        }
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        return setValidator(s -> pattern.matcher(s).matches());
    }

    public T bindText(State<String> state) {
        state.bind(newValue -> {
            try {
                this.isUpdatingFromState = true;
                this.setText(newValue);
            } finally {
                this.isUpdatingFromState = false;
            }
        });
        this.onTextChanged(newText -> {
            if (this.isUpdatingFromState) return;

            if (!state.get().equals(newText)) {
                state.set(newText);
            }
        });
        return self();
    }

    public abstract T setText(String text);

    public long getLastActionTime() {
        return this.lastActionTime;
    }

    public enum ValidationState {
        NEUTRAL, VALID, INVALID
    }

    private record HistoryState(String text, int cursorPos, int selectionAnchor) {
    }

    public static final class StyleProps {
        public static final StyleProperty<Long> CURSOR_BLINK_INTERVAL = new StyleProperty<>("cursor.blink-interval", Long.class);
        public static final StyleProperty<Color> SELECTION_COLOR = new StyleProperty<>("selectionColor", Color.class);
        public static final StyleProperty<Color> BORDER_COLOR_VALID = new StyleProperty<>("borderColor.valid", Color.class);
        public static final StyleProperty<Color> BORDER_COLOR_INVALID = new StyleProperty<>("borderColor.invalid", Color.class);
        public static final StyleProperty<Color> BORDER_COLOR_FOCUSED = new StyleProperty<>("borderColor.focused", Color.class);
        public static final StyleProperty<Color> BORDER_COLOR_UNFOCUSED = new StyleProperty<>("borderColor.unfocused", Color.class);
        public static final StyleProperty<Color> PLACEHOLDER_COLOR = new StyleProperty<>("placeholderColor", Color.class);
        public static final StyleProperty<Color> CURSOR_COLOR = new StyleProperty<>("cursorColor", Color.class);
    }
}