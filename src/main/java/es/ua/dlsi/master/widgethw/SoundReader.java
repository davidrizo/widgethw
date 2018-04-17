package es.ua.dlsi.master.widgethw;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @autor drizo
 */
public class SoundReader implements ISoundReader {
    final static int bufferByteSize = 2048;
    private final AudioFormat audioFormat;
    TargetDataLine line;

    FloatProperty rmsProperty;
    FloatProperty peakProperty;

    public SoundReader() throws LineUnavailableException {
        rmsProperty = new SimpleFloatProperty();
        peakProperty = new SimpleFloatProperty();
        audioFormat = new AudioFormat(44100f, 16, 1, true, false);

        line = AudioSystem.getTargetDataLine(audioFormat);
        line.open(audioFormat, bufferByteSize);
    }

    /**
     * Code from: https://stackoverflow.com/questions/26574326/how-to-calculate-the-level-amplitude-db-of-audio-signal-in-java
     */
    public void run() {
        byte[] buf = new byte[bufferByteSize];
        float[] samples = new float[bufferByteSize / 2];

        float lastPeak = 0f;

        line.start();
        for (int b; (b = line.read(buf, 0, buf.length)) > -1; ) {
            // convert bytes to samples here
            for (int i = 0, s = 0; i < b; ) {
                int sample = 0;

                sample |= buf[i++] & 0xFF; // (reverse these two lines
                sample |= buf[i++] << 8;   //  if the format is big endian)

                // normalize to range of +/-1.0f
                samples[s++] = sample / 32768f;
            }

            float rms = 0f;
            float peak = 0f;
            for (float sample : samples) {
                float abs = Math.abs(sample);
                if (abs > peak) {
                    peak = abs;
                }

                rms += sample * sample;
            }

            rms = (float) Math.sqrt(rms / samples.length);

            if (lastPeak > peak) {
                peak = lastPeak * 0.875f;
            }

            lastPeak = peak;
            final float finalRms = rms;
            final float finalPeak = peak;
            Platform.runLater(new Runnable() { // to avoid dealocks when interacting main JavaFX thread
                @Override
                public void run() {
                    rmsProperty.setValue(finalRms);
                    peakProperty.setValue(finalPeak);
                }
            });

            Logger.getLogger(SoundReader.class.getName()).log(Level.FINE, "RMS {0}", rms);
            Logger.getLogger(SoundReader.class.getName()).log(Level.FINE, "PEAK {0}", peak);
        }
    }

    @Override
    public void start() {
        run();
    }

    public void stop() {
            line.stop();
    }

    @Override
    public FloatProperty lastRMSProperty() {
        return rmsProperty;
    }

    @Override
    public FloatProperty lastPeakProperty() {
        return peakProperty;
    }
}
