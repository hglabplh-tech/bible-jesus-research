package org.harry.jesus.fxutils;

/**
 * The type Plan model.
 */
public class PlanModel {

    private String day;

    private String devotional;

    private String vers;

    /**
     * Instantiates a new Plan model.
     *
     * @param day        the day
     * @param devotional the devotional
     * @param vers       the vers
     */
    public PlanModel(String day, String devotional, String vers) {
        this.day = day;
        this.devotional = devotional;
        this.vers = vers;
    }

    /**
     * Gets day.
     *
     * @return the day
     */
    public String getDay() {
        return day;
    }

    /**
     * Sets day.
     *
     * @param day the day
     */
    public void setDay(String day) {
        this.day = day;
    }

    /**
     * Gets devotional.
     *
     * @return the devotional
     */
    public String getDevotional() {
        return devotional;
    }

    /**
     * Sets devotional.
     *
     * @param devotional the devotional
     */
    public void setDevotional(String devotional) {
        this.devotional = devotional;
    }

    /**
     * Gets vers.
     *
     * @return the vers
     */
    public String getVers() {
        return vers;
    }

    /**
     * Sets vers.
     *
     * @param vers the vers
     */
    public void setVers(String vers) {
        this.vers = vers;
    }
}
