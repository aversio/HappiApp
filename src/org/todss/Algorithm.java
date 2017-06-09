package org.todss;

import org.todss.model.*;

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

class Algorithm {
    private Alarm alarm;
    private Travel travel;

    private PriorityQueue<Path> completedPaths = new PriorityQueue<>(Comparator.comparingLong(Path::getCost));

    private int count;

    private int maxCost = 700;
    private boolean checkIsPossible = true;

    Algorithm(Alarm alarm, Travel travel) {
        this.alarm = alarm;
        this.travel = travel;

        execute();
    }

    private ZonedDateTime getStart() {
        int test = 3;
        if (alarm.getFrequency() == Frequency.HALF_DAY) {
            test = 6;
        }

        return travel.getDeparture()
                .withHour(alarm.getStart().getHour())
                .withMinute(alarm.getStart().getMinute())
                .minusMinutes(test * alarm.getFrequency().getMinutes());
    }

    private long difference(ZonedDateTime one, ZonedDateTime two) {
        return Duration.between(one, two).toMinutes();
    }

    private ZonedDateTime getTargetDateTime(int counter) {
        ZonedDateTime targetDateTime = getStart().plusMinutes(
                counter * alarm.getFrequency().getMinutes()
        );

        if (targetDateTime.isEqual(travel.getArrival()) || targetDateTime.isAfter(travel.getArrival())) {
            ZoneOffset currentOffset = targetDateTime.withZoneSameLocal(travel.getDeparture().getZone()).getOffset();
            ZoneOffset newOffset = targetDateTime.withZoneSameLocal(travel.getArrival().getZone()).getOffset();
            int difference = currentOffset.compareTo(newOffset) / 60;
            boolean change = (abs(difference) > alarm.getFrequency().getMinutes() / 2);

            if (change) {
                int changeValue = alarm.getFrequency().getMinutes() - abs(difference);
                changeValue = (difference < 0 ? -changeValue : changeValue);

                targetDateTime = targetDateTime
                        .withZoneSameInstant(travel.getArrival().getZone())
                        .plusMinutes(changeValue);
            } else {
                targetDateTime = targetDateTime
                        .withZoneSameLocal(travel.getArrival().getZone());
            }

        }

        return targetDateTime;
    }

    private ZoneId getZoneId(ZonedDateTime dateTime) {
        // ZoneId prevZoneId = alarm.getStart().getZone();
        ZoneId prevZoneId = travel.getDeparture().getZone();

        //for (Travel travel : travels) {
            if (dateTime.isEqual(travel.getArrival())
                    || dateTime.isAfter(travel.getArrival())) {
                prevZoneId = travel.getArrival().getZone();
            }
        //}

        return prevZoneId;
    }

    public Path execute() {
        // Start algorithm
        addPaths(0, new Path());

        // Return first result
        if (!completedPaths.isEmpty())
            return completedPaths.poll();

        return null;
    }

    private boolean isPossible(ZonedDateTime dateTime) {
        return (dateTime.getHour() >= 8 && dateTime.getHour() <= 22);
    }

