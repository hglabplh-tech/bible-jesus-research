package org.harry.jesus.mosedbmodel;

/**
 * The type Plans.
 */
public class Plans {

    private Integer planID;

    private String planÄTitle;

    private String planAuthor;

    private String planDescription;

    /**
     * Gets plan id.
     *
     * @return the plan id
     */
    public Integer getPlanID() {
        return planID;
    }

    /**
     * Sets plan id.
     *
     * @param planID the plan id
     * @return the plan id
     */
    public Plans setPlanID(Integer planID) {
        this.planID = planID;
        return this;
    }

    /**
     * Get plan ä title string.
     *
     * @return the string
     */
    public String getPlanÄTitle() {
        return planÄTitle;
    }

    /**
     * Set plan ä title plans.
     *
     * @param planÄTitle the plan ä title
     * @return the plans
     */
    public Plans setPlanÄTitle(String planÄTitle) {
        this.planÄTitle = planÄTitle;
        return this;
    }

    /**
     * Gets plan author.
     *
     * @return the plan author
     */
    public String getPlanAuthor() {
        return planAuthor;
    }

    /**
     * Sets plan author.
     *
     * @param planAuthor the plan author
     * @return the plan author
     */
    public Plans setPlanAuthor(String planAuthor) {
        this.planAuthor = planAuthor;
        return this;
    }

    /**
     * Gets plan description.
     *
     * @return the plan description
     */
    public String getPlanDescription() {
        return planDescription;
    }

    /**
     * Sets plan description.
     *
     * @param planDescription the plan description
     * @return the plan description
     */
    public Plans setPlanDescription(String planDescription) {
        this.planDescription = planDescription;
        return this;
    }
}
