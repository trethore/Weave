package tytoo.weave.style.selector;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.component.NamedPart;
import tytoo.weave.style.StyleState;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;

public class StyleSelector {
    private final Class<? extends Component<?>> componentType;
    @Nullable
    private final String id;
    private final Set<String> styleClasses;
    private final Set<StyleState> states;
    @Nullable
    private final Class<? extends Component<?>> ancestorType;
    private final boolean directChild;

    @Nullable
    private final String partName;
    @Nullable
    private final Class<? extends Component<?>> partHostType;

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
        this.ancestorType = null;
        this.directChild = false;
        this.partName = null;
        this.partHostType = null;
        this.specificity = calculateSpecificity();
    }

    @SuppressWarnings("unchecked")
    public StyleSelector(Class<?> componentType,
                         @Nullable String id,
                         @Nullable Set<String> styleClasses,
                         @Nullable Set<StyleState> states,
                         @Nullable Class<?> ancestorType,
                         boolean directChild) {
        if (componentType != null && !Component.class.isAssignableFrom(componentType)) {
            throw new IllegalArgumentException("Selector componentType must be a subclass of Component.");
        }
        if (ancestorType != null && !Component.class.isAssignableFrom(ancestorType)) {
            throw new IllegalArgumentException("Selector ancestorType must be a subclass of Component.");
        }
        this.componentType = (Class<? extends Component<?>>) (componentType != null ? componentType : Component.class);
        this.id = id;
        this.styleClasses = styleClasses != null ? Set.copyOf(styleClasses) : Set.of();
        this.states = states != null ? Set.copyOf(states) : Set.of();
        this.ancestorType = (Class<? extends Component<?>>) ancestorType;
        this.directChild = directChild;
        this.partName = null;
        this.partHostType = null;
        this.specificity = calculateSpecificity();
    }

    @SuppressWarnings("unchecked")
    public StyleSelector(@Nullable Class<?> componentType,
                         @Nullable String id,
                         @Nullable Set<String> styleClasses,
                         @Nullable Set<StyleState> states,
                         @Nullable Class<?> ancestorType,
                         boolean directChild,
                         @Nullable String partName,
                         @Nullable Class<?> partHostType) {
        if (componentType != null && !Component.class.isAssignableFrom(componentType)) {
            throw new IllegalArgumentException("Selector componentType must be a subclass of Component.");
        }
        if (ancestorType != null && !Component.class.isAssignableFrom(ancestorType)) {
            throw new IllegalArgumentException("Selector ancestorType must be a subclass of Component.");
        }
        if (partHostType != null && !Component.class.isAssignableFrom(partHostType)) {
            throw new IllegalArgumentException("Selector partHostType must be a subclass of Component.");
        }
        this.componentType = (Class<? extends Component<?>>) (componentType != null ? componentType : Component.class);
        this.id = id;
        this.styleClasses = styleClasses != null ? Set.copyOf(styleClasses) : Set.of();
        this.states = states != null ? Set.copyOf(states) : Set.of();
        this.ancestorType = (Class<? extends Component<?>>) ancestorType;
        this.directChild = directChild;
        this.partName = partName;
        this.partHostType = (Class<? extends Component<?>>) partHostType;
        this.specificity = calculateSpecificity();
    }

    public static StyleSelector child(Class<?> parentType,
                                      Class<?> childType,
                                      @Nullable String id,
                                      @Nullable Set<String> styleClasses,
                                      @Nullable Set<StyleState> states) {
        return new StyleSelector(childType, id, styleClasses, states, parentType, true);
    }

    public static StyleSelector descendant(Class<?> ancestorType,
                                           Class<?> childType,
                                           @Nullable String id,
                                           @Nullable Set<String> styleClasses,
                                           @Nullable Set<StyleState> states) {
        return new StyleSelector(childType, id, styleClasses, states, ancestorType, false);
    }

    public static StyleSelector part(Class<?> hostType,
                                     String partName) {
        return new StyleSelector(null, null, null, null, null, false, partName, hostType);
    }

    public static StyleSelector part(Class<?> hostType,
                                     String partName,
                                     @Nullable Class<?> partType,
                                     @Nullable String id,
                                     @Nullable Set<String> styleClasses,
                                     @Nullable Set<StyleState> states) {
        return new StyleSelector(partType, id, styleClasses, states, null, false, partName, hostType);
    }

    private int calculateSpecificity() {
        int s = 0;
        if (id != null) {
            s += 10000;
        }
        s += styleClasses.size() * 1000;
        s += states.size() * 100;
        if (partName != null) {
            s += 1000;
        }

        int depth = 0;
        Class<?> current = this.componentType;
        while (current != null && Component.class.isAssignableFrom(current)) {
            depth++;
            current = current.getSuperclass();
        }
        s += depth;
        if (ancestorType != null) {
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

        if (!component.getActiveStyleStates().containsAll(this.states)) {
            return false;
        }

        if (ancestorType != null) {
            if (directChild) {
                Component<?> parent = component.getParent();
                if (!(parent != null && ancestorType.isAssignableFrom(parent.getClass()))) return false;
            } else {
                boolean found = false;
                for (Component<?> p = component.getParent(); p != null; p = p.getParent()) {
                    if (ancestorType.isAssignableFrom(p.getClass())) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
        }

        if (partName != null) {
            boolean found = false;
            for (Component<?> p = component.getParent(); p != null; p = p.getParent()) {
                if (partHostType != null && !partHostType.isAssignableFrom(p.getClass())) continue;
                for (Field f : p.getClass().getDeclaredFields()) {
                    if (f.isAnnotationPresent(NamedPart.class) && f.getName().equals(partName)) {
                        try {
                            f.setAccessible(true);
                            Object val = f.get(p);
                            if (val == component) {
                                found = true;
                                break;
                            }
                        } catch (IllegalAccessException ignored) {
                        }
                    }
                }
                if (found) break;
            }
            return found;
        }
        return true;
    }

    public int getSpecificity() {
        return specificity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StyleSelector that = (StyleSelector) o;
        return specificity == that.specificity && directChild == that.directChild && Objects.equals(componentType, that.componentType) && Objects.equals(id, that.id) && Objects.equals(styleClasses, that.styleClasses) && Objects.equals(states, that.states) && Objects.equals(ancestorType, that.ancestorType) && Objects.equals(partName, that.partName) && Objects.equals(partHostType, that.partHostType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentType, id, styleClasses, states, ancestorType, directChild, partName, partHostType, specificity);
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
        if (partName != null) {
            sb.append("::part(").append(partName).append(")");
            if (partHostType != null) {
                sb.append(" of ").append(partHostType.getSimpleName());
            }
        }
        if (ancestorType != null) {
            sb.append(directChild ? " > " : " ")
                    .append(ancestorType.getSimpleName());
        }
        return sb.toString();
    }
}
