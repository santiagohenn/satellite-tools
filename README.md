# satellite-tools

Simple interface for orbital propagation tools, focused on space system scenarios
and access intervals between **assets**: either **Satellites** or **Devices** on the
surface of the Earth.

# Configuration

### Simulation
Using the sim.properties file in classpath:

```properties
# Orbit extrapolation data folder path. Can be cloned from: https://gitlab.orekit.org/orekit/orekit-data
orekit_data_path=C:/Projects/orekit-data
# Default start date
start_date=2022-01-01T20:20:00.000
# Default end date
end_date=2022-01-01T20:45:00.000
# Default time step in seconds
time_step=60
# Minimum visibility threshold over the horizon, in degrees
visibility_threshold=5
# Access interval threshold detection in seconds (internal propagator parameter)
th_detection=0.001
```

### Logger

To configure the Logger use the log4j2.properties in the classpath or another valid method.
Refer to [Log4j configuration](https://logging.apache.org/log4j/2.x/manual/configuration.html) for more information.

### Install in local maven

Build the project and then install the jar in local maven:

```
mvn install:install-file \ 
        -Dfile=<Jar location> \
        -DgroupId=com.santiagohenn -DartifactId=satellite-tools \
        -Dversion=2.0 \ 
        -Dpackaging=jar \
        -DgeneratePom=true \
```
---
# Basic usage

#### Obtain access intervals for a given scenario time-span with default properties from file
```java
// Instantiate a satellite from its TLE
Satellite satellite = new Satellite("1 25544U 98067A   22122.68846215  .00030457  00000-0  54086-3 0  9994",
"2 25544  51.6435 201.9265 0006436  54.9097 105.7177 15.49915502338120");
// Instantiate a Device with its Latitude, Longitude and Height (deg, deg, meters)
Device device = new Device(15, 15, 3);
// Instantiate simulation with the device and satellite as parameters
Simulation sim = new Simulation(device, satellite);
// Propagate orbits and obtain intervals of connectivity
sim.computeAccess();
// This will print the intervals start and end time as a unix timestamp
System.out.println("Start,End,Duration[ms]");
sim.getIntervals().forEach(System.out::println);
```

#### Obtain access intervals for a given scenario time-span, with specific parameters
```java
// Declare a satellite from its TLE
Satellite satellite = new Satellite("1 25544U 98067A   22122.68846215  .00030457  00000-0  54086-3 0  9994",
        "2 25544  51.6435 201.9265 0006436  54.9097 105.7177 15.49915502338120");
Device device = new Device(15, 15, 3);

// Instanciate simulation with a start time, end time, devices involved, time step in seconds and 
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

#### Obtain Position, Velocity and Doppler for a specific moment in time
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