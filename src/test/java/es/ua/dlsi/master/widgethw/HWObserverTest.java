package es.ua.dlsi.master.widgethw;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import es.ua.dlsi.master.widgethw.oshi.HWReader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(ConcurrentTestRunner.class)
public class HWObserverTest {
    HWObserver hwObserver;

    @Before
    public void init() throws InterruptedException {
        hwObserver = new HWObserver(new HWReader());
        hwObserver.refreshTimeProperty().setValue(500);
        hwObserver.run();
    }

    @Test
    public void stop() throws InterruptedException {
        Thread.currentThread().wait(1100); // TODO Hacerlo bien
        hwObserver.stop();
    }
}