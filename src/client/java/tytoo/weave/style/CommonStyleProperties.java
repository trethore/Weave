package tytoo.weave.style;

import tytoo.weave.ui.CursorType;

public final class CommonStyleProperties {
    public static final StyleProperty<CursorType> CURSOR = new StyleProperty<>("cursor", CursorType.class);

    private CommonStyleProperties() {
    }
}