package satellite.tools.structures;

/**
 * This Class stores a set of orbital elements and some derivatives at a specific moment in time
 */
public class OrbitalElements {

    private double semiMajorAxis;
    private double eccentricity;
    private double inclination;
    private double rightAscension;
    private double argOfPerigee;
    private double anomaly;
    private double period;
    private long unixTime;
    private String timestamp;
    private double dragCoefficient = 0;
    private double meanMotionFirstDerivative = 0;
    private double meanMotionSecondDerivative = 0;

    /**
     * The class default constructor.
     */
    public OrbitalElements() {

    }

    /**
     * The class constructor. Takes a timestamp in the YYYY-MM-DDTHH:MM:SS.sss format and each orbital element as a double
     * value in this order and format:
     * <ul>
     * <li> Semi Major Axis: The orbit's SemiMajor Axis in meters.
     * <li> Eccentricity: The orbit's eccentricity
     * <li> Inclination: The orbit's inclination in degrees
     * <li> Right ascension: The orbit's Right Ascension of the Ascending Node in degrees
     * <li> Argument of Perigee: The orbits Argument of Perigee in degrees
     * <li> Anomaly: The orbit's true or mean anomaly in degrees
     * </ul>
     */
    public OrbitalElements(String timestamp, double semiMajorAxis, double eccentricity, double inclination, double rightAscension
            , double argOfPerigee, double anomaly) {
        this.timestamp = timestamp;
        this.semiMajorAxis = semiMajorAxis;
        this.eccentricity = eccentricity;
        this.inclination = inclination;
        this.rightAscension = rightAscension;
        this.argOfPerigee = argOfPerigee;
        this.anomaly = anomaly;
    }

    /**
     * A class constructor that takes a timestamp in the YYYY-MM-DDTHH:MM:SS.sss format, each orbital element as a double
     * value, the drag coefficient and both the first and second derivative of the Mean Motion in this order and format:
     * <ul>
     * <li> Semi Major Axis: The orbit's SemiMajor Axis in meters.
     * <li> Eccentricity: The orbit's eccentricity
     * <li> Inclination: The orbit's inclination in degrees
     * <li> Right ascension: The orbit's Right Ascension of the Ascending Node in degrees
     * <li> Argument of Perigee: The orbits Argument of Perigee in degrees
     * <li> Anomaly: The orbit's true or mean anomaly in degrees
     * <li> Drag coefficient (B STAR) in radii^-1
     * <li> Mean motion first derivative in revs/day
     * <li> Mean motion second derivative in revs/day^3
     * </ul>
     *
     * @see <a href="http://spaceflight.nasa.gov/realdata/sightings/SSapplications/Post/JavaSSOP/SSOP_Help/tle_def.html">NORAD TLE</a>
     */
    public OrbitalElements(String timestamp, double semiMajorAxis, double eccentricity, double inclination, double rightAscension
            , double argOfPerigee, double anomaly, double dragCoefficient, double meanMotionFirstDerivative, double meanMotionSecondDerivative) {
        this.timestamp = timestamp;
        this.semiMajorAxis = semiMajorAxis;
        this.eccentricity = eccentricity;
        this.inclination = inclination;
        this.rightAscension = rightAscension;
        this.argOfPerigee = argOfPerigee;
        this.anomaly = anomaly;
        this.dragCoefficient = dragCoefficient;
        this.meanMotionFirstDerivative = meanMotionFirstDerivative;
        this.meanMotionSecondDerivative = meanMotionSecondDerivative;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    public void setSemiMajorAxis(double semiMajorAxis) {
        this.semiMajorAxis = semiMajorAxis;
    }

    public double getEccentricity() {
        return eccentricity;
    }

    public void setEccentricity(double eccentricity) {
        this.eccentricity = eccentricity;
    }

    public double getInclination() {
        return inclination;
    }

    public double getInclinationRads() {
        return Math.toRadians(inclination);
    }

    public void setInclination(double inclination) {
        this.inclination = inclination;
    }

    public double getRightAscension() {
        return rightAscension;
    }

    public double getRightAscensionRads() {
        return Math.toRadians(rightAscension);
    }

    public void setRightAscension(double rightAscension) {
        this.rightAscension = rightAscension;
    }

    public double getArgOfPerigee() {
        return argOfPerigee;
    }

    public double getArgOfPerigeeRads() {
        return Math.toRadians(argOfPerigee);
    }

    public void setArgOfPerigee(double argOfPerigee) {
        this.argOfPerigee = argOfPerigee;
    }

    public double getAnomaly() {
        return anomaly;
    }

    public double getAnomalyRads() {
        return Math.toRadians(anomaly);
    }

    public void setAnomaly(double anomaly) {
        this.anomaly = anomaly;
    }

    public double getPeriod() {
        return this.period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    public double getDragCoefficient() {
        return dragCoefficient;
    }

    public void setDragCoefficient(double dragCoefficient) {
        this.dragCoefficient = dragCoefficient;
    }

    public double getMeanMotionFirstDerivative() {
        return meanMotionFirstDerivative;
    }

    public void setMeanMotionFirstDerivative(double meanMotionFirstDerivative) {
        this.meanMotionFirstDerivative = meanMotionFirstDerivative;
    }

    public double getMeanMotionSecondDerivative() {
        return meanMotionSecondDerivative;
    }

    public void setMeanMotionSecondDerivative(double meanMotionSecondDerivative) {
        this.meanMotionSecondDerivative = meanMotionSecondDerivative;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String toString() {
        return timestamp + "," + semiMajorAxis + "," + eccentricity + "," + inclination + "," + rightAscension +
                "," + argOfPerigee + "," + anomaly;
    }
}
