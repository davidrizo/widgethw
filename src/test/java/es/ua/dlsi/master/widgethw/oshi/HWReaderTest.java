package es.ua.dlsi.master.widgethw.oshi;

import org.junit.Test;

import static org.junit.Assert.*;

public class HWReaderTest {
    @Test
    public void testHWReaderCPULoad() {
        HWReader reader = new HWReader();

        int processors = reader.getNumLogicalProcessors();
        assertTrue("At least 1 processor", processors > 1);

        double[] cpuLoad = reader.getCPULoad();
        assertEquals("CPU load size", processors, cpuLoad.length);

        for (int i=0; i<cpuLoad.length; i++) {
            assertTrue("CPU load in ]0,1]", cpuLoad[i] > 0 && cpuLoad[i] <= 1.0);
        }

        long memoryUsed = reader.getMemoryUsed();
        assertTrue("At least 256Mb", memoryUsed < reader.getMemoryAmount() && memoryUsed > 1024*1024*256);
    }

}