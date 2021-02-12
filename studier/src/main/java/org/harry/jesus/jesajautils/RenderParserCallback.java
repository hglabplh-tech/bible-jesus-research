package org.harry.jesus.jesajautils;

import generated.*;

public interface RenderParserCallback {

    boolean textCallBack(String text);

    boolean gramCallback(String prefix, GRAM gram, String text);

    boolean styleCallback(STYLE style, StringBuffer collected);

    boolean beforeNoteCallback(NOTE note, Integer verseNo);

    boolean afterNoteCallback(NOTE note, Integer verseNo);

    boolean beforeDivCallback(DIV div, Integer verseNo);

    boolean afterDivCallback(DIV div, Integer verseNo);

    boolean breakCallback(BreakType type, Integer count);

    boolean  startCaption(CaptionType captionType);

    boolean  endCaption(CaptionType captionType);
}
