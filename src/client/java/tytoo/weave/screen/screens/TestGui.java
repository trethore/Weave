package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.WeaveClient;
import tytoo.weave.animation.Easing;
import tytoo.weave.component.components.display.Image;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.component.components.layout.Panel;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.state.State;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.Styling;
import tytoo.weave.utils.ImageManager;
import tytoo.weave.utils.McUtils;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class TestGui extends WeaveScreen {

    private final State<String> textFieldState = new State<>("https://picsum.photos/200");

    public TestGui() {
        super(Text.literal("Test GUI"));

        window.setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.START, 5));
        window.setPadding(10);

        Panel titlePanel = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.childBased(10))
                .setLayout(LinearLayout.of(LinearLayout.Orientation.HORIZONTAL, LinearLayout.Alignment.CENTER));

        TextComponent titleText = TextComponent.of("Weave Test UI")
                .setStyle(Styling.create()
                        .color(Color.BLACK)
                        .shadow(true)
                        .colorWave(new ColorWave(ColorWave.createRainbow(36), 2f)))
                .setScale(1.5f);

        titlePanel.addChildren(titleText);

        Panel contentPanel = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setPadding(10)
                .setLayout(LinearLayout.of(LinearLayout.Orientation.VERTICAL, LinearLayout.Alignment.CENTER, 5))
                .setLayoutData(LinearLayout.Data.grow(1));

        Image imageDisplay = Image.from(ImageManager.getPlaceholder())
                .setWidth(Constraints.pixels(128))
                .setHeight(Constraints.pixels(128));

        TextField urlInput = TextField.create()
                .bindText(textFieldState)
                .setWidth(Constraints.relative(1.0f));

        Button loadButton = Button.of("Load Image")
                .setWidth(Constraints.relative(1.0f))
                .onClick(button -> {
                    try {
                        URL url = new URI(textFieldState.get()).toURL();
                        imageDisplay.setImage(ImageManager.getPlaceholder()).setColor(Color.WHITE);

                        imageDisplay.animate()
                                .duration(500L).easing(Easing.EASE_OUT_SINE).scale(1.5f).opacity(1f)
                                .then(() -> imageDisplay.animate().duration(1000L).easing(Easing.EASE_IN_SINE).scale(0.5f).opacity(0.2f));

                        ImageManager.forceFetchIdentifierForUrl(url).whenCompleteAsync((id, throwable) -> {
                            if (throwable != null) {
                                WeaveClient.LOGGER.error("Failed to load image from URL {}.", url, throwable);
                                imageDisplay.setImage(ImageManager.getPlaceholder()).setColor(Color.WHITE);
                            } else {
                                imageDisplay.setImage(id).setColor(Color.WHITE);
                            }
                            imageDisplay.setOpacity(1.0f);
                            imageDisplay.setScale(1.0f);
                        }, McUtils.getMc().orElseThrow());

                    } catch (MalformedURLException | URISyntaxException e) {
                        WeaveClient.LOGGER.warn("Invalid URL: {}", textFieldState.get(), e);
                        imageDisplay.setImage(ImageManager.getPlaceholder()).setColor(Color.WHITE);
                    }
                });

        contentPanel.addChildren(imageDisplay, urlInput, loadButton);
        window.addChildren(titlePanel, contentPanel);
    }
}