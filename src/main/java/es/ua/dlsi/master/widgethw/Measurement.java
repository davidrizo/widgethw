package es.ua.dlsi.master.widgethw;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;

/**
 * @autor drizo
 */
public class Measurement {
    private FloatProperty rms;
    private DoubleProperty cpuLoad;
    private DoubleProperty memoryLoad;

    public Measurement(float rms, double cpu, double memory) {
        this.rms = new SimpleFloatProperty(rms);
        this.cpuLoad = new SimpleDoubleProperty(cpu);
        this.memoryLoad = new SimpleDoubleProperty(memory);
    }

    public float getRms() {
        return rms.get();
    }

    public FloatProperty rmsProperty() {
        return rms;
    }

    public double getCpuLoad() {
        return cpuLoad.get();
    }

    public DoubleProperty cpuLoadProperty() {
        return cpuLoad;
    }

    public double getMemoryLoad() {
        return memoryLoad.get();
    }

    public DoubleProperty memoryLoadProperty() {
        return memoryLoad;
    }
}
