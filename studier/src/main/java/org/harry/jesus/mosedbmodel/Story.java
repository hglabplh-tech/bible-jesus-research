package org.harry.jesus.mosedbmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Story.
 */
public class Story {

    private Integer id;

    private String text;

    private List<String> links = new ArrayList<>();

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
    public Story setId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Gets text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets text.
     *
     * @param text the text
     * @return the text
     */
    public Story setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * Gets links.
     *
     * @return the links
     */
    public List<String> getLinks() {
        return links;
    }

    /**
     * Sets links.
     *
     * @param links the links
     * @return the links
     */
    public Story setLinks(List<String> links) {
        this.links = links;
        return this;
    }
}
