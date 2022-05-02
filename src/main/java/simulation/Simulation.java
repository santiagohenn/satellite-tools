package simulation;

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
import simulation.assets.Asset;
import simulation.assets.objects.Device;
import simulation.assets.objects.Satellite;
import simulation.structures.Ephemeris;
import simulation.structures.Interval;
import simulation.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Simulation condenses the main entry class for the software. It propagates orbits and output results based on the
 * configured Satellite object and parameters such as date and reference frames.
 */
public class Simulation implements Runnable {

    private List<Interval> intervalList;
    private List<Ephemeris> ephemerisList = new ArrayList<>();
    private String time1 = "2020-03-20T11:00:00.000";
    private String time2 = "2020-03-30T11:00:00.000";

    private Satellite satellite;
    private Device device;

    private double step = 60D;
    private double th;
    private Frame inertialFrame;
    private BodyShape earth;
    private GeodeticPoint geodeticPoint;
    private TopocentricFrame topocentricFrame;
    private TLEPropagator tlePropagator;
    private final double TH_DETECTION = 0.001; // 1 ms default
    private Date contact = new Date();
    private double lastSimTime = 0;

    public Simulation() {
        init();
    }

    /**
     * A Class constructor that gets the Position and Velocity coordinates for a Satellite, with its cartesian coordinates
     * in respect to the IERS 2010 Earth's frame reference
     */
    public Simulation(Satellite satellite) {
        init();
        setSatellite(satellite);
    }

    public Simulation(String timeStart, String timeEnd, Device device, Satellite satellite, double step, double th) {
        init();
        this.time1 = timeStart;
        this.time2 = timeEnd;
        setSatellite(satellite);
        setDevice(device);
        this.step = step;
        this.th = Math.toRadians(th);
    }

    public Simulation(String timeStart, String timeEnd, double step, double th) {
        init();
        this.time1 = timeStart;
        this.time2 = timeEnd;
        this.step = step;
        this.th = Math.toRadians(th);
    }

    public Simulation(long timeStart, long timeEnd, double step, double th) {
        init();
        this.time1 = Utils.unix2stamp(timeStart);
        this.time2 = Utils.unix2stamp(timeEnd);
        this.step = step;
        this.th = Math.toRadians(th);
    }

