package es.ua.dlsi.master.widgethw;

/**
 * @author drizo
 */
public interface IHWReader {
    /**
     * Number of logical processors
     * @return
     */
    int getNumLogicalProcessors();
    /**
     * Get CPU load for each microprocessor
     * @return double array (values [0..1]), each cell contains the CPU load of a microprocessor
     */
    double[] getCPULoad();

    /**
     * Total RAM memory in bytes
     * @return
     */
    long getMemoryAmount();

    /**
     * Used RAM memory in bytes
     * @return
     */
    long getMemoryUsed();
}
