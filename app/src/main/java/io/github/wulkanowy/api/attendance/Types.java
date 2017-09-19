package io.github.wulkanowy.api.attendance;

import java.util.ArrayList;
import java.util.List;

public class Types {

    private double total = 0;

    private List<Type> typeList = new ArrayList<>();

    public double getTotal() {
        return total;
    }

    public Types setTotal(double total) {
        this.total = total;
        return this;
    }

    public List<Type> getTypeList() {
        return typeList;
    }

    public Types setTypeList(List<Type> typeList) {
        this.typeList = typeList;
        return this;
    }
}
