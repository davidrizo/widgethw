package es.ua.dlsi.master.widgethw;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class performs a busy waiting checking values from the hardware
 * @autor drizo
 */
public class HWObserver {
    private final IHWReader hwReader;
    private Timer timer;
    /**
     * Delay in ms between succesive reads. By default it is 1000ms
     */
    private LongProperty refreshTime;
    private LongProperty usedMemory;
    private DoubleProperty cpuSumPercentage;
    private long totalMemory;

    public HWObserver(IHWReader reader) {
        this.hwReader = reader;
        this.totalMemory = reader.getMemoryAmount();
        this.refreshTime = new SimpleLongProperty(1000);
        usedMemory = new SimpleLongProperty();
        cpuSumPercentage = new SimpleDoubleProperty();
    }

    private TimerTask createTimerTask() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Logger.getLogger(this.getClass().getName()).log(Level.FINE, "HW Observer read");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        // Change it inside this method because it may with the main JavaFX thread through binding
                        usedMemory.setValue(hwReader.getMemoryUsed());
                        double[] load = hwReader.getCPULoad();
                        double sum = 0;
                        for (double d : load) {
                            sum += d;
                        }
                        cpuSumPercentage.setValue((double) sum / (double) load.length);
                    }
                });
            }
        };
        return timerTask;
    }

    public void run() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "HW Observer run");
        timer = new Timer(true);
        timer.scheduleAtFixedRate(createTimerTask(), 0, refreshTime.get());
    }

    public void stop() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "HW Observer stopped");
        timer.cancel();
    }

    /**
     * It should be changed when timer is not run
     * @return
     */
    public LongProperty refreshTimeProperty() {
        return refreshTime;
    }

    public LongProperty usedMemoryProperty() {
        return usedMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public double getCpuSumPercentage() {
        return cpuSumPercentage.get();
    }

    public DoubleProperty cpuSumPercentageProperty() {
        return cpuSumPercentage;
    }
}
