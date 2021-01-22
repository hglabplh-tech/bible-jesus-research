package org.harry.jesus.jesajautils.browse;
import javafx.scene.Node;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.StyledTextArea;
import org.fxmisc.richtext.TextExt;
import org.fxmisc.richtext.model.*;
import org.harry.jesus.fxutils.event.SearchDictEvent;
import org.reactfx.util.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * The type Foldable styled area.
 */
public class FoldableStyledArea extends GenericStyledArea<ParStyle, Either<String, LinkedImage>, TextStyle>
{
    private final static TextOps<String, TextStyle> styledTextOps = SegmentOps.styledTextOps();
    private final static LinkedImageOps<TextStyle> linkedImageOps = new LinkedImageOps<>();

    private List<TextField> searchInputFields = new ArrayList<>();


    /**
     * Instantiates a new Foldable styled area.
     */
    public FoldableStyledArea()
    {
        super(
                ParStyle.EMPTY,                                                 // default paragraph style
                (paragraph, style) -> paragraph.setStyle(style.toCss()),        // paragraph style setter
                TextStyle.EMPTY.updateFontSize(12).updateFontFamily("Serif").updateTextColor(Color.BLACK),  // default segment style
                styledTextOps._or(linkedImageOps, (s1, s2) -> Optional.empty()),                            // segment operations
                seg -> createNode(seg, (text, style) -> text.setStyle(style.toCss())));                     // Node creator and segment style setter
    }

    private static Node createNode(StyledSegment<Either<String, LinkedImage>, TextStyle> seg,
                                   BiConsumer<? super TextExt, TextStyle> applyStyle ) {
        return seg.getSegment().unify(
                text -> StyledTextArea.createStyledTextNode(text, seg.getStyle(), applyStyle),
                LinkedImage::createNode
        );
    }

    /**
     * Emit searxch to all.
     */
    public void emitSearxchToAll() {
        String query = this.getSelectedText();
        SearchDictEvent event = new SearchDictEvent(query);
        for(TextField field : getLinkedSearchTextFields()) {
            field.fireEvent(event);
        }
    }

    /**
     * Add linked search text field.
     *
     * @param searchField the search field
     */
    public void addLinkedSearchTextField(TextField searchField) {
        searchInputFields.add(searchField);
    }

    /**
     * Gets linked search text fields.
     *
     * @return the linked search text fields
     */
    public List<TextField> getLinkedSearchTextFields() {
        return searchInputFields;
    }

    /**
     * Gets add fold style.
     *
     * @return the add fold style
     */
    public UnaryOperator<ParStyle> getAddFoldStyle() {
        return pstyle -> pstyle.updateFold( true );
    }

    /**
     * Gets remove fold style.
     *
     * @return the remove fold style
     */
    public UnaryOperator<ParStyle> getRemoveFoldStyle() {
        return pstyle -> pstyle.updateFold( false );
    }

    /**
     * Gets fold style check.
     *
     * @return the fold style check
     */
    public Predicate<ParStyle> getFoldStyleCheck() {
        return pstyle -> pstyle.isFolded();
    }

    private void updateStyleInSelection(TextStyle mixin) {
        IndexRange selection = this.getSelection();
        if (selection.getLength() != 0) {
            StyleSpans<TextStyle> styles = this.getStyleSpans(selection);
            StyleSpans<TextStyle> newStyles = styles.mapStyles(style -> style.updateWith(mixin));
            this.setStyleSpans(selection.getStart(), newStyles);
        }
    }

    private void updateParagraphStyleInSelection(Function<ParStyle, ParStyle> updater) {
        IndexRange selection = this.getSelection();
        int startPar = this.offsetToPosition(selection.getStart(), Bias.Forward).getMajor();
        int endPar = this.offsetToPosition(selection.getEnd(), Bias.Backward).getMajor();
        for(int i = startPar; i <= endPar; ++i) {
            Paragraph<ParStyle, Either<String, LinkedImage>, TextStyle> paragraph = this.getParagraph(i);
            this.setParagraphStyle(i, updater.apply(paragraph.getParagraphStyle()));
        }
    }

    /**
     * Sets par visable selection.
     */
    public void setParVisableSelection() {
        IndexRange selection = this.getSelection();
        int startPar = this.offsetToPosition(selection.getStart(), Bias.Forward).getMajor();
        int endPar = this.offsetToPosition(selection.getEnd(), Bias.Backward).getMajor();
        for(int i = startPar; i <= endPar; ++i) {
            this.showParagraphAtTop(i);
        }
    }

    private void updateParagraphStyleInSelection(ParStyle mixin) {
        updateParagraphStyleInSelection(style -> style.updateWith(mixin));
    }

    private void updateFontSize(Integer size) {

            updateStyleInSelection(TextStyle.fontSize(size));

    }

    private void updateFontFamily(String family) {

            updateStyleInSelection(TextStyle.fontFamily(family));

    }

    private void updateTextColor(Color color) {

            updateStyleInSelection(TextStyle.textColor(color));

    }

    private void updateBackgroundColor(Color color) {

            updateStyleInSelection(TextStyle.backgroundColor(color));

    }

    private void updateParagraphBackground(Color color) {

            updateParagraphStyleInSelection(ParStyle.backgroundColor(color));

    }

}

