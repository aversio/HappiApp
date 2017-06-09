package org.todss;

import org.todss.model.Alarm;
import org.todss.model.Intake;
import org.todss.model.Path;
import org.todss.model.Travel;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static java.lang.Math.round;
import static java.lang.StrictMath.abs;

/**
 * Algorithm for an alarm with travels in mind.
 *
 * All times are in minutes.
 */
class Algorithm {
    /**
     * Alarm with desired time.
     */
    private Alarm alarm;

    /**
     * Travel for converting the alarm time.
     */
    private Travel travel;

    /**
     * Queue for all the possible paths, order by cost.
     */
    private PriorityQueue<Path> completedPaths = new PriorityQueue<>(Comparator.comparingLong(Path::getCost));

    /**
     * Setting for max cost of a path.
     */
    private int maxCost;

    /**
     * Setting for checking if a calculated intake is possible.
     */
    private boolean checkIsPossible;

    {
        checkIsPossible = true;
        maxCost = 700;
    }

    /**
     * New algorithm for calculating paths.
     *
     * @param alarm Desired time for alarm
     * @param travel Travel for new time zone
     */
    Algorithm(Alarm alarm, Travel travel) {
        this.alarm = alarm;
        this.travel = travel;
    }

    /**
     * Date-time for starting calculating moments.
     *
     * Is always enough time for using the margin to change the alarm for half a day.
     *
     * @return The date-time for starting the calculations
     */
    private ZonedDateTime getStart() {
        int minusFrequency = 0;

        if (alarm.getMargin() > 0)
            minusFrequency = 12 * 60 / alarm.getMargin();

        return travel.getDeparture()
                .withHour(alarm.getStart().getHour())
                .withMinute(alarm.getStart().getMinute())
                .minusMinutes(minusFrequency * alarm.getFrequency().getMinutes());
    }

    /**
     * Calculates the desired date-time for alarm.
     *
     * Uses the `getStart()` method.
     *
     * @param counter moments after start
     * @return the desired date-time for alarm
     */
    private ZonedDateTime getTargetDateTime(int counter) {
        // Default target
        ZonedDateTime targetDateTime = getStart().plusMinutes(
                counter * alarm.getFrequency().getMinutes()
        );

        // Target after arrival
        if (targetDateTime.isEqual(travel.getArrival()) || targetDateTime.isAfter(travel.getArrival())) {
            // Offset difference
            ZoneOffset currentOffset = targetDateTime.withZoneSameLocal(travel.getDeparture().getZone()).getOffset();
            ZoneOffset newOffset = targetDateTime.withZoneSameLocal(travel.getArrival().getZone()).getOffset();

            int difference = currentOffset.compareTo(newOffset) / 60;

            // True if the difference is greater than half of the alarm frequency.
            boolean change = abs(difference) > alarm.getFrequency().getMinutes() / 2;

            if (change) {
                // Correct the alarm to the closest previous alarm
                int changeValue = alarm.getFrequency().getMinutes() - abs(difference);
                changeValue = (difference < 0 ? -changeValue : changeValue);

                targetDateTime = targetDateTime
                        .withZoneSameInstant(travel.getArrival().getZone())
                        .plusMinutes(changeValue);
            } else {
                // Change only the time
                targetDateTime = targetDateTime.withZoneSameLocal(travel.getArrival().getZone());
            }
        }

        return targetDateTime;
    }

    /**
     * Get the zone-id by a date-time, calculated with the travel.
     *
     * @param dateTime date-time for calculating zone-id
     * @return zone-id belonging to date-time
     */
    private ZoneId getZoneId(ZonedDateTime dateTime) {
        ZoneId prevZoneId = travel.getDeparture().getZone();

        if (dateTime.isEqual(travel.getArrival())
                || dateTime.isAfter(travel.getArrival())) {
            prevZoneId = travel.getArrival().getZone();
        }

        return prevZoneId;
    }

