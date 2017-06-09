package org.todss;

import org.todss.model.*;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Alarm alarm = new Alarm(
                Frequency.DAY,
                ZonedDateTime.parse("2017-04-10T08:00+02:00")
                        .withZoneSameLocal(ZoneId.of("Europe/Amsterdam")),
                4 * 60
        );
        List<Travel> travels = new ArrayList<>();

        ZonedDateTime departure;
        ZonedDateTime arrival;

        // 0
        departure = ZonedDateTime.parse("2017-04-14T22:00+02:00")
                .withZoneSameLocal(ZoneId.of("Europe/Amsterdam"));
        arrival = ZonedDateTime.parse("2017-04-15T16:00+08:00")
                .withZoneSameLocal(ZoneId.of("Australia/West"));
        travels.add(
                new Travel(
                        departure,
                        arrival
                )
        );

        // 1
        departure = ZonedDateTime.parse("2017-04-20T22:00+08:00")
                .withZoneSameLocal(ZoneId.of("Australia/West"));
        arrival = ZonedDateTime.parse("2017-04-21T14:00+02:00")
                .withZoneSameLocal(ZoneId.of("Europe/Amsterdam"));
        travels.add(
                new Travel(
                        departure,
                        arrival
                )
        );

        // 2
        departure = ZonedDateTime.parse("2017-05-19T17:00+00:00")
                .withZoneSameLocal(ZoneId.of("Europe/Amsterdam"));
        arrival = ZonedDateTime.parse("2017-05-20T19:00+00:00")
                .withZoneSameLocal(ZoneId.of("America/Los_Angeles"));
        travels.add(
                new Travel(
                        departure,
                        arrival
                )
        );

        // 3
        // TODO extra inname?
        departure = ZonedDateTime.parse("2017-05-27T08:15+00:00")
                .withZoneSameLocal(ZoneId.of("America/Los_Angeles"));
        arrival = ZonedDateTime.parse("2017-05-28T09:15+00:00")
                .withZoneSameLocal(ZoneId.of("Europe/Amsterdam"));
        travels.add(
                new Travel(
                        departure,
                        arrival
                )
        );

        // 4
        departure = ZonedDateTime.parse("2017-05-27T08:15+00:00")
                .withZoneSameLocal(ZoneId.of("America/Los_Angeles"));
        arrival = ZonedDateTime.parse("2017-05-28T14:00+00:00")
                .withZoneSameLocal(ZoneId.of("Europe/Amsterdam"));
        travels.add(
                new Travel(
                        departure,
                        arrival
                )
        );

        // Set travel
        Travel travel = travels.get(0);

        // Start measure
        ZonedDateTime start = ZonedDateTime.now();

        // Execute algorithm
        Algorithm algorithm = new Algorithm(alarm, travel);
        Path result = algorithm.execute();

        //
        System.out.println("Departure: " + travel.getDeparture());
        System.out.println("Arrival: " + travel.getArrival());

        if (result == null) {
            System.out.println("\nNo solution found.");
        } else {
            System.out.println();
            System.out.println("Cost: " + result.getCost());
            System.out.println();
            writeIntakes(result.getIntakes());
        }

        // End measure
        ZonedDateTime end = ZonedDateTime.now();
        Duration duration = Duration.between(start, end);

        //
        System.out.println();
        System.out.println("Count: " + algorithm.getCount());
        System.out.println("Seconds: " + (double)duration.toMillis() / 1000);
    }

    private static void writeIntakes(List<Intake> intakes) {
        Intake prevIntake = null;
        for (int i = 0; i < intakes.size(); i++) {
            Intake intake = intakes.get(i);

            if (prevIntake != null) {
                Duration difference = Duration.between(prevIntake.getDate(), intake.getDate());

                System.out.println(
                        String.format("\t+%s", difference.toHours())
                );
            }

            System.out.println(String.format("[%d] %s", i, intake.getDate()));

            prevIntake = intake;
        }
    }
}
