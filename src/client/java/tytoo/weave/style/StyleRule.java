package tytoo.weave.style;

import tytoo.weave.style.contract.StyleSlot;
import tytoo.weave.style.selector.StyleSelector;

import java.util.Map;

public class StyleRule {
    private final StyleSelector selector;
    private final Map<StyleSlot, Object> values;
    private final int specificity;

    public StyleRule(StyleSelector selector, Map<StyleSlot, Object> values) {
        this.selector = selector;
        this.values = values;
        this.specificity = selector.getSpecificity();
    }

    public StyleSelector getSelector() {
        return selector;
    }

    public Map<StyleSlot, Object> getValues() {
        return values;
    }

    public int getSpecificity() {
        return specificity;
    }
}
