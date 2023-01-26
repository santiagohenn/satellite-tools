package satellite.tools;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.events.Action;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.frames.Transform;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;
import org.orekit.utils.TimeStampedPVCoordinates;
import satellite.tools.assets.Asset;
import satellite.tools.assets.entities.Position;
import satellite.tools.assets.entities.Satellite;
import satellite.tools.structures.Ephemeris;
import satellite.tools.structures.Interval;
import satellite.tools.utils.Log;
import satellite.tools.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Simulation condenses the main entry class for the software. It propagates orbits and output results based on the
 * configured Satellite object and parameters such as date and reference frames.
 */
public class Simulation {

    /**
     * Initial properties and extrapolation variables and orekit data path
     */
    private Properties prop = Utils.loadProperties("sim.properties");
    private static final DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
    private Frame inertialFrame;
    private Frame fixedFrame;
    private BodyShape earth;
    private double TH_DETECTION = Double.parseDouble((String) prop.get("th_detection"));

    /**
     * Mutable variables
     **/
    private String time1 = (String) prop.get("start_date");
    private String time2 = (String) prop.get("end_date");
    private double timeStepSeconds = Double.parseDouble((String) prop.get("time_step"));
    private double visibilityThreshold = Math.toRadians(Double.parseDouble((String) prop.get("visibility_threshold")));
    private TopocentricFrame topocentricFrame;
    private TLEPropagator tlePropagator;
    private Satellite satellite;
    private Position device;
    private List<Interval> intervalList = new ArrayList<>();
    private List<Ephemeris> ephemerisList = new ArrayList<>();
    private Date contact = new Date();
    private double lastSimTime = 0;

    /**
     * Default constructor
     **/
    public Simulation() {
        loadOrekit();
    }

    /**
     * Orekit path specified constructor
     **/
    public Simulation(String orekitPath) {
        File orekitFile = Utils.loadDirectory(orekitPath);
        manager.addProvider(new DirectoryCrawler(orekitFile));
        Log.debug("Orekit data provider loaded from: " + orekitPath);
        setDefaultFrames();
    }

    /**
     * Constructor that receives the Position and Velocity coordinates from a Satellite object,
     * with its cartesian coordinates in respect to the IERS 2010 Earth's frame reference
     */
    public Simulation(Satellite satellite) {
        loadOrekit();
        setSatellite(satellite);
    }

    /**
     * Constructor that receives a Satellite object and a Device object.
     */
    public Simulation(Position device, Satellite satellite) {
        loadOrekit();
        setDevice(device);
        setSatellite(satellite);
    }

    /**
     * A Class constructor that configures the main parameters needed for a pair device-satellite on a scenario (replaces
     * default values)
     */
    public Simulation(String timeStart, String timeEnd, Position device, Satellite satellite, double step, double th) {
        loadOrekit();
        setSatellite(satellite);
        setDevice(device);
        setParams(timeStart, timeEnd, step, th);
    }

    public void loadOrekit() {

        if (manager.getLoadedDataNames().stream()
                .allMatch(s -> s.contains("itrf.versions.conf") || s.contains("tai-utc.dat") || s.contains("finals2000A.all"))) {
            Log.debug("Loading orbital data: itrf.versions.conf, tai-utc.dat, finals2000A.all");
            String orekitPath = (String) prop.get("orekit_data_path");
            if (orekitPath == null || orekitPath.isBlank() || orekitPath.isEmpty()) {
                Log.error("Insert orekit_data_path in properties or use the corresponding path-specified constructor.");
                throw new RuntimeException("Insert orekit_data_path in properties or use the corresponding path-specified constructor.");
            }
            File orekitFile = Utils.loadDirectory(orekitPath);
            manager.addProvider(new DirectoryCrawler(orekitFile));
            Log.debug("Loaded datasets: " + manager.getLoadedDataNames());
        }

        if (inertialFrame == null || fixedFrame == null || earth == null) {
            setDefaultFrames();
        }

    }

    private void setDefaultFrames() {
        setInertialFrame(FramesFactory.getEME2000());
        setFixedFrame(FramesFactory.getITRF(IERSConventions.IERS_2010, true));
    }

    public void setInertialFrame(Frame inertialFrame) {
        this.inertialFrame = inertialFrame;
    }

