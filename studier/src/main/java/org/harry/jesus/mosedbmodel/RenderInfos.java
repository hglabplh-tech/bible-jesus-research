package org.harry.jesus.mosedbmodel;

/**
 * The type Render infos.
 */
public class RenderInfos {

    private Integer bible;

    private Integer bibleBook;

    private Integer chapter;

    private  Integer vers;

    private Integer vstart;

    private Integer vend;

    private String attributes;

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
    public RenderInfos setBible(Integer bible) {
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
    public RenderInfos setBibleBook(Integer bibleBook) {
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
    public RenderInfos setChapter(Integer chapter) {
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
    public RenderInfos setVers(Integer vers) {
        this.vers = vers;
        return this;
    }

    /**
     * Gets vstart.
     *
     * @return the vstart
     */
    public Integer getVstart() {
        return vstart;
    }

    /**
     * Sets vstart.
     *
     * @param vstart the vstart
     * @return the vstart
     */
    public RenderInfos setVstart(Integer vstart) {
        this.vstart = vstart;
        return this;
    }

    /**
     * Gets vend.
     *
     * @return the vend
     */
    public Integer getVend() {
        return vend;
    }

    /**
     * Sets vend.
     *
     * @param vend the vend
     * @return the vend
     */
    public RenderInfos setVend(Integer vend) {
        this.vend = vend;
        return this;
    }

    /**
     * Gets attributes.
     *
     * @return the attributes
     */
    public String getAttributes() {
        return attributes;
    }

    /**
     * Sets attributes.
     *
     * @param attributes the attributes
     * @return the attributes
     */
    public RenderInfos setAttributes(String attributes) {
        this.attributes = attributes;
        return this;
    }
}