    private void addPaths(int counter, Path path) {
        ZonedDateTime prevTargetDateTime = getTargetDateTime(counter - 1);
        ZonedDateTime targetDateTime = getTargetDateTime(counter);

        // Default margins
        boolean extra = false;
        long minMargin = -alarm.getMargin();
        long maxMargin = alarm.getMargin();

        boolean extra2 = false;

        // After arrival, go to target date-time
        if (prevTargetDateTime.isAfter(travel.getArrival())) {
            // Difference between ... and ...
            long difference = difference(prevTargetDateTime, path.getLastIntake().getDate());

            extra = true;
            extra2 = true;

            if (difference > 0) {
                maxMargin = -60;

                // abs(-240) > 120 = 240 > 120
                // -120
                if (abs(minMargin) > difference) {
                    minMargin = -difference;
                }
            } else {
                minMargin = 60;

                // 240 > abs(-120) = 240 > 120
                if (maxMargin > abs(difference)) {
                    maxMargin = -difference;
                }
            }
        }

        List<Long> margins = new ArrayList<>();

        for (long i = minMargin; i <= maxMargin; i += 60) {
            margins.add(i);
        }

        if (!prevTargetDateTime.getZone().equals(targetDateTime.getZone())) {
            //extra = true;
        }

        if (extra) {
            if (extra2) {
                if (!margins.contains(0)) {
                    margins.add(0L);
                }
            }

            long difference = difference(prevTargetDateTime, path.getLastIntake().getDate());

//            if (difference == 0) {
//                difference = difference(path.getLastIntake().getDate().plusMinutes(alarm.getFrequency().getMinutes()), targetDateTime);
//            }

            // Controleer overdosis tijd
            if (abs(difference) <= alarm.getFrequency().getMinutes() / 2) {
                long insertValue = -difference;

                if (insertValue < alarm.getMargin()) {
                    if (!margins.contains(insertValue)) {
                        margins.add(insertValue);
                    }
                }
            }
        }

//        boolean stop = false;
//        if (!path.getIntakes().isEmpty()) {
//            if (path.getLastIntake().getDate().equals(
//                    ZonedDateTime.parse("2017-05-20T08:00+02:00[Europe/Amsterdam]")))
//            {
//                System.out.println(path);
//                System.out.println(margins);
//                System.out.println(extra);
//
//                ZonedDateTime test = path.getLastIntake().getDate()
//                        .plusMinutes(alarm.getFrequency().getMinutes() - 540);
//
//                System.out.println(test);
//
//                stop = true;
//                exit(1);
//            }
//        }

//        if (extra2) {
//            System.out.println(margins);
//
//            exit(1);
//        }

        for (long i : margins) {
            count++;

            // Previous intake
            ZonedDateTime start = (
                    path.getIntakes().isEmpty() ?
                            getStart()
                            :
                            path.getLastIntake().getDate().plusMinutes(alarm.getFrequency().getMinutes())
            );

            // Create new intake
            Intake newIntake = new Intake(
                    start.plusMinutes(i)
            );
            newIntake.setDate(
                    newIntake.getDate().withZoneSameInstant(getZoneId(newIntake.getDate()))
            );

//            if (stop && i == -540) {
//                System.out.println(path.getLastIntake());
//                System.out.println(newIntake);
//                System.out.println(getZoneId(newIntake.getDate()));
//                System.out.println(newIntake.getDate().withZoneSameInstant(
//                        ZoneId.of("America/Los_Angeles")
//                ));
//
//                exit(1);
//            }

            // Check intake
            if (checkIsPossible && !isPossible(newIntake.getDate())) {
                continue;
            }

            // Calculate cost for new intake
            double daysDeparture = Math.abs((double)Duration.between(newIntake.getDate(), travel.getDeparture()).toHours() / 24);
            double daysArrival = Math.abs((double)Duration.between(newIntake.getDate(), travel.getArrival()).toHours() / 24);

            double days = Math.min(daysDeparture, daysArrival);
            long minutes = abs(Duration.between(targetDateTime, newIntake.getDate()).toMinutes());
            long cost = round(minutes + (minutes * days));

            // Create/copy path
            Path newPath = new Path();
            newPath.getIntakes().addAll(path.getIntakes());
            newPath.setCost(path.getCost());

            // Add new path
            newPath.addIntake(newIntake, cost);

            // Done?
            if (difference(targetDateTime, newIntake.getDate()) == 0
                    &&
                    (newIntake.getDate().isEqual(travel.getArrival())
                            || newIntake.getDate().isAfter(travel.getArrival()))
                    ) {
                completedPaths.add(newPath);

                continue;
            }

            if (newPath.getCost() >= maxCost) {
                continue;
            }

            // Loop
            addPaths(counter + 1, newPath);
        }
    }

    public int getCount() {
        return count;
    }
}
