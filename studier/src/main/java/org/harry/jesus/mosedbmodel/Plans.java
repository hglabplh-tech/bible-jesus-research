package org.harry.jesus.mosedbmodel;

public class Plans {

    private Integer planID;

    private String planÄTitle;

    private String planAuthor;

    private String planDescription;

    public Integer getPlanID() {
        return planID;
    }

    public Plans setPlanID(Integer planID) {
        this.planID = planID;
        return this;
    }

    public String getPlanÄTitle() {
        return planÄTitle;
    }

    public Plans setPlanÄTitle(String planÄTitle) {
        this.planÄTitle = planÄTitle;
        return this;
    }

    public String getPlanAuthor() {
        return planAuthor;
    }

    public Plans setPlanAuthor(String planAuthor) {
        this.planAuthor = planAuthor;
        return this;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public Plans setPlanDescription(String planDescription) {
        this.planDescription = planDescription;
        return this;
    }
}
