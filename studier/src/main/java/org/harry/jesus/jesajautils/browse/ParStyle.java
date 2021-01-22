package org.harry.jesus.jesajautils.browse;

import static javafx.scene.text.TextAlignment.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import org.fxmisc.richtext.model.Codec;
import org.harry.jesus.jesajautils.browse.TextStyle;

/**
 * Holds information about the style of a paragraph.
 */
public class ParStyle {

    /**
     * The constant EMPTY.
     */
    public static final ParStyle EMPTY = new ParStyle();

    /**
     * The constant CODEC.
     */
    public static final Codec<ParStyle> CODEC = new Codec<ParStyle>() {

        private final Codec<Optional<TextAlignment>> OPT_ALIGNMENT_CODEC =
                Codec.optionalCodec(Codec.enumCodec(TextAlignment.class));
        private final Codec<Optional<Color>> OPT_COLOR_CODEC =
                Codec.optionalCodec(Codec.COLOR_CODEC);

        @Override
        public String getName() {
            return "par-style";
        }

        @Override
        public void encode(DataOutputStream os, ParStyle t) throws IOException {
            OPT_ALIGNMENT_CODEC.encode(os, t.alignment);
            OPT_COLOR_CODEC.encode(os, t.backgroundColor);
            os.writeInt( t.indent.map( i -> Integer.valueOf( i.level ) ).orElse( 0 ) );
            os.writeInt( t.foldCount );
        }

        @Override
        public ParStyle decode(DataInputStream is) throws IOException {
            return new ParStyle(
                    OPT_ALIGNMENT_CODEC.decode(is),
                    OPT_COLOR_CODEC.decode(is),
                    Optional.of( new Indent( is.readInt() ) ),
                    is.readInt() );
        }

    };

    /**
     * Align left par style.
     *
     * @return the par style
     */
    public static ParStyle alignLeft() { return EMPTY.updateAlignment(LEFT); }

    /**
     * Align center par style.
     *
     * @return the par style
     */
    public static ParStyle alignCenter() { return EMPTY.updateAlignment(CENTER); }

    /**
     * Align right par style.
     *
     * @return the par style
     */
    public static ParStyle alignRight() { return EMPTY.updateAlignment(RIGHT); }

    /**
     * Align justify par style.
     *
     * @return the par style
     */
    public static ParStyle alignJustify() { return EMPTY.updateAlignment(JUSTIFY); }

    /**
     * Background color par style.
     *
     * @param color the color
     * @return the par style
     */
    public static ParStyle backgroundColor(Color color) { return EMPTY.updateBackgroundColor(color); }

    /**
     * Folded par style.
     *
     * @return the par style
     */
    public static ParStyle folded() { return EMPTY.updateFold(Boolean.TRUE); }

    /**
     * Unfolded par style.
     *
     * @return the par style
     */
    public static ParStyle unfolded() { return EMPTY.updateFold(Boolean.FALSE); }

    /**
     * The Alignment.
     */
    final Optional<TextAlignment> alignment;
    /**
     * The Background color.
     */
    final Optional<Color> backgroundColor;
    /**
     * The Indent.
     */
    final Optional<Indent> indent;
    /**
     * The Fold count.
     */
    final int foldCount;

    private ParStyle() {
        this(Optional.empty(), Optional.empty(), Optional.empty(), 0);
    }

    private ParStyle(Optional<TextAlignment> alignment, Optional<Color> backgroundColor, Optional<Indent> indent, int folds) {
        this.alignment = alignment;
        this.backgroundColor = backgroundColor;
        this.foldCount = folds;
        this.indent = indent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(alignment, backgroundColor, indent, foldCount);
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof ParStyle) {
            ParStyle that = (ParStyle) other;
            return Objects.equals(this.alignment, that.alignment) &&
                    Objects.equals(this.backgroundColor, that.backgroundColor) &&
                    Objects.equals(this.indent, that.indent) &&
                    this.foldCount == that.foldCount;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return toCss();
    }

    /**
     * To css string.
     *
     * @return the string
     */
    public String toCss() {
        StringBuilder sb = new StringBuilder();

        alignment.ifPresent(al -> {
            String cssAlignment;
            switch(al) {
                case LEFT:    cssAlignment = "left";    break;
                case CENTER:  cssAlignment = "center";  break;
                case RIGHT:   cssAlignment = "right";   break;
                case JUSTIFY: cssAlignment = "justify"; break;
                default: throw new AssertionError("unreachable code");
            }
            sb.append("-fx-text-alignment: " + cssAlignment + ";");
        });

        backgroundColor.ifPresent(color -> {
            sb.append("-fx-background-color: " + TextStyle.cssColor(color) + ";");
        });

        if ( foldCount > 0 ) sb.append( "visibility: collapse;" );

        return sb.toString();
    }

    /**
     * Update with par style.
     *
     * @param mixin the mixin
     * @return the par style
     */
    public ParStyle updateWith(ParStyle mixin) {
        return new ParStyle(
                mixin.alignment.isPresent() ? mixin.alignment : alignment,
                mixin.backgroundColor.isPresent() ? mixin.backgroundColor : backgroundColor,
                mixin.indent.isPresent() ? mixin.indent : indent,
                mixin.foldCount + foldCount);
    }

    /**
     * Update alignment par style.
     *
     * @param alignment the alignment
     * @return the par style
     */
    public ParStyle updateAlignment(TextAlignment alignment) {
        return new ParStyle(Optional.of(alignment), backgroundColor, indent, foldCount);
    }

    /**
     * Update background color par style.
     *
     * @param backgroundColor the background color
     * @return the par style
     */
    public ParStyle updateBackgroundColor(Color backgroundColor) {
        return new ParStyle(alignment, Optional.of(backgroundColor), indent, foldCount);
    }

    /**
     * Update indent par style.
     *
     * @param indent the indent
     * @return the par style
     */
    public ParStyle updateIndent(Indent indent) {
        return new ParStyle(alignment, backgroundColor, Optional.ofNullable(indent), foldCount);
    }

    /**
     * Increase indent par style.
     *
     * @return the par style
     */
    public ParStyle increaseIndent() {
        return updateIndent( indent.map( Indent::increase ).orElseGet( Indent::new ) );
    }

    /**
     * Decrease indent par style.
     *
     * @return the par style
     */
    public ParStyle decreaseIndent() {
        return updateIndent( indent.filter( in -> in.level > 1 )
                .map( Indent::decrease ).orElse( null ) );
    }

    /**
     * Gets indent.
     *
     * @return the indent
     */
    public Indent getIndent() {
        return indent.get();
    }

    /**
     * Is indented boolean.
     *
     * @return the boolean
     */
    public boolean isIndented() {
        return indent.map( in -> in.level > 0 ).orElse( false );
    }

    /**
     * Update fold par style.
     *
     * @param fold the fold
     * @return the par style
     */
    public ParStyle updateFold(boolean fold) {
        int foldLevels = fold ? foldCount+1 : Math.max( 0, foldCount-1 );
        return new ParStyle(alignment, backgroundColor, indent, foldLevels);
    }

    /**
     * Is folded boolean.
     *
     * @return the boolean
     */
    public boolean isFolded() {
        return foldCount > 0;
    }
}
