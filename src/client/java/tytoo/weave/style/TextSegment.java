package tytoo.weave.style;

public class TextSegment {
    private final String text;
    private final Styling hoverStyling;
    private Styling styling;

    public TextSegment(String text, Styling styling) {
        this(text, styling, null);
    }

    public TextSegment(String text, Styling styling, Styling hoverStyling) {
        this.text = text;
        this.styling = styling;
        this.hoverStyling = hoverStyling;
    }

    public void setStyling(Styling styling) {
        this.styling = styling;
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