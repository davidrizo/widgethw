package es.ua.dlsi.master.widgethw;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @autor drizo
 */
public class Model {
    ObjectProperty<ObservableList<Measurement>> measurements;

    public Model() {
        ObservableList<Measurement> array = FXCollections.observableArrayList();
        measurements = new SimpleObjectProperty<>(array);
    }

    public Property<ObservableList<Measurement>> getMeasurements() {
        return measurements;
    }

    public void addMeasurement(Measurement measurement) {
        measurements.get().add(measurement);
    }
}
