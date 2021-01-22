package org.harry.jesus.mosedbmodel;

/**
 * The type Stories.
 */
public class Stories {

    private Integer id;

    private String title;

    private String author;

    /**
     * Gets id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public Stories setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     * @return the title
     */
    public Stories setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Gets author.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets author.
     *
     * @param author the author
     * @return the author
     */
    public Stories setAuthor(String author) {
        this.author = author;
        return this;
    }
}
