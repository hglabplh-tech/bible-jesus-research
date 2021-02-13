package org.harry.jesus.jesajautils;

import generated.*;

/**
 * The interface Render parser callback.
 */
public interface RenderParserCallback {

    /**
     * Text call back boolean.
     *
     * @param text the text
     * @return the boolean
     */
    boolean textCallBack(String text);

    /**
     * Gram callback boolean.
     *
     * @param prefix the prefix
     * @param gram   the gram
     * @param text   the text
     * @return the boolean
     */
    boolean gramCallback(String prefix, GRAM gram, String text);

    /**
     * Style callback boolean.
     *
     * @param style     the style
     * @param collected the collected
     * @return the boolean
     */
    boolean styleCallback(STYLE style, StringBuffer collected);

    /**
     * Before note callback boolean.
     *
     * @param note    the note
     * @param verseNo the verse no
     * @return the boolean
     */
    boolean beforeNoteCallback(NOTE note, Integer verseNo);

    /**
     * After note callback boolean.
     *
     * @param note    the note
     * @param verseNo the verse no
     * @return the boolean
     */
    boolean afterNoteCallback(NOTE note, Integer verseNo);

    /**
     * Before div callback boolean.
     *
     * @param div     the div
     * @param verseNo the verse no
     * @return the boolean
     */
    boolean beforeDivCallback(DIV div, Integer verseNo);

    /**
     * After div callback boolean.
     *
     * @param div     the div
     * @param verseNo the verse no
     * @return the boolean
     */
    boolean afterDivCallback(DIV div, Integer verseNo);

    /**
     * Break callback boolean.
     *
     * @param type  the type
     * @param count the count
     * @return the boolean
     */
    boolean breakCallback(BreakType type, Integer count);

    /**
     * Start caption boolean.
     *
     * @param captionType the caption type
     * @return the boolean
     */
    boolean  startCaption(CaptionType captionType);

    /**
     * End caption boolean.
     *
     * @param captionType the caption type
     * @return the boolean
     */
    boolean  endCaption(CaptionType captionType);
}
