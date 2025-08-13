package tytoo.weave.component.components.interactive;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.WeaveClient;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.layout.BasePanel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.state.State;

import java.util.Objects;

public class RadioButtonGroup<V> extends BasePanel<RadioButtonGroup<V>> {
    private final State<V> selectedValueState;
    private boolean isUpdatingFromState = false;

    private RadioButtonGroup(State<V> selectedValueState) {
        this.selectedValueState = selectedValueState;

        this.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 5f));
        this.setWidth(Constraints.childBased());
        this.setHeight(Constraints.sumOfChildrenHeight(0, 5f));

        this.selectedValueState.addListener(this::onStateChanged);
    }

    public static <V> RadioButtonGroup<V> create(State<V> selectedValueState) {
        return new RadioButtonGroup<>(selectedValueState);
    }

    private void onStateChanged(V newValue) {
        if (isUpdatingFromState) return;
        try {
            this.isUpdatingFromState = true;
            updateSelectionFromValue(newValue);
        } finally {
            this.isUpdatingFromState = false;
        }
    }

    void onButtonSelected(RadioButton<V> button) {
        if (isUpdatingFromState) return;
        V value = (button == null) ? null : button.getValue();

        if (!Objects.equals(selectedValueState.get(), value)) {
            this.selectedValueState.set(value);
        } else {
            // If the state was already correct, we still need to enforce the visual selection
            updateSelectionFromValue(value);
        }
    }

    private void updateSelectionFromValue(@Nullable V value) {
        for (Component<?> child : getChildren()) {
            if (child instanceof RadioButton) {
                @SuppressWarnings("unchecked")
                RadioButton<V> rb = (RadioButton<V>) child;
                rb.updateSelected(Objects.equals(rb.getValue(), value));
            }
        }
    }

    @Override
    public void addChild(Component<?> child) {
        if (!(child instanceof RadioButton)) {
            WeaveClient.LOGGER.error("RadioButtonGroup can only contain RadioButton components. Found {}.", child.getClass().getSimpleName());
            return;
        }

        @SuppressWarnings("unchecked")
        RadioButton<V> rb = (RadioButton<V>) child;
        boolean isFirstButton = getChildren().isEmpty();

        rb.setGroup(this);
        super.addChild(child);

        if (this.selectedValueState.get() == null && isFirstButton) {
            onButtonSelected(rb);
        } else if (Objects.equals(rb.getValue(), this.selectedValueState.get())) {
            rb.updateSelected(true);
        }
    }

    @Override
    public void removeChild(Component<?> child) {
        if (child instanceof RadioButton) {
            ((RadioButton<?>) child).setGroup(null);
        }
        super.removeChild(child);
    }

    public State<V> getSelectedValueState() {
        return selectedValueState;
    }

    public V getSelectedValue() {
        return selectedValueState.get();
    }

    public void setSelectedValue(V value) {
        selectedValueState.set(value);
    }
}