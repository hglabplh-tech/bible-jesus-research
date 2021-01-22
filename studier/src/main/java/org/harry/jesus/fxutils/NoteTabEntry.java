package org.harry.jesus.fxutils;

import javafx.beans.property.SimpleStringProperty;

/**
 * The type Note tab entry.
 */
public class NoteTabEntry {
    private final SimpleStringProperty verseLink = new SimpleStringProperty("");
    private final SimpleStringProperty versText = new SimpleStringProperty("");
    private final SimpleStringProperty noteText = new SimpleStringProperty("");


    /**
     * Instantiates a new Note tab entry.
     */
    public NoteTabEntry () {
        this("", "", "");
    }

    /**
     * Instantiates a new Note tab entry.
     *
     * @param verseLink the verse link
     * @param versText  the vers text
     * @param noteText  the note text
     */
    public NoteTabEntry (String verseLink, String versText, String noteText) {
        setVerseLink(verseLink);
        setVersText(versText);
        setNoteText(noteText);
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

    /**
     * Gets note text.
     *
     * @return the note text
     */
    public String getNoteText() {
        return noteText.get();
    }

    /**
     * Sets note text.
     *
     * @param fName the f name
     */
    public void setNoteText(String fName) {
        noteText.set(fName);
    }
}
