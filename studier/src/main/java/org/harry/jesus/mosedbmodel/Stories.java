package org.harry.jesus.mosedbmodel;

public class Stories {

    private Integer id;

    private String title;

    private String author;

    public Integer getId() {
        return id;
    }

    public Stories setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Stories setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Stories setAuthor(String author) {
        this.author = author;
        return this;
    }
}
