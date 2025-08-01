package tytoo.weave.screen.screens;

import net.minecraft.text.Text;
import tytoo.weave.component.components.display.Image;
import tytoo.weave.component.components.display.TextComponent;
import tytoo.weave.component.components.interactive.Button;
import tytoo.weave.component.components.interactive.TextField;
import tytoo.weave.component.components.layout.*;
import tytoo.weave.constraint.constraints.Constraints;
import tytoo.weave.layout.LinearLayout;
import tytoo.weave.screen.WeaveScreen;
import tytoo.weave.state.State;
import tytoo.weave.style.ColorWave;
import tytoo.weave.style.Styling;

import java.net.URI;
import java.net.MalformedURLException;
import java.net.URL;



public class TestGui extends WeaveScreen {

    private final State<String> textFieldState = new State<>("https://picsum.photos/200");

    public TestGui() {
        super(Text.literal("Test GUI"));

        window.setWidth(Constraints.pixels(350));
        window.setHeight(Constraints.childBased());
        window.setLayout(LinearLayout.of(
                LinearLayout.Orientation.VERTICAL,
                LinearLayout.Alignment.START,
                10
        ));

        Panel titlePanel = createTitlePanel();

        Panel contentPanel = createContentPanel();

        window.addChildren(titlePanel, contentPanel);
    }

    private Panel createTitlePanel() {
        Panel titlePanel = Panel.create()
                .setWidth(Constraints.relative(1.0f))
                .setHeight(Constraints.pixels(50))
                .setPadding(10);

        ColorWave rainbowWave = new ColorWave(ColorWave.createRainbow(7), 2.0f);
        TextComponent titleText = TextComponent.of("Weave UI Test")
                .setX(Constraints.center())
                .setY(Constraints.center())
                .setScale(1.5f)
                .setStyle(Styling.create().colorWave(rainbowWave));


        titlePanel.addChild(titleText);
        return titlePanel;
    }

    private Panel createContentPanel() {
        Panel contentPanel = Panel.create()
                .setWidth(Constraints.relative(1f))
                .setHeight(Constraints.childBased(10))
                .setLayout(LinearLayout.of(
                        LinearLayout.Orientation.VERTICAL,
                        LinearLayout.Alignment.CENTER,
                        LinearLayout.CrossAxisAlignment.CENTER,
                        10
                ));

        Panel inputPanel = Panel.create()
                .setWidth(Constraints.relative(1f, -20))
                .setHeight(Constraints.childBased())
                .setLayout(LinearLayout.of(
                        LinearLayout.Orientation.HORIZONTAL,
                        LinearLayout.Alignment.CENTER,
                        5
                ));

        TextField urlInput = TextField.create()
                .setLayoutData(LinearLayout.Data.grow(1))
                .setPlaceholder("Enter Image URL...")
                .bindText(textFieldState);

        Panel imageHolderPanel = Panel.create()
                .setWidth(Constraints.pixels(200))
                .setHeight(Constraints.pixels(200));


        Button loadImageButton = Button.of("Load Image")
                .onClick(button -> {
                    try {
                        URL imageUrl = URI.create(textFieldState.get()).toURL();

                        imageHolderPanel.removeAllChildren();
                        Image newImage = Image.from(imageUrl).setWidth(Constraints.relative(1f)).setHeight(Constraints.relative(1f));
                        imageHolderPanel.addChild(newImage);
                    } catch (MalformedURLException | IllegalArgumentException e) {
                        imageHolderPanel.removeAllChildren();
                        System.err.println("Invalid URL: " + textFieldState.get());
                    }
                });

        inputPanel.addChildren(urlInput, loadImageButton);
        contentPanel.addChildren(inputPanel, imageHolderPanel);
        return contentPanel;
    }
}