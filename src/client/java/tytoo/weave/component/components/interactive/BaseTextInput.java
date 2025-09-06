package tytoo.weave.component.components.interactive;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import tytoo.weave.effects.ColorableEffect;
import tytoo.weave.effects.Effect;
import tytoo.weave.effects.Effects;
import tytoo.weave.effects.implementations.OutlineEffect;
import tytoo.weave.event.keyboard.CharTypeEvent;
import tytoo.weave.event.keyboard.KeyPressEvent;
import tytoo.weave.state.State;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.StyleState;
import tytoo.weave.style.renderer.textfield.*;
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
    private final List<HistoryState> undoStack = new ArrayList<>();
    private final List<HistoryState> redoStack = new ArrayList<>();
    private Effect outlineEffect;
    @Nullable
    private ColorableEffect outlineColorTarget;
    private String text = "";
    private int cursorPos = 0;
    private int selectionAnchor = 0;
    private long lastActionTime = 0;
    private long lastClickTime = 0;
    private int consecutiveClicks = 0;
    private int maxLength = -1;
    @Nullable
    private Predicate<String> charFilter = null;
    @Nullable
    private Predicate<String> validator = null;
    @Nullable
    private Text placeholder = null;
    private CursorRenderer cursorRenderer = new DefaultCursorRenderer();
    private SelectionRenderer selectionRenderer = new DefaultSelectionRenderer();
    private PlaceholderRenderer placeholderRenderer = new DefaultPlaceholderRenderer();
    private boolean isUpdatingFromState = false;

    protected BaseTextInput() {
        this.setPadding(4);
        this.addStyleClass("interactive-visual");

        this.setOutlineEffect(Effects.outline(Color.BLACK, 1.0f));
        this.addStyleState(StyleState.NORMAL);

        this.onCharTyped(this::onCharTyped);
        this.onKeyPress(this::onKeyPressed);

        this.onFocusGained(e -> {
            this.setLastActionTime(System.currentTimeMillis());
            this.consecutiveClicks = 0;
        });
        this.onFocusLost(e -> this.consecutiveClicks = 0);

        updateVisualState(0L);
    }

    @Override
    protected void updateVisualState(long duration) {
        super.updateVisualState(duration);
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
        if (handleUndoRedo()) return true;
        if (handleClipboard()) return true;
        if (handleSelection()) return true;
        if (handleDeletion(event)) return true;
        if (event.getKeyCode() == GLFW.GLFW_KEY_TAB && !InputHelper.isShiftDown()) {
            return true;
        }
        return handleNavigation(event);
    }

    private boolean handleUndoRedo() {
        if (InputHelper.isUndo()) {
            undo();
            return true;
        }

        if (InputHelper.isRedo()) {
            redo();
            return true;
        }
        return false;
    }

    private boolean handleClipboard() {
        if (InputHelper.isCopy()) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            return true;
        }

        if (InputHelper.isPaste()) {
            this.write(MinecraftClient.getInstance().keyboard.getClipboard());
            return true;
        }

        if (InputHelper.isCut()) {
            MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
            this.write("");
            return true;
        }
        return false;
    }

    private boolean handleSelection() {
        if (InputHelper.isSelectAll()) {
            setSelectionAnchor(0);
            setCursorPos(getText().length(), true);
            setLastActionTime(System.currentTimeMillis());
            ensureCursorVisible();
            return true;
        }
        return false;
    }

    private boolean handleDeletion(KeyPressEvent event) {
        if (event.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE || event.getKeyCode() == GLFW.GLFW_KEY_DELETE) {
            if (getCursorPos() != getSelectionAnchor()) {
                write("");
            } else if (event.getKeyCode() == GLFW.GLFW_KEY_BACKSPACE && getCursorPos() > 0) {
                setSelectionAnchor(getCursorPos() - 1);
                this.write("");
            } else if (event.getKeyCode() == GLFW.GLFW_KEY_DELETE && getCursorPos() < getText().length()) {
                setSelectionAnchor(getCursorPos() + 1);
                this.write("");
            }
            return true;
        }
        return false;
    }

    protected void beforeWriteAction() {
        saveStateToHistory();
    }

    private void saveStateToHistory() {
        redoStack.clear();
        HistoryState currentState = new HistoryState(getText(), getCursorPos(), getSelectionAnchor());
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
        setCursorPos(Math.min(state.cursorPos(), getText().length()), false);
        setSelectionAnchor(Math.min(state.selectionAnchor(), getText().length()));
        ensureCursorVisible();
        setLastActionTime(System.currentTimeMillis());
    }

    private void undo() {
        if (undoStack.isEmpty()) return;
        redoStack.add(new HistoryState(getText(), getCursorPos(), getSelectionAnchor()));
        HistoryState stateToApply = undoStack.removeLast();
        applyHistoryState(stateToApply);
    }

    private void redo() {
        if (redoStack.isEmpty()) return;
        undoStack.add(new HistoryState(getText(), getCursorPos(), getSelectionAnchor()));
        HistoryState stateToApply = redoStack.removeLast();
        applyHistoryState(stateToApply);
    }

    public void write(String newText) {
        beforeWriteAction();

        Predicate<String> currentFilter = getCharFilter();
        if (currentFilter != null) {
            StringBuilder filteredText = new StringBuilder();
            for (char c : newText.toCharArray()) {
                if (currentFilter.test(String.valueOf(c))) {
                    filteredText.append(c);
                }
            }
            newText = filteredText.toString();
        }

        int start = Math.min(getCursorPos(), getSelectionAnchor());
        int end = Math.max(getCursorPos(), getSelectionAnchor());
        int selectionLength = end - start;
        int lengthWithoutSelection = getText().length() - selectionLength;

        if (getMaxLength() > 0 && lengthWithoutSelection + newText.length() > getMaxLength()) {
            int capacity = getMaxLength() - lengthWithoutSelection;
            newText = capacity <= 0 ? "" : newText.substring(0, capacity);
        }

        internalSetText(new StringBuilder(this.text).replace(start, end, newText).toString());
        setCursorPos(start + newText.length(), false);
    }

    protected int getWordSkipPosition(int direction) {
        if (direction == 0) return getCursorPos();

        int pos = getCursorPos();
        int len = getText().length();

        if (direction > 0) {
            while (pos < len && !Character.isWhitespace(getText().charAt(pos))) {
                pos++;
            }
            while (pos < len && Character.isWhitespace(getText().charAt(pos))) {
                pos++;
            }
        } else {
            while (pos > 0 && Character.isWhitespace(getText().charAt(pos - 1))) {
                pos--;
            }
            while (pos > 0 && !Character.isWhitespace(getText().charAt(pos - 1))) {
                pos--;
            }
        }
        return pos;
    }

    protected void setCursorPos(int pos, boolean shiftPressed) {
        this.cursorPos = Math.max(0, Math.min(getText().length(), pos));
        if (!shiftPressed) {
            setSelectionAnchor(this.cursorPos);
        }
        setLastActionTime(System.currentTimeMillis());
        ensureCursorVisible();
    }

    protected int registerClickAndGetCount() {
        long now = System.currentTimeMillis();
        long interval = ThemeManager.getStylesheet().get(this, StyleProps.MULTI_CLICK_INTERVAL, 300L);
        if (now - lastClickTime <= interval) {
            consecutiveClicks++;
        } else {
            consecutiveClicks = 1;
        }
        lastClickTime = now;
        return consecutiveClicks;
    }

    protected Point getWordBoundsAt(int pos) {
        String t = getText();
        int len = t.length();
        if (len == 0) return new Point(0, 0);
        pos = Math.max(0, Math.min(len, pos));

        boolean wsLeft = pos > 0 && Character.isWhitespace(t.charAt(pos - 1));
        boolean wsRight = pos < len && Character.isWhitespace(t.charAt(pos));
        boolean selectWhitespace = wsLeft || wsRight;

        int start = pos;
        int end = pos;

        if (selectWhitespace) {
            while (start > 0 && Character.isWhitespace(t.charAt(start - 1))) start--;
            while (end < len && Character.isWhitespace(t.charAt(end))) end++;
        } else {
            while (start > 0 && !Character.isWhitespace(t.charAt(start - 1))) start--;
            while (end < len && !Character.isWhitespace(t.charAt(end))) end++;
        }
        return new Point(start, end);
    }

    public String getSelectedText() {
        int start = Math.min(getCursorPos(), getSelectionAnchor());
        int end = Math.max(getCursorPos(), getSelectionAnchor());
        return getText().substring(start, end);
    }

    protected void internalSetText(String newText) {
        if (Objects.equals(this.text, newText)) return;
        this.text = newText;
        validate();
        for (Consumer<String> listener : this.textChangeListeners) {
            listener.accept(getText());
        }
    }

    private void validate() {
        this.removeStyleState(StyleState.VALID);
        this.removeStyleState(StyleState.INVALID);

        Predicate<String> currentValidator = this.getValidator();
        if (currentValidator == null) {
            validationState.set(ValidationState.NEUTRAL);
            return;
        }

        boolean isValid = currentValidator.test(this.text);
        validationState.set(isValid ? ValidationState.VALID : ValidationState.INVALID);
        addStyleState(isValid ? StyleState.VALID : StyleState.INVALID);
    }

    public T onTextChanged(Consumer<String> listener) {
        this.textChangeListeners.add(listener);
        return self();
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

    public long getLastActionTime() {
        return lastActionTime;
    }

    protected void setLastActionTime(long lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    public String getText() {
        return this.text;
    }

    public abstract T setText(String text);

    public int getCursorPos() {
        return this.cursorPos;
    }

    protected void setCursorPos(int cursorPos) {
        this.cursorPos = cursorPos;
    }

    public int getSelectionAnchor() {
        return this.selectionAnchor;
    }

    protected void setSelectionAnchor(int selectionAnchor) {
        this.selectionAnchor = selectionAnchor;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public T setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (this.maxLength > 0 && getText().length() > this.maxLength) {
            setText(getText().substring(0, this.maxLength));
        }
        return self();
    }

    @Nullable
    public Predicate<String> getCharFilter() {
        return this.charFilter;
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

    @Nullable
    public Predicate<String> getValidator() {
        return this.validator;
    }

    public T setValidator(@Nullable Predicate<String> validator) {
        this.validator = validator;
        validate();
        return self();
    }

    public T setValidator(@Nullable String regex) {
        if (regex == null) {
            this.validator = null;
            validate();
            return self();
        }
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        return setValidator(s -> pattern.matcher(s).matches());
    }

    @Nullable
    public Text getPlaceholder() {
        return this.placeholder;
    }

    public T setPlaceholder(@Nullable Text placeholder) {
        this.placeholder = placeholder;
        return self();
    }

    public T setPlaceholder(@Nullable String placeholder) {
        this.placeholder = placeholder == null ? null : Text.of(placeholder);
        return self();
    }

    public CursorRenderer getCursorRenderer() {
        return cursorRenderer;
    }

    public T setCursorRenderer(CursorRenderer cursorRenderer) {
        this.cursorRenderer = cursorRenderer;
        return self();
    }

    public SelectionRenderer getSelectionRenderer() {
        return selectionRenderer;
    }

    public T setSelectionRenderer(SelectionRenderer selectionRenderer) {
        this.selectionRenderer = selectionRenderer;
        return self();
    }

    public PlaceholderRenderer getPlaceholderRenderer() {
        return placeholderRenderer;
    }

    public T setPlaceholderRenderer(PlaceholderRenderer placeholderRenderer) {
        this.placeholderRenderer = placeholderRenderer;
        return self();
    }

    @Override
    public T clone() {
        T clone = super.clone();
        if (this.validator != null) {
            ((BaseTextInput<?>) clone).setValidator(this.validator);
        }
        return clone;
    }

    public ValidationState getValidationState() {
        return this.validationState.get();
    }

    public void applyOutlineColor(Color color) {
        if (this.outlineColorTarget != null) {
            this.outlineColorTarget.setColor(color);
        } else if (this.outlineEffect instanceof OutlineEffect outline) {
            outline.setColor(color);
        }
    }

    public void applyOutlineWidth(float width) {
        if (this.outlineEffect instanceof OutlineEffect outline) {
            outline.setWidth(width);
        }
    }

    public void applyOutlineInside(boolean inside) {
        if (this.outlineEffect instanceof OutlineEffect outline) {
            outline.setInside(inside);
        }
    }

    public T setOutlineEffect(Effect effect) {
        if (effect == null) return self();
        if (this.outlineEffect != null) {
            this.removeEffect(this.outlineEffect);
        }
        this.outlineEffect = effect;
        this.addEffect(effect);
        this.outlineColorTarget = effect instanceof ColorableEffect c ? c : null;
        return self();
    }

    public enum ValidationState {
        NEUTRAL, VALID, INVALID
    }

    private record HistoryState(String text, int cursorPos, int selectionAnchor) {
    }

    public static final class StyleProps {
        public static final StyleProperty<Long> CURSOR_BLINK_INTERVAL = new StyleProperty<>("cursor.blink-interval", Long.class);
        public static final StyleProperty<Long> MULTI_CLICK_INTERVAL = new StyleProperty<>("click.multi-interval", Long.class);
        public static final StyleProperty<Color> SELECTION_COLOR = new StyleProperty<>("selectionColor", Color.class);
        public static final StyleProperty<Color> BORDER_COLOR_VALID = new StyleProperty<>("borderColor.valid", Color.class);
        public static final StyleProperty<Color> BORDER_COLOR_INVALID = new StyleProperty<>("borderColor.invalid", Color.class);
        public static final StyleProperty<Color> BORDER_COLOR_FOCUSED = new StyleProperty<>("borderColor.focused", Color.class);
        public static final StyleProperty<Color> BORDER_COLOR_HOVERED = new StyleProperty<>("borderColor.hovered", Color.class);
        public static final StyleProperty<Color> BORDER_COLOR_UNFOCUSED = new StyleProperty<>("borderColor.unfocused", Color.class);
        public static final StyleProperty<Color> PLACEHOLDER_COLOR = new StyleProperty<>("placeholderColor", Color.class);
        public static final StyleProperty<Color> CURSOR_COLOR = new StyleProperty<>("cursorColor", Color.class);

        public static final StyleProperty<Float> DEFAULT_WIDTH = new StyleProperty<>("text-input.default-width", Float.class);
        public static final StyleProperty<Float> DEFAULT_HEIGHT = new StyleProperty<>("text-input.default-height", Float.class);

        public static final StyleProperty<Float> OUTLINE_WIDTH = new StyleProperty<>("outline.width", Float.class);
        public static final StyleProperty<Boolean> OUTLINE_INSIDE = new StyleProperty<>("outline.inside", Boolean.class);
    }
}
