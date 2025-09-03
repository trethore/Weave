package tytoo.weave.animation;

import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.Component;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.BaseTextInput;
import tytoo.weave.component.components.interactive.ComboBox;
import tytoo.weave.state.State;
import tytoo.weave.style.LayoutStyleProperties;
import tytoo.weave.style.StyleProperty;
import tytoo.weave.style.StyleState;
import tytoo.weave.style.renderer.ColorableRenderer;
import tytoo.weave.style.renderer.ComponentRenderer;
import tytoo.weave.theme.Stylesheet;
import tytoo.weave.theme.ThemeManager;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@SuppressWarnings({"unused", "unchecked"})
public final class StyleTransitionRegistry {
    private static final List<Registration<?, ?>> REGISTRATIONS = new ArrayList<>();
    private static final WeakHashMap<Component<?>, Map<Object, Object>> LAST_VALUES = new WeakHashMap<>();

    static {
        registerStyleProperty(Component.class, LayoutStyleProperties.PADDING, null,
                Interpolators.EDGE_INSETS,
                Component::setAnimatedPadding,
                Component::commitAnimatedPadding);

        registerStyleProperty(Component.class, LayoutStyleProperties.BORDER_WIDTH, 0.0f,
                Interpolators.FLOAT,
                Component::setAnimatedBorderWidth,
                Component::commitAnimatedBorderWidth);
        registerStyleProperty(Component.class, LayoutStyleProperties.BORDER_COLOR, null,
                Interpolators.COLOR,
                Component::setAnimatedBorderColor,
                Component::commitAnimatedBorderColor);
        registerStyleProperty(Component.class, LayoutStyleProperties.BORDER_RADIUS, 0.0f,
                Interpolators.FLOAT,
                Component::setAnimatedBorderRadius,
                Component::commitAnimatedBorderRadius);

        registerStyleProperty(Component.class, LayoutStyleProperties.OVERLAY_BORDER_WIDTH, 0.0f,
                Interpolators.FLOAT,
                Component::setAnimatedOverlayBorderWidth,
                Component::commitAnimatedOverlayBorderWidth);
        registerStyleProperty(Component.class, LayoutStyleProperties.OVERLAY_BORDER_COLOR, null,
                Interpolators.COLOR,
                Component::setAnimatedOverlayBorderColor,
                Component::commitAnimatedOverlayBorderColor);
        registerStyleProperty(Component.class, LayoutStyleProperties.OVERLAY_BORDER_RADIUS, 0.0f,
                Interpolators.FLOAT,
                Component::setAnimatedOverlayBorderRadius,
                Component::commitAnimatedOverlayBorderRadius);

        registerComputed(Component.class, Keys.BACKGROUND_COLOR,
                (ss, c) -> {
                    ComponentRenderer r = c.getStyle().getRenderer(c);
                    if (r instanceof ColorableRenderer cr) return cr.getColor();
                    return null;
                },
                Interpolators.COLOR,
                (c, color) -> {
                    ComponentRenderer r = c.getStyle().getRenderer(c);
                    if (r instanceof ColorableRenderer cr && color != null) cr.setColor(color);
                },
                null);

        registerComputed(Component.class, Keys.OVERLAY_BACKGROUND_COLOR,
                (ss, c) -> {
                    ComponentRenderer r = c.getStyle().getOverlayRenderer(c);
                    if (r instanceof ColorableRenderer cr) return cr.getColor();
                    return null;
                },
                Interpolators.COLOR,
                (c, color) -> {
                    ComponentRenderer r = c.getStyle().getOverlayRenderer(c);
                    if (r instanceof ColorableRenderer cr && color != null) cr.setColor(color);
                },
                null);

        registerStyleProperty(TextComponent.class, TextComponent.StyleProps.TEXT_COLOR, null,
                Interpolators.COLOR,
                TextComponent::applyAnimatedTextColor,
                TextComponent::clearAnimatedTextColor);

        registerComputed(ComboBox.class, Keys.COMBOBOX_OUTLINE_COLOR,
                (ss, c) -> {
                    if (c.isFocused() || c.hasStyleState(StyleState.ACTIVE)) {
                        return ss.get(c, BaseTextInput.StyleProps.BORDER_COLOR_FOCUSED, new Color(160, 160, 160));
                    } else if (c.hasStyleState(StyleState.HOVERED)) {
                        return ss.get(c, ComboBox.StyleProps.BORDER_COLOR_HOVERED, new Color(120, 120, 120));
                    } else {
                        return ss.get(c, BaseTextInput.StyleProps.BORDER_COLOR_UNFOCUSED, new Color(80, 80, 80));
                    }
                },
                Interpolators.COLOR,
                ComboBox::applyOutlineColor,
                null);

        registerComputed(BaseTextInput.class, Keys.TEXT_INPUT_OUTLINE_COLOR,
                (ss, c) -> {
                    BaseTextInput.ValidationState vState = c.getValidationState();
                    if (vState == BaseTextInput.ValidationState.VALID) {
                        return ss.get(c, BaseTextInput.StyleProps.BORDER_COLOR_VALID, new Color(0, 180, 0));
                    } else if (vState == BaseTextInput.ValidationState.INVALID) {
                        return ss.get(c, BaseTextInput.StyleProps.BORDER_COLOR_INVALID, new Color(180, 0, 0));
                    } else if (c.isFocused()) {
                        return ss.get(c, BaseTextInput.StyleProps.BORDER_COLOR_FOCUSED, new Color(160, 160, 160));
                    } else if (c.hasStyleState(StyleState.HOVERED)) {
                        return ss.get(c, BaseTextInput.StyleProps.BORDER_COLOR_HOVERED, new Color(120, 120, 120));
                    } else {
                        return ss.get(c, BaseTextInput.StyleProps.BORDER_COLOR_UNFOCUSED, new Color(80, 80, 80));
                    }
                },
                Interpolators.COLOR,
                BaseTextInput::applyOutlineColor,
                null);
    }

