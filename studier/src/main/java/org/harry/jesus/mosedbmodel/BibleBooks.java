package org.harry.jesus.mosedbmodel;

public class BibleBooks {

    private Integer id;

    private String name;

    private String info;

    public Integer getId() {
        return id;
    }

    public BibleBooks setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public BibleBooks setName(String name) {
        this.name = name;
        return this;
    }

    public String getInfo() {
        return info;
    }

    public BibleBooks setInfo(String info) {
        this.info = info;
        return this;
    }
}
