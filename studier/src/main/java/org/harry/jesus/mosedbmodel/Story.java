package org.harry.jesus.mosedbmodel;

import java.util.ArrayList;
import java.util.List;

public class Story {

    private Integer id;

    private String text;

    private List<String> links = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public Story setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getText() {
        return text;
    }

    public Story setText(String text) {
        this.text = text;
        return this;
    }

    public List<String> getLinks() {
        return links;
    }

    public Story setLinks(List<String> links) {
        this.links = links;
        return this;
    }
}
