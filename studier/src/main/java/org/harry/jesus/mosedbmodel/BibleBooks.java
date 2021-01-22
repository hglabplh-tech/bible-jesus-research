package org.harry.jesus.mosedbmodel;

/**
 * The type Bible books.
 */
public class BibleBooks {

    private Integer id;

    private String name;

    private String info;

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
    public BibleBooks setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @return the name
     */
    public BibleBooks setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets info.
     *
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets info.
     *
     * @param info the info
     * @return the info
     */
    public BibleBooks setInfo(String info) {
        this.info = info;
        return this;
    }
}
