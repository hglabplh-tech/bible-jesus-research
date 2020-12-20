package org.harry.jesus.mosedbmodel;

public class Verses {
    private Integer bible;

    private Integer bibleBook;

    private Integer chapter;

    private  Integer vers;

    private String note;

    public Integer getBible() {
        return bible;
    }

    public Verses setBible(Integer bible) {
        this.bible = bible;
        return this;
    }

    public Integer getBibleBook() {
        return bibleBook;
    }

    public Verses setBibleBook(Integer bibleBook) {
        this.bibleBook = bibleBook;
        return this;
    }

    public Integer getChapter() {
        return chapter;
    }

    public Verses setChapter(Integer chapter) {
        this.chapter = chapter;
        return this;
    }

    public Integer getVers() {
        return vers;
    }

    public Verses setVers(Integer vers) {
        this.vers = vers;
        return this;
    }

    public String getNote() {
        return note;
    }

    public Verses setNote(String note) {
        this.note = note;
        return this;
    }
}
