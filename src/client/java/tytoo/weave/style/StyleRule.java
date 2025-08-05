package tytoo.weave.style;

import tytoo.weave.style.selector.StyleSelector;

import java.util.Map;

public class StyleRule {
    private final StyleSelector selector;
    private final Map<StyleProperty<?>, Object> properties;
    private final int specificity;

    public StyleRule(StyleSelector selector, Map<StyleProperty<?>, Object> properties) {
        this.selector = selector;
        this.properties = properties;
        this.specificity = selector.getSpecificity();
    }

    public StyleSelector getSelector() {
        return selector;
    }

    public Map<StyleProperty<?>, Object> getProperties() {
        return properties;
    }

    public int getSpecificity() {
        return specificity;
    }
}