    /**
     * Start algorithm.
     *
     * @return path with lowest cost.
     */
    Path execute() {
        // Start algorithm
        addPaths(0, new Path());

        // Return first result
        if (!completedPaths.isEmpty())
            return completedPaths.poll();

        return null;
    }

    /**
     * Check if the date-time is possible.
     *
     * @param dateTime date-time to be checked
     * @return true if possible, false otherwise
     */
    private boolean isPossible(ZonedDateTime dateTime) {
        return (dateTime.getHour() >= 8 && dateTime.getHour() <= 22);
    }

    /**
     * Calculate the margins from minimal and maximal margin.
     *
     * @param minMargin minimal margin
     * @param maxMargin maximal margin
     * @return all margins
     */
    private List<Long> getMargins(long minMargin, long maxMargin) {
        List<Long> margins = new ArrayList<>();

        for (long i = minMargin; i <= maxMargin; i += 60) {
            margins.add(i);
        }

        return margins;
    }

    /**
     * Calculates cost for intake.
     *
     * @param intake intake
     * @param target target date-time
     * @return cost for intake
     */
    private long getCost(Intake intake, ZonedDateTime target) {
        double daysDeparture = Math.abs((double)Duration.between(intake.getDate(), travel.getDeparture()).toHours() / 24);
        double daysArrival = Math.abs((double)Duration.between(intake.getDate(), travel.getArrival()).toHours() / 24);

        double days = Math.min(daysDeparture, daysArrival);
        long minutes = abs(Duration.between(target, intake.getDate()).toMinutes());

        return round(minutes + (minutes * days));
    }

    /**
     * Generate new paths of a previous path.
     *
     * @param counter moments after start
     * @param path previous path
     */
    private void addPaths(int counter, Path path) {
        // Targets of new and previous moments
        ZonedDateTime prevTargetDateTime = getTargetDateTime(counter - 1);
        ZonedDateTime targetDateTime = getTargetDateTime(counter);

        // Default margins
        long minMargin = -alarm.getMargin();
        long maxMargin = alarm.getMargin();

        // After arrival, go to target date-time (only use margins that get us closer to the target date-time)
        if (prevTargetDateTime.isAfter(travel.getArrival())) {
            // Difference between previous target and last target
            long difference = Duration.between(prevTargetDateTime, path.getLastIntake().getDate()).toMinutes();

            // Set new margins
            if (difference > 0) {
                maxMargin = -60;

                if (abs(minMargin) > difference) {
                    minMargin = -difference;
                }
            } else {
                minMargin = 60;

                if (maxMargin > abs(difference)) {
                    maxMargin = -difference;
                }
            }
        }

        for (long i : getMargins(minMargin, maxMargin)) {
            // Previous intake
            ZonedDateTime start = (
                    path.getIntakes().isEmpty() ?
                            getStart()
                            :
                            path.getLastIntake().getDate().plusMinutes(alarm.getFrequency().getMinutes())
            );

            // Create new intake
            Intake newIntake = new Intake(start.plusMinutes(i));
            newIntake.setDate(newIntake.getDate().withZoneSameInstant(getZoneId(newIntake.getDate())));

            // Check intake
            if (checkIsPossible && !isPossible(newIntake.getDate())) {
                continue;
            }

            // Calculate cost for new intake
            long cost = getCost(newIntake, targetDateTime);

            // Create/copy path
            Path newPath = new Path();
            newPath.getIntakes().addAll(path.getIntakes());
            newPath.setCost(path.getCost());

            // Add new path
            newPath.addIntake(newIntake, cost);

            // If path is done
            if (Duration.between(targetDateTime, newIntake.getDate()).toMinutes() == 0
                    &&
                    (newIntake.getDate().isEqual(travel.getArrival())
                            || newIntake.getDate().isAfter(travel.getArrival()))
                    ) {
                completedPaths.add(newPath);

                continue;
            }

            // Skip path if cost is to high
            if (newPath.getCost() >= maxCost) {
                continue;
            }

            // Loop
            addPaths(counter + 1, newPath);
        }
    }
}
