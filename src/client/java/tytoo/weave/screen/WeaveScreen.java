package tytoo.weave.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import tytoo.weave.component.components.layout.Window;
import tytoo.weave.ui.UIManager;

public abstract class WeaveScreen extends Screen {
    protected final Window window = Window.create();

    protected WeaveScreen(Text title) {
        super(title);
        UIManager.setRoot(this, this.window);
    }

    public Window getWindow() {
        return window;
    }

    public void open() {
        if (this.client != null) {
            this.client.setScreen(this);
        }
    }
}