package simulation;

import org.junit.Test;
import simulation.assets.objects.Device;
import simulation.assets.objects.Satellite;
import simulation.structures.Ephemeris;
import simulation.utils.Utils;

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
        Device device = new Device(15, 15, 0.3);
        simulation.setDevice(device);
        Ephemeris ephemeris = simulation.computePVDAt("2025-03-20T12:00:00.000");
        assertNotNull(ephemeris);
    }

    @Test
    public void testPropagationWithSatelliteFromTLE2() {
        Satellite satellite = new Satellite();
        satellite.setTLE("1 25544U 98067A   22122.68846215  .00030457  00000-0  54086-3 0  9994",
                "2 25544  51.6435 201.9265 0006436  54.9097 105.7177 15.49915502338120");
        Device device = new Device(15, 15, 3);
        Simulation simulation = new Simulation("2022-03-20T01:00:00.000", "2022-03-20T23:00:00.000", device, satellite, 60, 5);
        simulation.setDevice(device);
        simulation.computeAccess();
        simulation.getIntervals().forEach(System.out::println);
        simulation.getIntervals().forEach(interval -> {
            System.out.println(Utils.unix2stamp(interval.getStart()) + "," + Utils.unix2stamp(interval.getEnd()) + "," + interval.getDuration()/1000.0);
        });
        Ephemeris ephemeris = simulation.computePVDAt("2025-03-20T12:00:00.000");
        System.out.println(ephemeris.toString());
        assertNotNull(ephemeris);
    }

}
