package es.ua.dlsi.master.widgethw.oshi;

import es.ua.dlsi.master.widgethw.IHWReader;
import oshi.SystemInfo;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * It reads information from the hardware
 * @autor drizo
 */
public class HWReader implements IHWReader {
    HardwareAbstractionLayer hal;

    public HWReader() {
        SystemInfo si = new SystemInfo();
        hal = si.getHardware();
    }

    @Override
    public int getNumLogicalProcessors() {
        return hal.getProcessor().getLogicalProcessorCount();
    }

    /**
     * Get CPU load for each microprocessor
     * @return double array (values [0..1]), each cell contains the CPU load of a microprocessor
     */
    @Override
    public double[] getCPULoad() {
        return hal.getProcessor().getProcessorCpuLoadBetweenTicks();
    }

    @Override
    public long getMemoryAmount() {
        return hal.getMemory().getTotal();
    }

    @Override
    public long getMemoryUsed() {
        return hal.getMemory().getTotal() - hal.getMemory().getAvailable();
    }
}
