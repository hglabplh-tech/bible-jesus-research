package org.harry.jesus.mosedbmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Plan days.
 */
public class PlanDays {

    private Integer planId;

    private Integer dayNo;

    private List<String> linksInOrder = new ArrayList<>();

    private String devotional;

    /**
     * Gets plan id.
     *
     * @return the plan id
     */
    public Integer getPlanId() {
        return planId;
    }

    /**
     * Sets plan id.
     *
     * @param planId the plan id
     * @return the plan id
     */
    public PlanDays setPlanId(Integer planId) {
        this.planId = planId;
        return this;
    }

    /**
     * Gets day no.
     *
     * @return the day no
     */
    public Integer getDayNo() {
        return dayNo;
    }

    /**
     * Sets day no.
     *
     * @param dayNo the day no
     * @return the day no
     */
    public PlanDays setDayNo(Integer dayNo) {
        this.dayNo = dayNo;
        return this;
    }

    /**
     * Gets links in order.
     *
     * @return the links in order
     */
    public List<String> getLinksInOrder() {
        return linksInOrder;
    }

    /**
     * Sets links in order.
     *
     * @param linksInOrder the links in order
     * @return the links in order
     */
    public PlanDays setLinksInOrder(List<String> linksInOrder) {
        this.linksInOrder = linksInOrder;
        return this;
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
     * @return the devotional
     */
    public PlanDays setDevotional(String devotional) {
        this.devotional = devotional;
        return this;
    }
}
