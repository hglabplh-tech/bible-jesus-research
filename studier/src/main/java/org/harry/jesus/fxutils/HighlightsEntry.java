package org.harry.jesus.fxutils;

import javafx.beans.property.SimpleStringProperty;

public class HighlightsEntry {
    private final SimpleStringProperty verseLink = new SimpleStringProperty("");
    private final SimpleStringProperty versText = new SimpleStringProperty("");


    public HighlightsEntry() {
        this("", "");
    }

    public HighlightsEntry(String verseLink, String versText) {
        setVerseLink(verseLink);
        setVersText(versText);
    }

    public String getVerseLink() {
        return verseLink.get();
    }

    public void setVerseLink(String fName) {
        verseLink.set(fName);
    }

    public String getVersText() {
        return versText.get();
    }

    public void setVersText(String fName) {
        versText.set(fName);
    }


}
