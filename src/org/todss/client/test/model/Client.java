package org.todss.client.test.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client {
    private static Client instance = new Client();

    private Preset selectedPreset;
    private ObservableList<Preset> presets = FXCollections.observableArrayList(Preset.extractor());

    private Client() { }

    public static Client getInstance() {
        return instance;
    }

    public Preset getSelectedPreset() {
        return selectedPreset;
    }

    public void setSelectedPreset(Preset selectedPreset) {
        this.selectedPreset = selectedPreset;
    }

    public ObservableList<Preset> getPresets() {
        return presets;
    }

    public void addPreset(Preset preset) {
        presets.add(preset);
    }

    public void removePreset(Preset preset) {
        presets.remove(preset);
    }
}