    private StyleTransitionRegistry() {
    }

    public static <C extends Component<?>, T> void registerStyleProperty(
            Class<C> componentClass,
            StyleProperty<T> property,
            T defaultValue,
            PropertyInterpolator<T> interpolator,
            BiConsumer<C, T> applyUpdate,
            @Nullable BiConsumer<C, T> onFinish
    ) {
        REGISTRATIONS.add(new Registration<>(componentClass, property,
                (ss, c) -> ss.get(c, property, defaultValue), interpolator, applyUpdate, onFinish));
    }

    public static <C extends Component<?>, T> void registerComputed(
            Class<C> componentClass,
            Object key,
            BiFunction<Stylesheet, C, T> toValueResolver,
            PropertyInterpolator<T> interpolator,
            BiConsumer<C, T> applyUpdate,
            @Nullable BiConsumer<C, T> onFinish
    ) {
        REGISTRATIONS.add(new Registration<>(componentClass, key, toValueResolver, interpolator, applyUpdate, onFinish));
    }

    public static void applyTransitions(Component<?> component, long duration, EasingFunction easing) {
        if (REGISTRATIONS.isEmpty()) return;

        Stylesheet ss = ThemeManager.getStylesheet();

        for (Registration<?, ?> regRaw : REGISTRATIONS) {
            if (!regRaw.componentClass.isAssignableFrom(component.getClass())) continue;
            applyOne((Registration<Component<?>, Object>) regRaw, component, ss, duration, easing);
        }
    }

    private static <C extends Component<?>, T> void applyOne(Registration<C, T> reg,
                                                             Component<?> component,
                                                             Stylesheet ss,
                                                             long duration,
                                                             EasingFunction easing) {
        C comp = (C) component;
        T toValue = reg.toValueResolver.apply(ss, comp);
        if (toValue == null) return;

        Map<Object, Object> map = LAST_VALUES.computeIfAbsent(comp, k -> new HashMap<>());
        T last = (T) map.get(reg.key);

        if (last == null) {
            map.put(reg.key, toValue);
            reg.applyUpdate.accept(comp, toValue);
            return;
        }

        if (Objects.equals(last, toValue)) {
            return;
        }

        if (duration <= 0) {
            reg.applyUpdate.accept(comp, toValue);
            map.put(reg.key, toValue);
            if (reg.onFinish != null) reg.onFinish.accept(comp, toValue);
            return;
        }

        State<T> state = new State<>(last);
        state.addListener(v -> {
            reg.applyUpdate.accept(comp, v);
            map.put(reg.key, v);
        });

        Animation<T> animation = new Animation<>(state, toValue, duration, easing, reg.interpolator, a -> {
            map.put(reg.key, toValue);
            if (reg.onFinish != null) {
                reg.onFinish.accept(comp, toValue);
            }
        });

        Animator.getInstance().add(new AnimationBuilder.AnimationKey(comp, reg.key), animation);
    }

    public static final class Keys {
        public static final Object BACKGROUND_COLOR = new Object();
        public static final Object OVERLAY_BACKGROUND_COLOR = new Object();
        public static final Object TEXT_INPUT_OUTLINE_COLOR = new Object();
        public static final Object COMBOBOX_OUTLINE_COLOR = new Object();

        private Keys() {
        }
    }

    private record Registration<C extends Component<?>, T>(
            Class<C> componentClass,
            Object key,
            BiFunction<Stylesheet, C, T> toValueResolver,
            PropertyInterpolator<T> interpolator,
            BiConsumer<C, T> applyUpdate,
            @Nullable BiConsumer<C, T> onFinish
    ) {
    }
}
