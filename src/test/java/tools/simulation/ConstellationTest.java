package tools.simulation;

import org.junit.Test;
import tools.constellation.Constellation;
import tools.simulation.assets.entities.Device;
import tools.simulation.assets.entities.Satellite;
import tools.simulation.structures.OrbitalElements;

import java.util.ArrayList;
import java.util.List;

public class ConstellationTest {

    @Test
    public void testConstellation() {

        List<Satellite> satelliteList = new ArrayList<>();
        List<Device> deviceList = new ArrayList<>();

        // Define 3 trailing satellites
        for (int i = 0; i < 3; i++) {
            OrbitalElements orbitalElements = new OrbitalElements("2022-01-01T19:40:00.000",6978135,
                    0,98,310,0,220 + i * 5);
            Satellite satellite = new Satellite(orbitalElements);
            satelliteList.add(satellite);
        }

        // Define some random nodes
        deviceList.add(new Device(45,15,3));
        deviceList.add(new Device(46,16,2));
        deviceList.add(new Device(47,17,1));

        Constellation constellation = new Constellation("2022-03-20T01:00:00.000",
                "2022-03-20T23:00:00.000", deviceList, satelliteList, 60, 5);

        constellation.setIncludeCoverageGaps(true);
//        constellation.computeSatellitesPOV();
        constellation.computeDevicesPOV();
        System.out.println("start(unix),end(unix),from,to,duration[ms]");
        constellation.getAllAccesses().forEach(System.out::println);


    }

}
