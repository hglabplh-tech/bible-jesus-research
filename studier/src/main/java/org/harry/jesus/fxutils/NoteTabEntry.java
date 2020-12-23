package org.harry.jesus.fxutils;

import javafx.beans.property.SimpleStringProperty;

public class NoteTabEntry {
    private final SimpleStringProperty verseLink = new SimpleStringProperty("");
    private final SimpleStringProperty versText = new SimpleStringProperty("");
    private final SimpleStringProperty noteText = new SimpleStringProperty("");


    public NoteTabEntry () {
        this("", "", "");
    }

    public NoteTabEntry (String verseLink, String versText, String noteText) {
        setVerseLink(verseLink);
        setVersText(versText);
        setNoteText(noteText);
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

    public String getNoteText() {
        return noteText.get();
    }

    public void setNoteText(String fName) {
        noteText.set(fName);
    }
}
