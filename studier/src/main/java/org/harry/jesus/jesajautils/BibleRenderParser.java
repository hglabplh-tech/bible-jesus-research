package org.harry.jesus.jesajautils;

import generated.*;
import org.harry.jesus.jesajautils.fulltext.BibleFulltextEngine;

import javax.xml.bind.JAXBElement;
import java.util.List;

public class BibleRenderParser {

    private final RenderParserCallback handler;

    public  BibleRenderParser(RenderParserCallback handler) {
        this.handler = handler;
    }

    /**
     * Gets vers entry.
     *
     * @param chapter the chapter
     * @param key     the key
     * @param getAll
     * @return the vers entry
     */
    public void getVersORCaptionOrEntry(CHAPTER chapter, BibleFulltextEngine.BibleTextKey key, Boolean getAll) {
        StringBuffer buffer = new StringBuffer();
        Integer versNo = 1;
        for (JAXBElement jaxbElement : chapter.getPROLOGOrCAPTIONOrVERS()) {
            if (getAll) {
                if (jaxbElement.getValue() instanceof PROLOG) {
                    PROLOG prolog = (PROLOG) jaxbElement.getValue();
                    for (Object content: prolog.getContent()) {
                        parseContent(content, key.getBook(), versNo);
                    }
                } else if (jaxbElement.getValue() instanceof CAPTION) {
                    CAPTION caption = (CAPTION) jaxbElement.getValue();
                    CaptionType captionType = caption.getType();
                    handler.startCaption(captionType);
                    for (Object content: caption.getContent()) {
                        parseContent(content, key.getBook(), versNo);
                    }
                    handler.endCaption(captionType);
                }
            }
            if (jaxbElement.getValue() instanceof VERS) {
                VERS vers = (VERS) jaxbElement.getValue();
                versNo = vers.getVnumber().intValue();
                if (versNo.equals(key.getVers())) {
                    for (Object content : vers.getContent()) {
                        parseContent(content, key.getBook().intValue(), key.getVers().intValue());
                    }
                }
            }
        }
    }
    private void parseContent (Object content, Integer book, Integer verse) {
        if (content instanceof String) {
            String text = (String) content;
            handler.textCallBack(text);
        } else if (content instanceof JAXBElement) {
            Class jaxbClazz = ((JAXBElement<?>) content).getDeclaredType();
            if (jaxbClazz.getName().equals(STYLE.class.getName())) {
                StringBuffer styleBuffer = new StringBuffer();
                STYLE styled = (STYLE) ((JAXBElement<?>) content).getValue();
                for (Object styledContent : styled.getContent()) {
                    if (styledContent instanceof String) {
                        String text = (String) styledContent;
                        styleBuffer.append(text);
                    }

                }
                handler.styleCallback(styled, styleBuffer);
            } else if (jaxbClazz.getName().equals(GRAM.class.getName())) {
                String prefix;
                if (book < 40) {
                    prefix = "H";
                } else {
                    prefix = "G";
                }
                GRAM gram = (GRAM) ((JAXBElement<?>) content).getValue();
                String text = null;
                for (Object gramContent : gram.getContent()) {

                    if (gramContent instanceof String) {
                        text = (String) gramContent;
                    }
                }
                handler.gramCallback(prefix, gram, text);
            } else if (jaxbClazz.getName().equals(DIV.class.getName()) ) {
                DIV div = (DIV)((JAXBElement<?>) content).getValue();
                handler.beforeDivCallback(div, verse);
                List<Object> divContents = div.getContent();
                for (Object divCont: divContents) {
                    if (divCont instanceof JAXBElement) {
                        parseContent(divCont, book, verse);
                    }
                }
                handler.afterDivCallback(div, verse);
            } else if (jaxbClazz.getName().equals(NOTE.class.getName()) ) {
                    NOTE note = (NOTE)((JAXBElement<?>) content).getValue();
                    handler.beforeNoteCallback(note, verse);
                    for (Object noteContent : note.getContent()) {
                        parseContent(noteContent, book, verse);
                    }
                    handler.afterNoteCallback(note, verse);
            } else if (jaxbClazz.getName().equals(BR.class.getName()) ) {
                    BR br = (BR)((JAXBElement<?>) content).getValue();
                    handler.breakCallback(br.getArt(), br.getCount().intValue());

            }

        }

    }

}

