package org.todss.model;

import java.util.ArrayList;
import java.util.List;

public class Node {
    List<Intake> intakes = new ArrayList<>();
    long cost = 0;

    public Node(List<Intake> intakes, long cost) {
        this.intakes = intakes;
        this.cost = cost;
    }

    public List<Intake> getIntakes() {
        return intakes;
    }

    public long getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Node{" +
                "intakes=" + intakes +
                ", cost=" + cost +
                '}';
    }
}
