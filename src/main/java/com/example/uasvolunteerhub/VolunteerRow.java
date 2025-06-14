package com.example.uasvolunteerhub;

import javafx.beans.property.SimpleStringProperty;

public class VolunteerRow {
    private final SimpleStringProperty sn;
    private final SimpleStringProperty name;
    private final SimpleStringProperty activity;

    public VolunteerRow(int sn, String name, String activity) {
        this.sn = new SimpleStringProperty(String.valueOf(sn));
        this.name = new SimpleStringProperty(name);
        this.activity = new SimpleStringProperty(activity);
    }

    public SimpleStringProperty snProperty() {
        return sn;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty activityProperty() {
        return activity;
    }
}