    public void setFixedFrame(Frame fixedFrame) {
        this.fixedFrame = fixedFrame;
        earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                Constants.WGS84_EARTH_FLATTENING,
                fixedFrame);
    }

    public void setParams(String timeStart, String timeEnd, double step, double visibilityThreshold) {
        this.time1 = timeStart;
        this.time2 = timeEnd;
        this.timeStepSeconds = step;
        setVisibilityThreshold(visibilityThreshold);
    }

    public double getVisibilityThreshold() {
        return visibilityThreshold;
    }

    public void setVisibilityThreshold(double visibilityThreshold) {
        this.visibilityThreshold = Math.toRadians(visibilityThreshold);
    }

    public String getStartTime() {
        return this.time1;
    }

    public String getEndTime() {
        return this.time2;
    }

    public long getStartTimeUnix() {
        return Utils.stamp2unix(this.time1);
    }

    public long getEndTimeUnix() {
        return Utils.stamp2unix(this.time2);
    }

    public void setStartTime(String time1) {
        this.time1 = time1;
    }

    public void setEndTime(String time2) {
        this.time2 = time2;
    }

    public void setStartTime(long time1) {
        this.time1 = Utils.unix2stamp(time1);
    }

    public void setEndTime(long time2) {
        this.time2 = Utils.unix2stamp(time2);
    }

    public long getTimeSpan() {
        return (Utils.stamp2unix(this.time1) - Utils.stamp2unix(this.time2));
    }

    public Satellite getSatellite() {
        return satellite;
    }

    public Position getDevice() {
        return device;
    }

    public int getDeviceId() {
        return this.device.getId();
    }

    public int getSatelliteId() {
        return this.satellite.getId();
    }

    public double getThDetection() {
        return TH_DETECTION;
    }

    public List<Interval> getIntervals() {
        return intervalList;
    }

    public void setAssets(Position device, Satellite satellite) {
        setDevice(device);
        setSatellite(satellite);
    }

    public void setAsset(Asset asset) {
        if (asset.getClass().isAssignableFrom(Position.class)) {
            setDevice((Position) asset);
        } else if (asset.getClass().isAssignableFrom(Satellite.class)) {
            setSatellite((Satellite) asset);
        }
    }

    public void setDevice(Position device) {
        this.device = device;
        GeodeticPoint geodeticPoint = new GeodeticPoint(device.getLatRad(), device.getLonRad(), device.getHeight());
        this.topocentricFrame = new TopocentricFrame(earth, geodeticPoint, device.getName());
    }

    public void setSatellite(Satellite satellite) {
        this.satellite = satellite;
        TLE tle;
        if (satellite.getTLE1().isEmpty() || satellite.getTLE2().isEmpty()) {
            tle = Utils.satellite2tle(satellite);
        } else {
            tle = new TLE(satellite.getTLE1(), satellite.getTLE2());
        }
        this.tlePropagator = TLEPropagator.selectExtrapolator(tle);
    }

    public double getTotalAccess() {
        double sum = 0;
        for (Interval interval : intervalList) {
            sum = sum + interval.getDuration();
        }
        return sum;
    }

    public void setTimeStepSeconds(double timeStepSeconds) {
        this.timeStepSeconds = timeStepSeconds;
    }

    //    @SuppressWarnings("squid:S2184")
    public void computeAccess() {

        long t0 = System.currentTimeMillis();

        intervalList = new ArrayList<>();

        contact.setTime(Utils.stamp2unix(time1));
        EventDetector elevDetector = new ElevationDetector(timeStepSeconds, TH_DETECTION, topocentricFrame).
                withConstantElevation(visibilityThreshold).
                withHandler(
                        (s, detector, increasing) -> {
                            addInterval(s, increasing);
                            return Action.CONTINUE;
                        });

        this.tlePropagator.addEventDetector(elevDetector);
        accessBetweenDates(Utils.stamp2AD(time1), Utils.stamp2AD(time2));
        lastSimTime = System.currentTimeMillis() - t0;

    }

    private void accessBetweenDates(AbsoluteDate time1, AbsoluteDate time2) {
        double scenarioTime = time2.durationFrom(time1);
        tlePropagator.propagate(time1, time1.shiftedBy(scenarioTime));
    }

    private void addInterval(SpacecraftState s, boolean dir) {
        try {
            if (dir) {
                contact = s.getDate().toDate(TimeScalesFactory.getUTC());
            } else {
                intervalList.add(new Interval(contact.getTime(), s.getDate().toDate(TimeScalesFactory.getUTC()).getTime(), this.device.getId(), this.satellite.getId()));
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    public void computePVD() {
        propagateAndComputePVD(Utils.stamp2AD(time1), Utils.stamp2AD(time2), this.timeStepSeconds);
    }

    public void computePVDBetween(long startTime, long endTime) {
        computePVDBetween(startTime, endTime, this.timeStepSeconds);
    }

    public void computePVDBetween(long startTime, long endTime, double stepInSeconds) {
        computePVDBetween(Utils.unix2stamp(startTime), Utils.unix2stamp(endTime), stepInSeconds);
    }

    public void computePVDBetween(String startTime, String endTime) {
        computePVDBetween(startTime, endTime, this.timeStepSeconds);
    }

    public void computePVDBetween(String startTime, String endTime, double stepInSeconds) {
        propagateAndComputePVD(Utils.stamp2AD(startTime), Utils.stamp2AD(endTime), stepInSeconds);
    }

    public Ephemeris computeTopocentricEphemeris(long timestamp, double step) {
        this.timeStepSeconds = step;
        return computeTopocentricEphemeris(Utils.unix2stamp(timestamp));
    }

    public Ephemeris computeTopocentricEphemeris(String timestamp) {
        return computeTopocentricEphemeris(Utils.stamp2AD(timestamp));
    }

    public Ephemeris computeTopocentricEphemeris(AbsoluteDate absoluteDate) {
        PVCoordinates pvInert = tlePropagator.propagate(absoluteDate).getPVCoordinates();
        var pvCoordinates = inertialFrame.getTransformTo(topocentricFrame, absoluteDate).transformPVCoordinates(pvInert);
        return toEphemeris(absoluteDate, pvCoordinates);
    }

    @SuppressWarnings("squid:S2184")
    private void propagateAndComputePVD(AbsoluteDate startDate, AbsoluteDate endDate, double step) {

        long t0 = System.currentTimeMillis();
        ephemerisList.clear();
        var lastPoint = false;
        AbsoluteDate pointerDate = startDate;
        while (pointerDate.compareTo(endDate) <= 0) {
            // Get the position and velocity of spacecraft in station frame at any time
            PVCoordinates pvInert = tlePropagator.propagate(pointerDate).getPVCoordinates();
            var pvDevice = inertialFrame.getTransformTo(topocentricFrame, pointerDate).transformPVCoordinates(pvInert);

            addEphemeris(pointerDate, pvDevice);
            pointerDate = pointerDate.shiftedBy(step);

            if (pointerDate.compareTo(endDate) > 0 && !lastPoint) {
                pointerDate = endDate;
                lastPoint = true;
            }

        }
        lastSimTime = System.currentTimeMillis() - t0;
    }

    public Ephemeris computeFixedEphemerisKm(AbsoluteDate absoluteDate) {

        Ephemeris e = computeFixedEphemeris(absoluteDate);
        e.setPos(e.getPosX() / 1000.0, e.getPosY() / 1000.0, e.getPosZ() / 1000.0);
        return e;

    }

    public Ephemeris computeFixedEphemeris(AbsoluteDate absoluteDate) {

        PVCoordinates pvCoordinatesInertial = tlePropagator.propagate(absoluteDate).getPVCoordinates();
        TimeStampedPVCoordinates timeStampedPVCoordinates = new TimeStampedPVCoordinates(absoluteDate, pvCoordinatesInertial);
        Transform inertial2fixed = inertialFrame.getTransformTo(fixedFrame, absoluteDate);
        TimeStampedPVCoordinates pvCoordinatesFixed = inertial2fixed.transformPVCoordinates(timeStampedPVCoordinates);

        Ephemeris eph = new Ephemeris();

        eph.setPos(pvCoordinatesFixed.getPosition().getX(),
                pvCoordinatesFixed.getPosition().getY(),
                pvCoordinatesFixed.getPosition().getZ());

        eph.setVel(pvCoordinatesFixed.getVelocity().getX(),
                pvCoordinatesFixed.getVelocity().getY(),
                pvCoordinatesFixed.getVelocity().getZ());

        timeStampedPVCoordinates = earth.projectToGround(pvCoordinatesFixed, inertialFrame);

        double alpha = timeStampedPVCoordinates.getPosition().getAlpha();
        double delta = timeStampedPVCoordinates.getPosition().getDelta();
        double height = timeStampedPVCoordinates.getPosition().getNorm();

        eph.setSSP(delta, alpha, height);

        return eph;

    }

    public Ephemeris computeInertialEphemeris(AbsoluteDate absoluteDate) {

        PVCoordinates pvCoordinates = tlePropagator.propagate(absoluteDate).getPVCoordinates();

        Ephemeris eph = new Ephemeris();
        eph.setPos(pvCoordinates.getPosition().getX(),
                pvCoordinates.getPosition().getY(),
                pvCoordinates.getPosition().getZ());

        eph.setVel(pvCoordinates.getVelocity().getX(),
                pvCoordinates.getVelocity().getY(),
                pvCoordinates.getVelocity().getZ());

        return eph;

    }

    private void addEphemeris(AbsoluteDate absoluteDate, PVCoordinates pvCoordinates) {
        ephemerisList.add(toEphemeris(absoluteDate, pvCoordinates));
    }

    private Ephemeris toEphemeris(AbsoluteDate absoluteDate, PVCoordinates pvDevice) {
        return toEphemeris(absoluteDate.toDate(TimeScalesFactory.getUTC()), pvDevice);
    }

    private Ephemeris toEphemeris(Date date, PVCoordinates pvDevice) {

        // Get the satellite's position and velocity in reference to the station
        Vector3D pos = pvDevice.getPosition();
        Vector3D vel = pvDevice.getVelocity();

        // Calculate Range
        double range = pvDevice.getPosition().getNorm();

        // Calculate the doppler signal
        double doppler = Vector3D.dotProduct(pvDevice.getPosition(), pvDevice.getVelocity()) / range;

        return new Ephemeris(date.getTime(), this.getDeviceId(), this.getSatelliteId(), pos.getX(), pos.getY(),
                pos.getZ(), vel.getX(), vel.getY(), vel.getZ(), range, doppler);
    }

    public List<Ephemeris> getEphemerisList() {
        return ephemerisList;
    }

    public double getLastSimTime() {
        return this.lastSimTime;
    }

    public Properties getProperties() {
        return prop;
    }

    public double getTimeStepSeconds() {
        return timeStepSeconds;
    }

    public void run() {
        computeAccess();
    }

}
	


