package es.ua.dlsi.master.widgethw;

import com.thoughtworks.xstream.XStream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.ArrayList;

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

    public void saveAs(File file) throws IOException {
        // avoid serializing the observable list - it is easier to do it using standard classes
        ArrayList<Measurement> measurementArrayList = new ArrayList<>();
        measurementArrayList.addAll(measurements.get());
        XStream xstream = new XStream();
        String xml = xstream.toXML(measurementArrayList);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        writer.write(xml);
        writer.close();
    }

    public void open(File file) throws IOException {
        XStream xStream = new XStream();
        ArrayList<Measurement> measurementsArrayList = (ArrayList<Measurement>) xStream.fromXML(file);
        measurements.get().clear();
        measurements.get().addAll(measurementsArrayList);
    }
}
