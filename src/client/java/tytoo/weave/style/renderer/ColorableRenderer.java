package tytoo.weave.style.renderer;

import java.awt.Color;

public interface ColorableRenderer extends ComponentRenderer {
    Color getColor();
    void setColor(Color color);
}
