# satellite-tools

Simple interface for orbital propagation tools, focused on scenarios 
and access intervals between **assets**: either **Satellites** or **Devices** on the
surface of the Earth.

---
# Basic usage

### Obtain access intervals for a given scenario time-span
```java
// Declare a satellite from its TLE
String tle1 = "1 25544U 98067A   22122.68846215  .00030457  00000-0  54086-3 0  9994";
String tle2 = "2 25544  51.6435 201.9265 0006436  54.9097 105.7177 15.49915502338120";
Satellite satellite = new Satellite(tle1, tle2);

// Declare a Device with its Latitude, Longitude and Height (deg, deg, meters)
Device device = new Device(15, 15, 3);

// Instanciate a simulation with a start time, end time, devices involved, time step in seconds and 
// visibility threshold in degrees
Simulation simulation = new Simulation("2022-03-20T12:00:00.000", "2022-03-20T15:00:00.000", 
        device, satellite, 60, 5);

// Compute access intervals
simulation.computeAccess();

// Print intervals in Unix timestamp
System.out.println("Start,End,Duration[ms]");
simulation.getIntervals().forEach(System.out::println);

// Print intervals in yyyy-MM-dd'T'HH:mm:SS.sss format
System.out.println("Start,End,Duration[s]");
simulation.getIntervals().forEach(interval -> {
System.out.println(Utils.unix2stamp(interval.getStart()) + "," 
        + Utils.unix2stamp(interval.getEnd()) + "," + interval.getDuration()/1000.0);
});

```
### Obtain Position, Velocity and Doppler for a specific moment in time
```java
// Declare a satellite from its orbital elements
// date of the elements, sem-maj axis, ecc, inc, RAAN, w, v
OrbitalElements orbitalElements = new OrbitalElements("2020-01-01T19:40:00.000",6978135,0,98,310,0,220);
Satellite satellite = new Satellite(orbitalElements);
Device device = new Device(15, 15, 3);
Simulation simulation = new Simulation("2022-03-20T01:00:00.000", "2022-03-20T23:00:00.000"
        , device, satellite, 60, 5);

Ephemeris ephemeris = simulation.computePVDAt("2022-03-20T03:00:00.000");
System.out.println("time(unix ts),posX,posY,posZ,velX,velY,velZ,range[m],doppler[m/s]");
System.out.println(ephemeris);

```