    private void init() {

        // configure Orekit
        var orekitData = new File("src/main/resources/orekit-data");
        if (!orekitData.exists()) {
            System.err.format(Locale.US, "Failed to find %s folder%n",
                    orekitData.getAbsolutePath());
            System.exit(1);
        }

        DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
        manager.addProvider(new DirectoryCrawler(orekitData));

        // configure Earth frame:
        Frame earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
        this.earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                Constants.WGS84_EARTH_FLATTENING,
                earthFrame);
        this.inertialFrame = FramesFactory.getEME2000();
        this.intervalList = new ArrayList<>();

    }

    public void setParams(String timeStart, String timeEnd, double step, double th) {
        this.time1 = timeStart;
        this.time2 = timeEnd;
        this.step = step;
        this.th = Math.toRadians(th);
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

    public Device getDevice() {
        return device;
    }

    public int getDeviceId() {
        return this.device.getId();
    }

    public int getSatelliteId() {
        return this.satellite.getId();
    }

    public List<Interval> getIntervals() {
        return intervalList;
    }

    public void setAssets(Device device, Satellite satellite) {
        setDevice(device);
        setSatellite(satellite);
    }

    public void setAsset(Asset asset) {
        if (asset.getClass().isAssignableFrom(Device.class)) {
            setDevice((Device) asset);
        } else if (asset.getClass().isAssignableFrom(Satellite.class)) {
            setSatellite((Satellite) asset);
        }
    }

    public void setDevice(Device device) {
        this.device = device;
        this.geodeticPoint = new GeodeticPoint(device.getLatRad(), device.getLonRad(), device.getHeight());
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

    @SuppressWarnings("squid:S2184")
    public void computeAccess() {

        long t0 = System.currentTimeMillis();

        intervalList.clear();

        contact.setTime(Utils.stamp2unix(time1));
        EventDetector elevDetector = new ElevationDetector(step, TH_DETECTION, topocentricFrame).
                withConstantElevation(th).
                withHandler(
                        (s, detector, increasing) -> {
                            addInterval(s, detector, increasing);
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

    private void addInterval(SpacecraftState s, ElevationDetector detector, boolean dir) {
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

    // * Generate TEMEOfDate Position - Velocity vectors * //
    public void computePVD() {
        propagateAndComputePVD(Utils.stamp2AD(time1), Utils.stamp2AD(time2), this.step);
    }

    public void computePVDBetween(long startTime, long endTime) {
        computePVDBetween(startTime, endTime, this.step);
    }

    public void computePVDBetween(long startTime, long endTime, double stepInSeconds) {
        computePVDBetween(Utils.unix2stamp(startTime), Utils.unix2stamp(endTime), stepInSeconds);   // FIXME needs rework
    }

    public void computePVDBetween(String startTime, String endTime) {
        computePVDBetween(startTime, endTime, this.step);
    }

    public void computePVDBetween(String startTime, String endTime, double stepInSeconds) {
        propagateAndComputePVD(Utils.stamp2AD(startTime), Utils.stamp2AD(endTime), stepInSeconds);
    }

    public Ephemeris computePVDAt(String timestamp) {
        return computePVDAt(Utils.stamp2unix(timestamp));
    }

    public Ephemeris computePVDAt(long timestamp) {
        return computePVDAt(Utils.unix2stamp(timestamp), this.step);
    }

    public Ephemeris computePVDAt(long timestamp, double step) {
        return computePVDAt(Utils.unix2stamp(timestamp), step);
    }

    public Ephemeris computePVDAt(String timestamp, double step) {
        return computePVDAt(Utils.stamp2AD(timestamp), step);
    }

    public Ephemeris computePVDAt(AbsoluteDate fixedDate, double step) {
        PVCoordinates pvInert = tlePropagator.propagate(fixedDate).getPVCoordinates();
        var pvCoordinates = inertialFrame.getTransformTo(topocentricFrame, fixedDate).transformPVCoordinates(pvInert);
        return toEphemeris(fixedDate, pvCoordinates);
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

    public void computeSSPAt(AbsoluteDate absoluteDate) {

        PVCoordinates pvCoordinates = tlePropagator.propagate(absoluteDate).getPVCoordinates();
        TimeStampedPVCoordinates timeStampedPVCoordinates = new TimeStampedPVCoordinates(absoluteDate, pvCoordinates);

        Frame bodyFrame = earth.getBodyFrame();
        Transform t = inertialFrame.getTransformTo(bodyFrame, timeStampedPVCoordinates.getDate());
        timeStampedPVCoordinates = earth.projectToGround(t.transformPVCoordinates(timeStampedPVCoordinates), inertialFrame);

        double alpha = timeStampedPVCoordinates.getPosition().getAlpha();
        double delta =  timeStampedPVCoordinates.getPosition().getDelta();

    }

    public Ephemeris computeSSPAndGetEphemeris(AbsoluteDate absoluteDate) {

        PVCoordinates pvCoordinates = tlePropagator.propagate(absoluteDate).getPVCoordinates();
        TimeStampedPVCoordinates timeStampedPVCoordinates = new TimeStampedPVCoordinates(absoluteDate, pvCoordinates);

        Frame bodyFrame = earth.getBodyFrame();
        Transform t = inertialFrame.getTransformTo(bodyFrame, timeStampedPVCoordinates.getDate());
        timeStampedPVCoordinates = earth.projectToGround(t.transformPVCoordinates(timeStampedPVCoordinates), inertialFrame);

        double alpha = timeStampedPVCoordinates.getPosition().getAlpha();
        double delta =  timeStampedPVCoordinates.getPosition().getDelta();

        return new Ephemeris(this.satellite.getId(), Math.toDegrees(delta), Math.toDegrees(alpha));

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

    private void addEphemeris(Date date, PVCoordinates pvDevice) {

        // Get the satellite's position and velocity in reference to the station
        Vector3D pos = pvDevice.getPosition();
        Vector3D vel = pvDevice.getVelocity();

        // Calculate Range
        double range = pvDevice.getPosition().getNorm();

        // Calculate the doppler signal
        double doppler = Vector3D.dotProduct(pvDevice.getPosition(), pvDevice.getVelocity()) / range;

        var ephemeris = new Ephemeris(date.getTime(), this.getDeviceId(), this.getSatelliteId(), pos.getX(), pos.getY(),
                pos.getZ(), vel.getX(), vel.getY(), vel.getZ(), range, doppler);
        ephemerisList.add(ephemeris);
    }

    public List<Ephemeris> getEphemerisList() {
        return ephemerisList;
    }

    public double getLastSimTime() {
        return this.lastSimTime;
    }

    @Override
    public void run() {
        computeAccess();
    }

}
	


