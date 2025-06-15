package com.example.uasvolunteerhub;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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

    // PERBAIKAN: Return StringProperty langsung, bukan create new instance
    public StringProperty snProperty() {
        return new SimpleStringProperty(String.valueOf(sn.get()));
    }

    public StringProperty titleProperty() {
        return title;
    }

    // Tambahan getter methods untuk kemudahan akses
    public int getSn() {
        return sn.get();
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    @Override
    public String toString() {
        return "ActivityRow{" +
                "sn=" + sn.get() +
                ", id=" + id +
                ", title='" + title.get() + '\'' +
                '}';
    }
}