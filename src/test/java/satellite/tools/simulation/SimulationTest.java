package satellite.tools.simulation;

import org.junit.Test;
import satellite.tools.Simulation;
import satellite.tools.assets.entities.Position;
import satellite.tools.assets.entities.Satellite;
import satellite.tools.structures.Ephemeris;
import satellite.tools.structures.OrbitalElements;
import satellite.tools.utils.Log;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SimulationTest {

    @Test
    public void testLoadProperties() {
        Simulation simulation = new Simulation("C:/Projects/orekit-data/");
        Properties p = simulation.getProperties();
        Log.debug("Orekit data path: " + p.get("orekit_data_path"));
        Log.debug("Start date: " + p.get("start_date"));
        Log.debug("End date: " + p.get("end_date"));
        Log.debug("Time step: " + p.get("time_step"));
        Log.debug("Vis. TH: " + p.get("visibility_threshold"));
        Log.debug("th_detection: " + p.get("th_detection"));
    }

    @Test
    public void testDefaultSettings() {
//        Satellite satellite = new Satellite("1 25544U 98067A   22122.68846215  .00030457  00000-0  54086-3 0  9994",
//                "2 25544  51.6435 201.9265 0006436  54.9097 105.7177 15.49915502338120");
//        Device device = new Device(15, 15, 3);
//        Simulation sim = new Simulation("C:/Projects/orekit-data/");
//        sim.setAssets(device, satellite);
//        sim.run();
//        Log.debug("Access intervals between " + sim.getStartTime() + " -> " + sim.getEndTime());
//        Log.debug("N of intervals: " + sim.getIntervals().size());
//        sim.getIntervals().forEach(System.out::println);
//        sim.getIntervals().forEach(interval -> Log.debug(interval.toString()));

    }

    @Test
    public void testDefaultSettings2() {
//
//        Simulation sim = new Simulation("C:/Projects/orekit-data/");
//        String tle1 = "1 25544U 98067A   22122.68846215  .00030457  00000-0  54086-3 0  9994";
//        String tle2 = "2 25544  51.6435 201.9265 0006436  54.9097 105.7177 15.49915502338120";
//        Satellite satellite = new Satellite(tle1, tle2);
//
//        // Declare a Device with its Latitude, Longitude and Height (deg, deg, meters)
//        Device device = new Device(15, 15, 3);
//
//        // Instanciate a tools.simulation with a start time, end time, devices involved, time step in seconds and
//        // visibility threshold in degrees
//        Simulation simulation = new Simulation("2022-03-20T12:00:00.000", "2022-03-20T15:00:00.000",
//                device, satellite, 60, 5);
//
//        // Compute access intervals
//        simulation.computeAccess();
//
//        // Print intervals in Unix timestamp
//        System.out.println("Start,End,Duration[ms]");
//        simulation.getIntervals().forEach(System.out::println);
//
//        // Print intervals in yyyy-MM-dd'T'HH:mm:SS.sss format
//        System.out.println("Start,End,Duration[s]");
//        simulation.getIntervals().forEach(interval -> {
//            System.out.println(Utils.unix2stamp(interval.getStart()) + ","
//                    + Utils.unix2stamp(interval.getEnd()) + "," + interval.getDuration() / 1000.0);
//        });
    }

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
        Position device = new Position(15, 15, 0.3);
        simulation.setDevice(device);
        Ephemeris ephemeris = simulation.computePVDAt("2025-03-20T12:00:00.000");
        assertNotNull(ephemeris);
    }

    @Test
    public void testPropagationWithSatelliteFromTLE2() {

        OrbitalElements orbitalElements = new OrbitalElements("2020-01-01T19:40:00.000", 6978135, 0, 98, 310, 0, 220);
        Satellite satellite = new Satellite(orbitalElements);
        Position device = new Position(15, 15, 3);
        Simulation simulation = new Simulation("2022-03-20T01:00:00.000", "2022-03-20T23:00:00.000", device, satellite, 60, 5);
        Ephemeris ephemeris = simulation.computePVDAt("2022-03-20T03:00:00.000");
        System.out.println("time(unix ts),posX,posY,posZ,velX,velY,velZ,range[m],doppler[m/s]");
        System.out.println(ephemeris);
        assertNotNull(ephemeris);
    }

    @Test
    public void testComputeAccessIntervals() {

        OrbitalElements orbitalElements = new OrbitalElements("2020-01-01T19:40:00.000", 6978135, 0, 98, 310, 0, 220);
        Satellite satellite = new Satellite(orbitalElements);
        Position device = new Position(15, 15, 3);
        Simulation simulation = new Simulation("2022-03-20T01:00:00.000", "2022-03-20T23:00:00.000", device, satellite, 60, 5);
        simulation.computeAccess();
        simulation.getIntervals().forEach(System.out::println);
        assertNotNull(simulation.getIntervals());
    }

}
