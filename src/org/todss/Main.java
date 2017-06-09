package org.todss;

import org.todss.model.Alarm;
import org.todss.model.Frequency;
import org.todss.model.Path;
import org.todss.model.Travel;

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

        Algorithm algorithm = new Algorithm(alarm, travels.get(0));
        Path result = algorithm.execute();

        System.out.println(result);
    }
}
