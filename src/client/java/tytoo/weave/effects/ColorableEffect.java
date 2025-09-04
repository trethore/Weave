package tytoo.weave.effects;

import java.awt.*;

public interface ColorableEffect extends Effect {
    Color getColor();

    void setColor(Color color);
}

