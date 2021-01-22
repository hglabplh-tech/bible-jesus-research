package org.harry.jesus.mosedbmodel;

/**
 * The type Verses.
 */
public class Verses {
    private Integer bible;

    private Integer bibleBook;

    private Integer chapter;

    private  Integer vers;

    private String note;

    /**
     * Gets bible.
     *
     * @return the bible
     */
    public Integer getBible() {
        return bible;
    }

    /**
     * Sets bible.
     *
     * @param bible the bible
     * @return the bible
     */
    public Verses setBible(Integer bible) {
        this.bible = bible;
        return this;
    }

    /**
     * Gets bible book.
     *
     * @return the bible book
     */
    public Integer getBibleBook() {
        return bibleBook;
    }

    /**
     * Sets bible book.
     *
     * @param bibleBook the bible book
     * @return the bible book
     */
    public Verses setBibleBook(Integer bibleBook) {
        this.bibleBook = bibleBook;
        return this;
    }

    /**
     * Gets chapter.
     *
     * @return the chapter
     */
    public Integer getChapter() {
        return chapter;
    }

    /**
     * Sets chapter.
     *
     * @param chapter the chapter
     * @return the chapter
     */
    public Verses setChapter(Integer chapter) {
        this.chapter = chapter;
        return this;
    }

    /**
     * Gets vers.
     *
     * @return the vers
     */
    public Integer getVers() {
        return vers;
    }

    /**
     * Sets vers.
     *
     * @param vers the vers
     * @return the vers
     */
    public Verses setVers(Integer vers) {
        this.vers = vers;
        return this;
    }

    /**
     * Gets note.
     *
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets note.
     *
     * @param note the note
     * @return the note
     */
    public Verses setNote(String note) {
        this.note = note;
        return this;
    }
}
