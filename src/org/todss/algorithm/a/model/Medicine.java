package org.todss.algorithm.a.model;

import java.util.List;

public class Medicine {
    private String name;
    private List<Alarm> alarms;
    private boolean active;

    public Medicine(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Alarm> getAlarms() {
        return alarms;
    }

    public void addAlarm(Alarm alarm) {
        alarms.add(alarm);
    }

    public void removeAlarm(Alarm alarm) {
        alarms.remove(alarm);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
