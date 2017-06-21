package org.todss.client.test.model;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.util.Callback;
import org.todss.algorithm.a.model.Alarm;
import org.todss.algorithm.a.model.Frequency;
import org.todss.algorithm.a.model.Travel;

public class Preset {
    private StringProperty name = new SimpleStringProperty();
    private Alarm[] alarms = new Alarm[2];
    private boolean[] alarmStatuses = new boolean[2];
    private Frequency frequency;
    private int margin = 0;
    private ObservableMap<Travel, Boolean> travels = FXCollections.observableHashMap();

    public Preset(String name) {
        setName(name);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setAlarm(int index, Alarm alarm, boolean status) {
        alarms[index] = alarm;
        alarmStatuses[index] = status;
    }

    public Alarm[] getAlarms() {
        return alarms;
    }

    public ObservableMap<Travel, Boolean> getTravels() {
        return travels;
    }

    public ObservableList<Travel> getTravelList() {
        ObservableList<Travel> list = FXCollections.observableArrayList(Travel.extractor());
        list.addAll(travels.keySet());

        return list;
    }

    public boolean isTravelActive(Travel travel) {
        try {
            return travels.get(travel);
        } catch (Exception e) {
            return false;
        }
    }

    public void setTravelActive(Travel travel, boolean status) {
        travels.put(travel, status);
    }

    public void addTravel(Travel travel, boolean status) {
        travels.put(travel, status);
    }

    public void removeTravel(Travel travel) {
        travels.remove(travel);
    }

    static Callback<Preset, Observable[]> extractor() {
        return (Preset p) -> new Observable[]{ p.name };
    }

    @Override
    public String toString() {
        return getName();
    }
}
