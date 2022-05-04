package satellite.tools.assets.entities;

import satellite.tools.structures.OrbitalElements;
import satellite.tools.assets.Asset;

import java.util.Locale;

/**
 * Satellite is a class that represents a Satellite in space. Parameters
 * of the class represent everything needed to reference a single satellite
 * in the sky, both in terms of common descriptors and/or orbital elements.
 */
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

    /**
     * Default constructor
     * */
    public Satellite() {

    }

    /**
     * Class constructor specifying the orbital elements of the satellite
     * */
    public Satellite(OrbitalElements elements) {
        this.name = "unknown";
        this.elements = elements;
    }
    /**
     * Class constructor specifying the orbital elements of the satellite and its ID
     * */
    public Satellite(int id, OrbitalElements elements) {
        this.setId(id);
        this.elements = elements;
    }

    /**
     * Class constructor specifying the orbital elements of the satellite, its ID and its Number
     * */
    public Satellite(int id, int satelliteNumber, OrbitalElements elements) {
        this(id, satelliteNumber, 'a', elements);
    }

    /**
     * Class constructor specifying the orbital elements of the satellite, its ID, Number, classification and its Number
     * */
    public Satellite(int id, int satelliteNumber, char satelliteClassification, OrbitalElements elements) {
        this.setId(id);
        this.satelliteNumber = satelliteNumber;
        this.satelliteClassification = satelliteClassification;
        this.elements = elements;
    }

    /**
     * Class constructor specifying the orbital elements of the satellite, its ID, Number, launch piece,
     * classification and its Number
     * */
    public Satellite(int id, int satelliteNumber, int launchNumber, String launchPiece, char satelliteClassification, OrbitalElements elements) {
        this.setId(id);
        this.satelliteNumber = satelliteNumber;
        this.launchNumber = launchNumber;
        this.launchPiece = launchPiece;
        this.satelliteClassification = satelliteClassification;
        this.elements = elements;
    }

    /**
     * Class constructor from NORAD's Two Line Elements
     * */
    public Satellite(String tle1, String tle2) {
        this.name = tle1.substring(2, 9);
        setTLE(tle1, tle2);
    }

    /**
     * Class constructor from NORAD's Two Line Elements and an internal use ID.
     * */
    public Satellite(int id, String tle1, String tle2) {
        this.setId(id);
        setTLE(tle1, tle2);
    }

    /**
     * Class constructor specifying ID, timestamp in YYYY-MM-DDTHH:MM:SS.sss format and each orbital element
     * as a double value
     * */
    public Satellite(int id, String timestamp, double semiMajorAxis, double eccentricity, double inclination, double rightAscension
            , double argOfPerigee, double anomaly) {
        this(id, new OrbitalElements(timestamp, semiMajorAxis, eccentricity, inclination, rightAscension, argOfPerigee, anomaly));
    }

    /**
     * Class constructor specifying ID, timestamp in YYYY-MM-DDTHH:MM:SS.sss format and each orbital element and mean
     * motion first and second derivatives as a double value
     * */
    public Satellite(String timestamp, double semiMajorAxis, double eccentricity, double inclination, double rightAscension
            , double argOfPerigee, double anomaly, double dragCoefficient, double meanMotionFirstDerivative, double meanMotionSecondDerivative) {
        this(new OrbitalElements(timestamp, semiMajorAxis, eccentricity, inclination, rightAscension, argOfPerigee,
                anomaly, dragCoefficient, meanMotionFirstDerivative, meanMotionSecondDerivative));
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

    /**
     * Returns an Orbital Element based on the name passed in the parameter
     *
     * @param element a String depicting the name of the orbital element to be retrieved, at the orbital elements
     *                configured time, as a double value.
     * @return the double value for the chosen element
     */
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

