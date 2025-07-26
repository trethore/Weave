package tytoo.weave.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import tytoo.weave.component.components.Window;

public abstract class WeaveScreen extends Screen {
    protected final Window window = new Window();

    protected WeaveScreen(Text title) {
        super(title);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        window.draw(context);
    }

    public Window getWindow() {
        return window;
    }
}