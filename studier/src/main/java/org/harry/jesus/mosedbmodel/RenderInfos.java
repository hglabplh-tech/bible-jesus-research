package org.harry.jesus.mosedbmodel;

public class RenderInfos {

    private String bible;

    private Integer bibleBook;

    private Integer chapter;

    private  Integer vers;

    private Integer vstart;

    private Integer vend;

    private String attributes;

    public String getBible() {
        return bible;
    }

    public RenderInfos setBible(String bible) {
        this.bible = bible;
        return this;
    }

    public Integer getBibleBook() {
        return bibleBook;
    }

    public RenderInfos setBibleBook(Integer bibleBook) {
        this.bibleBook = bibleBook;
        return this;
    }

    public Integer getChapter() {
        return chapter;
    }

    public RenderInfos setChapter(Integer chapter) {
        this.chapter = chapter;
        return this;
    }

    public Integer getVers() {
        return vers;
    }

    public RenderInfos setVers(Integer vers) {
        this.vers = vers;
        return this;
    }

    public Integer getVstart() {
        return vstart;
    }

    public RenderInfos setVstart(Integer vstart) {
        this.vstart = vstart;
        return this;
    }

    public Integer getVend() {
        return vend;
    }

    public RenderInfos setVend(Integer vend) {
        this.vend = vend;
        return this;
    }

    public String getAttributes() {
        return attributes;
    }

    public RenderInfos setAttributes(String attributes) {
        this.attributes = attributes;
        return this;
    }
}
