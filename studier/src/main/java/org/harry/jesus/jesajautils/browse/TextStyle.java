package org.harry.jesus.jesajautils.browse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.scene.paint.Color;

import org.fxmisc.richtext.model.Codec;

/**
 * Holds information about the style of a text fragment.
 */
public class TextStyle {

    /**
     * The constant CSS_BOLD.
     */
    public final static String CSS_BOLD = "font-style:bold";

    /**
     * The constant CSS_ITALIC.
     */
    public final static String CSS_ITALIC = "font-style:italic";

    /**
     * The constant EMPTY.
     */
    public static final TextStyle EMPTY = new TextStyle();

    /**
     * The constant CODEC.
     */
    public static final Codec<TextStyle> CODEC = new Codec<TextStyle>() {

        private final Codec<Optional<String>> OPT_STRING_CODEC =
                Codec.optionalCodec(Codec.STRING_CODEC);
        private final Codec<Optional<Color>> OPT_COLOR_CODEC =
                Codec.optionalCodec(Codec.COLOR_CODEC);



        @Override
        public String getName() {
            return "text-style";
        }

        @Override
        public void encode(DataOutputStream os, TextStyle s)
                throws IOException {
            os.writeByte(encodeBoldItalicUnderlineStrikethrough(s));
            os.writeInt(encodeOptionalUint(s.fontSize));
            OPT_STRING_CODEC.encode(os, s.fontFamily);
            OPT_COLOR_CODEC.encode(os, s.textColor);
            OPT_COLOR_CODEC.encode(os, s.backgroundColor);
        }

        @Override
        public TextStyle decode(DataInputStream is) throws IOException {
            byte bius = is.readByte();
            Optional<Integer> fontSize = decodeOptionalUint(is.readInt());
            Optional<String> fontFamily = OPT_STRING_CODEC.decode(is);
            Optional<Color> textColor = OPT_COLOR_CODEC.decode(is);
            Optional<Color> bgrColor = OPT_COLOR_CODEC.decode(is);
            return new TextStyle(
                    bold(bius), italic(bius), underline(bius), strikethrough(bius),
                    fontSize, fontFamily, textColor, bgrColor);
        }

        private int encodeBoldItalicUnderlineStrikethrough(TextStyle s) {
            return encodeOptionalBoolean(s.bold) << 6 |
                    encodeOptionalBoolean(s.italic) << 4 |
                    encodeOptionalBoolean(s.underline) << 2 |
                    encodeOptionalBoolean(s.strikethrough);
        }

        private Optional<Boolean> bold(byte bius) throws IOException {
            return decodeOptionalBoolean((bius >> 6) & 3);
        }

        private Optional<Boolean> italic(byte bius) throws IOException {
            return decodeOptionalBoolean((bius >> 4) & 3);
        }

        private Optional<Boolean> underline(byte bius) throws IOException {
            return decodeOptionalBoolean((bius >> 2) & 3);
        }

        private Optional<Boolean> strikethrough(byte bius) throws IOException {
            return decodeOptionalBoolean((bius >> 0) & 3);
        }

        private int encodeOptionalBoolean(Optional<Boolean> ob) {
            return ob.map(b -> 2 + (b ? 1 : 0)).orElse(0);
        }

        private Optional<Boolean> decodeOptionalBoolean(int i) throws IOException {
            switch(i) {
                case 0: return Optional.empty();
                case 2: return Optional.of(false);
                case 3: return Optional.of(true);
            }
            throw new MalformedInputException(0);
        }

        private int encodeOptionalUint(Optional<Integer> oi) {
            return oi.orElse(-1);
        }

        private Optional<Integer> decodeOptionalUint(int i) {
            return (i < 0) ? Optional.empty() : Optional.of(i);
        }
    };


    /**
     * Bold text style.
     *
     * @param bold the bold
     * @return the text style
     */
    public static TextStyle bold(boolean bold) { return EMPTY.updateBold(bold); }

    /**
     * Italic text style.
     *
     * @param italic the italic
     * @return the text style
     */
    public static TextStyle italic(boolean italic) { return EMPTY.updateItalic(italic); }

    /**
     * Underline text style.
     *
     * @param underline the underline
     * @return the text style
     */
    public static TextStyle underline(boolean underline) { return EMPTY.updateUnderline(underline); }

    /**
     * Strikethrough text style.
     *
     * @param strikethrough the strikethrough
     * @return the text style
     */
    public static TextStyle strikethrough(boolean strikethrough) { return EMPTY.updateStrikethrough(strikethrough); }

