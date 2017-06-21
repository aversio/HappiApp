package org.todss.algorithm.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A path with intakes that can be taken.
 *
 * Only used in the brute-algorithm.
 */
public class Path {
    /**
     * List of intakes that can be taken.
     */
    private List<Intake> intakes = new ArrayList<>();

    /**
     * Cost of the path.
     */
    private long cost = 0;

    /**
     * List of taken margins.
     */
    private List<Long> margins = new ArrayList<>();

    /**
     * Create a empty path.
     */
    public Path() {
    }

    /**
     * Add an intake to the path.
     * @param intake intake to be added
     * @param cost cost for the intake
     */
    public void addIntake(Intake intake, long cost) {
        intakes.add(intake);
        this.cost += cost;
    }

    /**
     * Get the intakes.
     * @return intakes of this path
     */
    public List<Intake> getIntakes() {
        return intakes;
    }

    /**
     * Get the last intake added
     * @return last intake added
     */
    public Intake getLastIntake() {
        return intakes.get(
                intakes.size() - 1
        );
    }

    /**
     * Get the cost for all the intakes
     * @return cost for all the intakes
     */
    public long getCost() {
        return cost;
    }

    /**
     * Set the cost for the path
     * @param cost
     */
    public void setCost(long cost) {
        this.cost = cost;
    }

    /**
     * Add margin to path
     * @param margin margin to add
     */
    public void addMargin(long margin) {
        margins.add(margin);
    }

    @Override
    public String toString() {
        return "Path{" +
                "intakes=" + intakes +
                ", cost=" + cost +
                '}';
    }
}
