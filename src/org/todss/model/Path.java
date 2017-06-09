package org.todss.model;

import java.util.ArrayList;
import java.util.List;

public class Path {
    private List<Intake> intakes = new ArrayList<>();
    private long cost = 0;

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

    @Override
    public String toString() {
        return "Path{" +
                "intakes=\n" + intakes +
                "\n, cost=" + cost +
                '}';
    }
}
