package tools.simulation;

import org.junit.Test;
import tools.simulation.utils.Log;

public class LoggerTest {

    @Test
    public void LoggerTest() {

        Log.logConfigs();
        Log.debug("Testing log - level Debug");
        Log.warn("Testing log - level Warn");
        Log.info("Testing log - level Info");
        Log.error("Testing log - level Error");
        Log.fatal("Testing log - level Fatal");

    }

}
