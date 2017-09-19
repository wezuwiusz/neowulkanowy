package io.github.wulkanowy.api.attendance;

import java.util.ArrayList;
import java.util.List;

public class Type {

    private String name = "";

    private int total = 0;

    private List<Month> monthList = new ArrayList<>();

    public String getName() {
        return name;
    }

    public Type setName(String name) {
        this.name = name;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public Type setTotal(int total) {
        this.total = total;
        return this;
    }

    public List<Month> getMonthList() {
        return monthList;
    }

    public Type setMonthList(List<Month> monthList) {
        this.monthList = monthList;
        return this;
    }
}