    /**
     * Font size text style.
     *
     * @param fontSize the font size
     * @return the text style
     */
    public static TextStyle fontSize(int fontSize) { return EMPTY.updateFontSize(fontSize); }

    /**
     * Font family text style.
     *
     * @param family the family
     * @return the text style
     */
    public static TextStyle fontFamily(String family) { return EMPTY.updateFontFamily(family); }

    /**
     * Text color text style.
     *
     * @param color the color
     * @return the text style
     */
    public static TextStyle textColor(Color color) { return EMPTY.updateTextColor(color); }

    /**
     * Background color text style.
     *
     * @param color the color
     * @return the text style
     */
    public static TextStyle backgroundColor(Color color) { return EMPTY.updateBackgroundColor(color); }

    /**
     * Css color string.
     *
     * @param color the color
     * @return the string
     */
    static String cssColor(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        return "rgb(" + red + ", " + green + ", " + blue + ")";
    }

    /**
     * The Bold.
     */
    final Optional<Boolean> bold;
    /**
     * The Italic.
     */
    final Optional<Boolean> italic;
    /**
     * The Underline.
     */
    final Optional<Boolean> underline;
    /**
     * The Strikethrough.
     */
    final Optional<Boolean> strikethrough;
    /**
     * The Font size.
     */
    final Optional<Integer> fontSize;
    /**
     * The Font family.
     */
    final Optional<String> fontFamily;
    /**
     * The Text color.
     */
    final Optional<Color> textColor;
    /**
     * The Background color.
     */
    final Optional<Color> backgroundColor;

    /**
     * Instantiates a new Text style.
     */
    public TextStyle() {
        this(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }

    /**
     * Instantiates a new Text style.
     *
     * @param bold            the bold
     * @param italic          the italic
     * @param underline       the underline
     * @param strikethrough   the strikethrough
     * @param fontSize        the font size
     * @param fontFamily      the font family
     * @param textColor       the text color
     * @param backgroundColor the background color
     */
    public TextStyle(
            Optional<Boolean> bold,
            Optional<Boolean> italic,
            Optional<Boolean> underline,
            Optional<Boolean> strikethrough,
            Optional<Integer> fontSize,
            Optional<String> fontFamily,
            Optional<Color> textColor,
            Optional<Color> backgroundColor) {
        this.bold = bold;
        this.italic = italic;
        this.underline = underline;
        this.strikethrough = strikethrough;
        this.fontSize = fontSize;
        this.fontFamily = fontFamily;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                bold, italic, underline, strikethrough,
                fontSize, fontFamily, textColor, backgroundColor);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof TextStyle) {
            TextStyle that = (TextStyle) other;
            return Objects.equals(this.bold,            that.bold) &&
                    Objects.equals(this.italic,          that.italic) &&
                    Objects.equals(this.underline,       that.underline) &&
                    Objects.equals(this.strikethrough,   that.strikethrough) &&
                    Objects.equals(this.fontSize,        that.fontSize) &&
                    Objects.equals(this.fontFamily,      that.fontFamily) &&
                    Objects.equals(this.textColor,       that.textColor) &&
                    Objects.equals(this.backgroundColor, that.backgroundColor);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        List<String> styles = new ArrayList<>();

        bold           .ifPresent(b -> styles.add(b.toString()));
        italic         .ifPresent(i -> styles.add(i.toString()));
        underline      .ifPresent(u -> styles.add(u.toString()));
        strikethrough  .ifPresent(s -> styles.add(s.toString()));
        fontSize       .ifPresent(s -> styles.add(s.toString()));
        fontFamily     .ifPresent(f -> styles.add(f.toString()));
        textColor      .ifPresent(c -> styles.add(c.toString()));
        backgroundColor.ifPresent(b -> styles.add(b.toString()));

        return String.join(",", styles);
    }

    /**
     * To css string.
     *
     * @return the string
     */
    public String toCss() {
        StringBuilder sb = new StringBuilder();

        if(bold.isPresent()) {
            if(bold.get()) {
                sb.append("-fx-font-weight: bold;");
            } else {
                sb.append("-fx-font-weight: normal;");
            }
        }

        if(italic.isPresent()) {
            if(italic.get()) {
                sb.append("-fx-font-style: italic;");
            } else {
                sb.append("-fx-font-style: normal;");
            }
        }

        if(underline.isPresent()) {
            if(underline.get()) {
                sb.append("-fx-underline: true;");
            } else {
                sb.append("-fx-underline: false;");
            }
        }

        if(strikethrough.isPresent()) {
            if(strikethrough.get()) {
                sb.append("-fx-strikethrough: true;");
            } else {
                sb.append("-fx-strikethrough: false;");
            }
        }

        if(fontSize.isPresent()) {
            sb.append("-fx-font-size: " + fontSize.get() + "pt;");
        }

        if(fontFamily.isPresent()) {
            sb.append("-fx-font-family: " + fontFamily.get() + ";");
        }

        if(textColor.isPresent()) {
            Color color = textColor.get();
            sb.append("-fx-fill: " + cssColor(color) + ";");
        }

        if(backgroundColor.isPresent()) {
            Color color = backgroundColor.get();
            sb.append("-rtfx-background-color: " + cssColor(color) + ";");
        }

        return sb.toString();
    }

