package org.harry.jesus.fxutils;

import javafx.beans.property.SimpleStringProperty;

/**
 * The type Highlights entry.
 */
public class HighlightsEntry {
    private final SimpleStringProperty verseLink = new SimpleStringProperty("");
    private final SimpleStringProperty versText = new SimpleStringProperty("");


    /**
     * Instantiates a new Highlights entry.
     */
    public HighlightsEntry() {
        this("", "");
    }

    /**
     * Instantiates a new Highlights entry.
     *
     * @param verseLink the verse link
     * @param versText  the vers text
     */
    public HighlightsEntry(String verseLink, String versText) {
        setVerseLink(verseLink);
        setVersText(versText);
    }

    /**
     * Gets verse link.
     *
     * @return the verse link
     */
    public String getVerseLink() {
        return verseLink.get();
    }

    /**
     * Sets verse link.
     *
     * @param fName the f name
     */
    public void setVerseLink(String fName) {
        verseLink.set(fName);
    }

    /**
     * Gets vers text.
     *
     * @return the vers text
     */
    public String getVersText() {
        return versText.get();
    }

    /**
     * Sets vers text.
     *
     * @param fName the f name
     */
    public void setVersText(String fName) {
        versText.set(fName);
    }


}
