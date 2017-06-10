package org.todss.model;

import java.util.ArrayList;
import java.util.List;

public class Path {
    private List<Intake> intakes = new ArrayList<>();
    private long cost = 0;
    private List<Long> margins = new ArrayList<>();

    public Path() {
    }

    public void addIntake(Intake intake, long cost) {
        intakes.add(intake);
        this.cost += cost;
    }

    public List<Intake> getIntakes() {
        return intakes;
    }

    public Intake getLastIntake() {
        return intakes.get(
                intakes.size() - 1
        );
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public void addMargin(long margin) {
        margins.add(margin);
    }

    public long getTotalMargin() {
        int totalMargin = 0;

        for (long margin : margins) {
            totalMargin += margin;
        }

        return totalMargin;
    }

    @Override
    public String toString() {
        return "Path{" +
                "intakes=" + intakes +
                ", cost=" + cost +
                '}';
    }
}
