package simulation.assets;

/**
 * Asset is the abstract class for objects that may participate in
 * a simulation and share parameters within the scenario. These are:
 * <ul>
 * <li> ID
 * <li> Position (Either in cartesian or polar coordinates)
 * <li> Visibility Threshold
 * </ul>
 * <p>
 * The ID may be a Name or an Integer value. Position is determined
 * in either the Geographic Coordinate System (GCS) or an X,Y,Z coordinate
 * System. Visibility Threshold is a parameter that restricts visibility
 * to or from a position in the 3D space, useful for several approximations.
 * <p>
 */
public abstract class Asset {

    private int id;
    private String name;
    private double lat;
    private double lon;
    private double height;
    private double xPos;
    private double yPos;
    private double zPos;
    private double visibilityTH = 0;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParams(double lat, double lon, double height) {
        this.lat = lat;
        this.lon = lon;
        this.height = height;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLatRad(){
        return Math.toRadians(this.lat);
    }

    public double getLonRad(){
        return Math.toRadians(this.lon);
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getTH(){
        return this.visibilityTH;
    }

    public double getTHRad(){
        return Math.toRadians(this.visibilityTH);
    }

    public void setVisibilityTH(double visibilityTH) {
        this.visibilityTH = visibilityTH;
    }

    public double getXPos() {
        return xPos;
    }

    public void setXPos(double xPos) {
        this.xPos = xPos;
    }

    public double getYPos() {
        return yPos;
    }

    public void setYPos(double yPos) {
        this.yPos = yPos;
    }

    public double getZPos() {
        return zPos;
    }

    public void setZPos(double zPos) {
        this.zPos = zPos;
    }

    public void setPos(double xPos, double yPos, double zPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
    }

    public void setLLA(double lat, double lon, double alt) {
        this.lat = lat;
        this.lon = lon;
        this.height = alt;
    }

}
