package simulation.structures;

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
    private double dragCoefficient;
    private double meanMotionFirstDerivative;
    private double meanMotionSecondDerivative;

    public OrbitalElements() {

    }

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

    public double[] asArray() {
        double[] elementsAsArray = new double[6];
        elementsAsArray[0] = this.getSemiMajorAxis();
        elementsAsArray[1] = this.getEccentricity();
        elementsAsArray[2] = this.getInclination();
        elementsAsArray[3] = this.getRightAscension();
        elementsAsArray[4] = this.getArgOfPerigee();
        elementsAsArray[5] = this.getAnomaly();
        return elementsAsArray;
    }

    public void fromArray(double[] elementsAsArray) {
        this.semiMajorAxis = elementsAsArray[0];
        this.eccentricity = elementsAsArray[1];
        this.inclination = elementsAsArray[2];
        this.rightAscension = elementsAsArray[3];
        this.argOfPerigee = elementsAsArray[4];
        this.anomaly = elementsAsArray[5];
        this.period = elementsAsArray[6];
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

}
