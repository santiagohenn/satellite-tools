package simulation;

import org.junit.Test;
import simulation.assets.objects.Satellite;
import simulation.structures.Ephemeris;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SimulationTest {

    @Test
    public void testClassInstantiation() {
        Simulation simulation = new Simulation();
        assertNotNull(simulation.getStartTime());
        assertNotNull(simulation.getEndTime());
    }

    @Test
    public void testSimulationWithSatelliteFromTLE() {
        Satellite satellite = new Satellite();
        satellite.setTLE("1 25544U 98067A   22122.68846215  .00030457  00000-0  54086-3 0  9994",
                "2 25544  51.6435 201.9265 0006436  54.9097 105.7177 15.49915502338120");
        Simulation simulation = new Simulation(satellite);
        assertNotNull(simulation.getSatellite());
        assertNotNull(simulation.getSatellite().getTLE1());
        assertNotNull(simulation.getSatellite().getTLE2());
        assertEquals("1 25544U 98067A   22122.68846215  .00030457  00000-0  54086-3 0  9994", simulation.getSatellite().getTLE1());
        assertEquals("2 25544  51.6435 201.9265 0006436  54.9097 105.7177 15.49915502338120", simulation.getSatellite().getTLE2());
    }

    @Test
    public void testPropagationWithSatelliteFromTLE() {
        Satellite satellite = new Satellite();
        satellite.setTLE("1 25544U 98067A   22122.68846215  .00030457  00000-0  54086-3 0  9994",
                "2 25544  51.6435 201.9265 0006436  54.9097 105.7177 15.49915502338120");
        Simulation simulation = new Simulation(satellite);
        Ephemeris ephemeris = simulation.computePVDAt("2025-03-20T12:00:00.000");

    }

}
