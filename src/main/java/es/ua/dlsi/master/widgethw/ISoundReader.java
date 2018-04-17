package es.ua.dlsi.master.widgethw;

import javafx.beans.property.FloatProperty;

/**
 * @autor drizo
 */
public interface ISoundReader {
    void start();
    void stop();
    FloatProperty lastRMSProperty();
    FloatProperty lastPeakProperty();
}
