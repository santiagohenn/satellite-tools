package simulation.assets.objects;

import simulation.assets.Asset;
import simulation.structures.OrbitalElements;

import java.util.Locale;

public class Satellite extends Asset {

    private String name = "";
    private String tle1 = "";
    private String tle2 = "";
    private OrbitalElements elements;
    private int satelliteNumber = 1;
    private char satelliteClassification = 'U';
    private String launchPiece = "A";
    private int launchNumber = 76;
    private int launchYear = 2018;
    private int revolutionNumber = 7836;
    private int elementsNumber = 999;

    public Satellite() {

    }

    public Satellite(OrbitalElements elements) {
        this.name = "unknown";
        this.elements = elements;
    }

    public Satellite(int id, OrbitalElements elements) {
        this.setId(id);
        this.elements = elements;
    }

    public Satellite(int id, int satelliteNumber, OrbitalElements elements) {
        this.setId(id);
        this.satelliteNumber = satelliteNumber;
        this.elements = elements;
    }

    public Satellite(int id, int satelliteNumber, char satelliteClassification, OrbitalElements elements) {
        this.setId(id);
        this.satelliteNumber = satelliteNumber;
        this.satelliteClassification = satelliteClassification;
        this.elements = elements;
    }

    public Satellite(int id, int satelliteNumber, int launchNumber, String launchPiece, char satelliteClassification, OrbitalElements elements) {
        this.setId(id);
        this.satelliteNumber = satelliteNumber;
        this.launchNumber = launchNumber;
        this.launchPiece = launchPiece;
        this.satelliteClassification = satelliteClassification;
        this.elements = elements;
    }

    public Satellite(String tle1, String tle2) {
        this.name = tle1.substring(2, 9);
        setTLE(tle1, tle2);
    }

    public Satellite(int id, String tle1, String tle2) {
        this.setId(id);
        setTLE(tle1, tle2);
    }

    public Satellite(int id, String timestamp, double semiMajorAxis, double eccentricity, double inclination, double rightAscension
            , double argOfPerigee, double anomaly) {
        this(id, new OrbitalElements(timestamp, semiMajorAxis, eccentricity, inclination, rightAscension, argOfPerigee, anomaly));
    }

    public Satellite(String timestamp, double semiMajorAxis, double eccentricity, double inclination, double rightAscension
            , double argOfPerigee, double anomaly) {
        this(new OrbitalElements(timestamp, semiMajorAxis, eccentricity, inclination, rightAscension, argOfPerigee, anomaly));
    }

    public Satellite(String timestamp, double semiMajorAxis, double eccentricity, double inclination, double rightAscension
            , double argOfPerigee, double anomaly, double dragCoefficient, double meanMotionFirstDerivative, double meanMotionSecondDerivative) {
        this(new OrbitalElements(timestamp, semiMajorAxis, eccentricity, inclination, rightAscension, argOfPerigee,
                anomaly, dragCoefficient, meanMotionFirstDerivative, meanMotionSecondDerivative));
    }

    public void setData(int satelliteNumber, int launchYear, int launchNumber, String launchPiece, char satelliteClassification, int revolutionNumber, int elementNumber) {
        setData(this.getId(), satelliteNumber, launchYear, launchNumber, launchPiece, satelliteClassification, revolutionNumber, elementNumber);
    }

    public void setData(int id, int satelliteNumber, int launchYear, int launchNumber, String launchPiece, char satelliteClassification, int revolutionNumber, int elementNumber) {
        this.setId(id);
        this.satelliteNumber = satelliteNumber;
        this.launchYear = launchYear;
        this.launchNumber = launchNumber;
        this.launchPiece = launchPiece;
        this.satelliteClassification = satelliteClassification;
        this.revolutionNumber = revolutionNumber;
        this.elementsNumber = elementNumber;
    }

    public int getSatelliteNumber() {
        return satelliteNumber;
    }

    public void setSatelliteNumber(int satelliteNumber) {
        this.satelliteNumber = satelliteNumber;
    }

    public char getSatelliteClassification() {
        return satelliteClassification;
    }

    public void setSatelliteClassification(char satelliteClassification) {
        this.satelliteClassification = satelliteClassification;
    }

    public int getLaunchNumber() {
        return launchNumber;
    }

    public void setLaunchNumber(int launchNumber) {
        this.launchNumber = launchNumber;
    }

    public int getLaunchYear() {
        return launchYear;
    }

    public void setLaunchYear(int launchYear) {
        this.launchYear = launchYear;
    }

    public String getLaunchPiece() {
        return launchPiece;
    }

    public void setLaunchPiece(String launchPiece) {
        this.launchPiece = launchPiece;
    }

    public int getRevolutionNumber() {
        return revolutionNumber;
    }

    public void setRevolutionNumber(int revolutionNumber) {
        this.revolutionNumber = revolutionNumber;
    }

    public int getElementsNumber() {
        return elementsNumber;
    }

    public void setElementsNumber(int elementsNumber) {
        this.elementsNumber = elementsNumber;
    }

    public void setElements (OrbitalElements elements) {
        this.elements = elements;
    }

    public OrbitalElements getElements() {
        return this.elements;
    }

    public double getElement(String element) {

        switch (element.toLowerCase(Locale.ROOT)) {
            case "sma": case "semmajaxis": case "a":
                return elements.getSemiMajorAxis();
            case "e": case "ecc": case "eccentricity":
                return elements.getEccentricity();
            case "i": case "inc": case "inclination":
                return elements.getInclination();
            case "raan": case "rightascension":
                return elements.getRightAscension();
            case "pa": case "argofperigee": case "aop":
                return elements.getArgOfPerigee();
            case "anomaly": case "v":
                return elements.getAnomaly();
            default:
                return 0;
        }

    }

    public void setTLE(String tle1, String tle2) {
        this.tle1 = tle1;
        this.tle2 = tle2;
    }

    public String getTLE1() {
        return this.tle1;
    }

    public String getTLE2() {
        return this.tle2;
    }



}

