package org.harry.jesus.mosedbmodel;

import java.util.ArrayList;
import java.util.List;

public class PlanDays {

    private Integer planId;

    private Integer dayNo;

    private List<String> linksInOrder = new ArrayList<>();

    private String devotional;

    public Integer getPlanId() {
        return planId;
    }

    public PlanDays setPlanId(Integer planId) {
        this.planId = planId;
        return this;
    }

    public Integer getDayNo() {
        return dayNo;
    }

    public PlanDays setDayNo(Integer dayNo) {
        this.dayNo = dayNo;
        return this;
    }

    public List<String> getLinksInOrder() {
        return linksInOrder;
    }

    public PlanDays setLinksInOrder(List<String> linksInOrder) {
        this.linksInOrder = linksInOrder;
        return this;
    }

    public String getDevotional() {
        return devotional;
    }

    public PlanDays setDevotional(String devotional) {
        this.devotional = devotional;
        return this;
    }
}
