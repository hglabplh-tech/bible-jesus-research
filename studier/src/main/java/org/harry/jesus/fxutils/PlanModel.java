package org.harry.jesus.fxutils;

public class PlanModel {

    private String day;

    private String devotional;

    private String vers;

    public PlanModel(String day, String devotional, String vers) {
        this.day = day;
        this.devotional = devotional;
        this.vers = vers;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDevotional() {
        return devotional;
    }

    public void setDevotional(String devotional) {
        this.devotional = devotional;
    }

    public String getVers() {
        return vers;
    }

    public void setVers(String vers) {
        this.vers = vers;
    }
}
