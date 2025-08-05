package tytoo.weave.style.selector;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.style.StyleState;

import java.util.Objects;
import java.util.Set;

public class StyleSelector {
    private final Class<? extends Component<?>> componentType;
    @Nullable
    private final String id;
    private final Set<String> styleClasses;
    private final Set<StyleState> states;

    private final int specificity;

    @SuppressWarnings("unchecked")
    public StyleSelector(Class<?> componentType, @Nullable String id, @Nullable Set<String> styleClasses, @Nullable Set<StyleState> states) {
        if (componentType != null && !Component.class.isAssignableFrom(componentType)) {
            throw new IllegalArgumentException("Selector componentType must be a subclass of Component.");
        }
        this.componentType = (Class<? extends Component<?>>) (componentType != null ? componentType : Component.class);
        this.id = id;
        this.styleClasses = styleClasses != null ? Set.copyOf(styleClasses) : Set.of();
        this.states = states != null ? Set.copyOf(states) : Set.of();
        this.specificity = calculateSpecificity();
    }

    private int calculateSpecificity() {
        int s = 0;
        if (id != null) {
            s += 1000;
        }
        s += styleClasses.size() * 100;
        s += states.size() * 10;

        if (!this.componentType.equals(Component.class)) {
            s += 1;
        }
        return s;
    }

    public boolean matches(Component<?> component) {
        if (!componentType.isAssignableFrom(component.getClass())) {
            return false;
        }

        if (id != null && !id.equals(component.getId())) {
            return false;
        }

        if (!component.getStyleClasses().containsAll(this.styleClasses)) {
            return false;
        }

        return component.getActiveStyleStates().containsAll(this.states);
    }

    public int getSpecificity() {
        return specificity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StyleSelector that = (StyleSelector) o;
        return specificity == that.specificity && Objects.equals(componentType, that.componentType) && Objects.equals(id, that.id) && Objects.equals(styleClasses, that.styleClasses) && Objects.equals(states, that.states);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentType, id, styleClasses, states, specificity);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (componentType.equals(Component.class)) {
            if (id == null && styleClasses.isEmpty()) {
                sb.append("*");
            }
        } else {
            sb.append(componentType.getSimpleName());
        }

        if (id != null) {
            sb.append('#').append(id);
        }
        for (String c : styleClasses) {
            sb.append('.').append(c);
        }
        for (StyleState s : states) {
            sb.append(':').append(s.getName());
        }
        return sb.toString();
    }
}