package tytoo.weave.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import tytoo.weave.component.components.layout.Window;
import tytoo.weave.ui.UIManager;
import tytoo.weave.utils.McUtils;

public abstract class WeaveScreen extends Screen {
    protected final Window window = Window.create();
    private @Nullable Screen prevScreen = null;

    protected WeaveScreen(Text title) {
        super(title);
        UIManager.setRoot(this, this.window);
    }

    @Override
    public void close() {
        McUtils.getMc().ifPresent(mc -> mc.setScreen(prevScreen));
    }

    public void open() {
        McUtils.getMc().ifPresent(mc -> {
            if (mc.currentScreen != null) {
                this.prevScreen = mc.currentScreen;
            }
            mc.setScreen(this);
        });
    }


    public Window getWindow() {
        return window;
    }

    public @Nullable Screen getPrevScreen() {
        return prevScreen;
    }


}