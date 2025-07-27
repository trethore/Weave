package tytoo.weave.style;

public class TextSegment {
    private final String text;
    private final Styling styling;
    private final Styling hoverStyling;

    public TextSegment(String text, Styling styling) {
        this(text, styling, null);
    }

    public TextSegment(String text, Styling styling, Styling hoverStyling) {
        this.text = text;
        this.styling = styling;
        this.hoverStyling = hoverStyling;
    }

    public String getText() {
        return text;
    }

    public Styling getFormatting() {
        return styling;
    }

    public Styling getHoverStyling() {
        return hoverStyling;
    }
}