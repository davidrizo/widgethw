package es.ua.dlsi.master.widgethw;

import es.ua.dlsi.master.widgethw.oshi.HWReader;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class Controller implements Initializable {
    @FXML
    ToggleButton btnRun;
    @FXML
    LineChart chart;
    @FXML
    ProgressBar rmsProgressBar;
    @FXML
    TableView<Measurement> tableView;
    @FXML
    TableColumn<Measurement, Float> colCPU;
    @FXML
    TableColumn<Measurement, Float> colRAM;
    @FXML
    TableColumn<Measurement, Float> colRMS;
    @FXML
    MenuItem menuItemRunStop;

    BooleanProperty running;

    HWObserver hwObserver;
    ISoundReader soundReader;
    /**
     * Used to avoid blocking the interface with sound reading
     */
    Service<Void> soundService;

    ObservableList<XYChart.Series<String, Double>> lineChartData;
    XYChart.Series<String, Double> usedRAMPercentages;
    XYChart.Series<String, Double> cpusSUMPercentages;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");


    Model model;

    public Controller() throws LineUnavailableException {
        model = new Model();
        hwObserver = new HWObserver(new HWReader()); // TODO Cambiar por inyección de código
        lineChartData = FXCollections.observableArrayList();

        running = new SimpleBooleanProperty(false);
        running.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue.booleanValue()) {
                    hwObserver.run();
                    menuItemRunStop.setText("Stop");
                } else {
                    hwObserver.stop();
                    menuItemRunStop.setText("Run");
                }
            }
        });

        // add a lineChart for each measure
        usedRAMPercentages = new LineChart.Series<>("Used memory", FXCollections.<XYChart.Data<String, Double>>observableArrayList());
        lineChartData.add(usedRAMPercentages);

        cpusSUMPercentages = new LineChart.Series<>("CPU sum", FXCollections.<XYChart.Data<String, Double>>observableArrayList());
        lineChartData.add(cpusSUMPercentages);

        soundReader = new SoundReader(); //TODO Cambiar por inyección de código
        //TODO Capturar excepción constructor, dar mensaje y salir

        //soundReader.start(); // esto deja colgado el interfaz porque está en el mismo thread y nunca acaba de leer- como es el constructor ni siquiera inicia
        soundService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        soundReader.start();
                        return null;
                    }
                };
                return task;
            }
        };
        soundService.start();
    }

    /**
     * Invoked when GUI resources are loaded and injected
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnRun.selectedProperty().bindBidirectional(running);

        // when ram values change add it to the chart
        hwObserver.usedMemoryProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                String dateTag = formatter.format(new Date());
                usedRAMPercentages.getData().add(new XYChart.Data(dateTag, newValue.doubleValue() / (double)hwObserver.getTotalMemory()));
                cpusSUMPercentages.getData().add(new XYChart.Data(dateTag, hwObserver.getCpuSumPercentage()));
            }
        });

        chart.setData(lineChartData); // enlazamos los datos a la gráfica

        // sound
        rmsProgressBar.progressProperty().bind(soundReader.lastRMSProperty());

        // tableview
        tableView.itemsProperty().bindBidirectional(model.getMeasurements());
        colCPU.setCellValueFactory(new PropertyValueFactory<Measurement, Float>("cpuLoad")); // cpuLoad = name of the property in Measurement class
        colRAM.setCellValueFactory(new PropertyValueFactory<Measurement, Float>("memoryLoad"));
        colRMS.setCellValueFactory(new PropertyValueFactory<Measurement, Float>("rms"));
    }

    @FXML
    private void handleSnapshot() {
        model.addMeasurement(new Measurement(soundReader.lastRMSProperty().get(), hwObserver.getCpuSumPercentage(), hwObserver.getTotalMemory()));
    }

    @FXML
    private void handleMenRunStop() {
        running.setValue(running.not().get());
    }

    @FXML
    private void handleClose() {
        Platform.exit();
    }

    @FXML
    private void handleOpen() {
        OpenSaveFileDialog dialog = new OpenSaveFileDialog();
        File file = dialog.openFile("WidgetHW", "WidgetHW XML", "xml");
        if (file != null) {
            try {
                model.open(file);
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot load model", e);
                new DialogoError().show("HWWidget", "Cannot load model", e);
            }
        }

    }

    @FXML
    private void handleSaveAs() {
        OpenSaveFileDialog dialog = new OpenSaveFileDialog();
        File file = dialog.saveFile("WidgetHW", "WidgetHW XML", "xml");
        if (file != null) {
            try {
                model.saveAs(file);
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot save model", e);
                new DialogoError().show("HWWidget", "Cannot save model", e);
            }
        }
    }

}