    /**
     * From css text style.
     *
     * @param styleString the style string
     * @return the text style
     */
    public static TextStyle fromCss(String styleString) {
        TextStyle style = new  TextStyle();
        if(styleString.contains(CSS_BOLD) || styleString.contains("-fx-font-weight: bold")) {
            style = TextStyle.bold(true);
        } else  if(styleString.contains(CSS_ITALIC) || styleString.contains("-fx-font-weight: italic")) {
            style = TextStyle.italic(true);
        }


        return style;
    }


    /**
     * Update with text style.
     *
     * @param mixin the mixin
     * @return the text style
     */
    public TextStyle updateWith(TextStyle mixin) {
        return new TextStyle(
                mixin.bold.isPresent() ? mixin.bold : bold,
                mixin.italic.isPresent() ? mixin.italic : italic,
                mixin.underline.isPresent() ? mixin.underline : underline,
                mixin.strikethrough.isPresent() ? mixin.strikethrough : strikethrough,
                mixin.fontSize.isPresent() ? mixin.fontSize : fontSize,
                mixin.fontFamily.isPresent() ? mixin.fontFamily : fontFamily,
                mixin.textColor.isPresent() ? mixin.textColor : textColor,
                mixin.backgroundColor.isPresent() ? mixin.backgroundColor : backgroundColor);
    }

    /**
     * Update bold text style.
     *
     * @param bold the bold
     * @return the text style
     */
    public TextStyle updateBold(boolean bold) {
        return new TextStyle(Optional.of(bold), italic, underline, strikethrough, fontSize, fontFamily, textColor, backgroundColor);
    }

    /**
     * Update italic text style.
     *
     * @param italic the italic
     * @return the text style
     */
    public TextStyle updateItalic(boolean italic) {
        return new TextStyle(bold, Optional.of(italic), underline, strikethrough, fontSize, fontFamily, textColor, backgroundColor);
    }

    /**
     * Update underline text style.
     *
     * @param underline the underline
     * @return the text style
     */
    public TextStyle updateUnderline(boolean underline) {
        return new TextStyle(bold, italic, Optional.of(underline), strikethrough, fontSize, fontFamily, textColor, backgroundColor);
    }

    /**
     * Update strikethrough text style.
     *
     * @param strikethrough the strikethrough
     * @return the text style
     */
    public TextStyle updateStrikethrough(boolean strikethrough) {
        return new TextStyle(bold, italic, underline, Optional.of(strikethrough), fontSize, fontFamily, textColor, backgroundColor);
    }

    /**
     * Update font size text style.
     *
     * @param fontSize the font size
     * @return the text style
     */
    public TextStyle updateFontSize(int fontSize) {
        return new TextStyle(bold, italic, underline, strikethrough, Optional.of(fontSize), fontFamily, textColor, backgroundColor);
    }

    /**
     * Update font family text style.
     *
     * @param fontFamily the font family
     * @return the text style
     */
    public TextStyle updateFontFamily(String fontFamily) {
        return new TextStyle(bold, italic, underline, strikethrough, fontSize, Optional.of(fontFamily), textColor, backgroundColor);
    }

    /**
     * Update text color text style.
     *
     * @param textColor the text color
     * @return the text style
     */
    public TextStyle updateTextColor(Color textColor) {
        return new TextStyle(bold, italic, underline, strikethrough, fontSize, fontFamily, Optional.of(textColor), backgroundColor);
    }

    /**
     * Update background color text style.
     *
     * @param backgroundColor the background color
     * @return the text style
     */
    public TextStyle updateBackgroundColor(Color backgroundColor) {
        return new TextStyle(bold, italic, underline, strikethrough, fontSize, fontFamily, textColor, Optional.of(backgroundColor));
    }

    /**
     * Gets text color.
     *
     * @return the text color
     */
    public Optional<Color> getTextColor() {
        return textColor;
    }

    /**
     * Gets background color.
     *
     * @return the background color
     */
    public Optional<Color> getBackgroundColor() {
        return backgroundColor;
    }
}