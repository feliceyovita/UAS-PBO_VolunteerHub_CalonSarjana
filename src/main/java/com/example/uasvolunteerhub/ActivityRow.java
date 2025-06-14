package com.example.uasvolunteerhub;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ActivityRow {
    private final SimpleIntegerProperty sn;
    private final int id;
    private final SimpleStringProperty title;

    public ActivityRow(int sn, int id, String title) {
        this.sn = new SimpleIntegerProperty(sn);
        this.id = id;
        this.title = new SimpleStringProperty(title);
    }

    public int getId() {
        return id;
    }

    public SimpleStringProperty snProperty() {
        return new SimpleStringProperty(String.valueOf(sn.get()));
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }
}
