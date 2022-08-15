package satellite.tools.structures;

public class Ephemeris {

    private long time;
    private double posX;
    private double posY;
    private double posZ;
    private double velX;
    private double velY;
    private double velZ;
    private double latitude;
    private double longitude;
    private double azimuth;
    private double elevation;
    private double range;
    private double dopplerShift;
    private int indexFrom;
    private int indexTo;

    public Ephemeris() {

    }

    public Ephemeris(long time, double posX, double posY, double posZ, double velX, double velY, double velZ, double range, double dopplerShift) {
        this.time = time;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
        this.range = range;
        this.dopplerShift = dopplerShift;
    }

    public Ephemeris(long time, int indexFrom, int indexTo, double posX, double posY, double posZ, double velX, double velY, double velZ, double range, double dopplerShift) {
        this.indexFrom = indexFrom;
        this.indexTo = indexTo;
        this.time = time;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
        this.range = range;
        this.dopplerShift = dopplerShift;
    }

    public Ephemeris(long time, double posX, double posY, double posZ, double range, double dopplerShift) {
        this.time = time;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.range = range;
        this.dopplerShift = dopplerShift;
    }

    public Ephemeris(long time, double posX, double posY, double posZ) {
        this.time = time;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public Ephemeris(double azimuth, double elevation, double range) {
        this.azimuth = azimuth;
        this.elevation = elevation;
        this.range = range;
    }

    public Ephemeris(int objectIndex, double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.indexFrom = objectIndex;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setAssetsInContact(int indexFrom, int indexTo) {
        this.indexFrom = indexFrom;
        this.indexTo = indexTo;
    }

    public int getIndex() {
        return indexFrom;
    }

    public int getIndexFrom() {
        return indexFrom;
    }

    public int getIndexTo() {
        return indexTo;
    }

    public void setPos(double posX, double posY, double posZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

    public void setVel(double velX, double velY, double velZ) {
        this.velX = velX;
        this.velY = velY;
        this.velZ = velZ;
    }

    public void setAER(double azimuth, double elevation, double range) {
        this.azimuth = azimuth;
        this.elevation = elevation;
        this.range = range;
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public double getVelZ() {
        return velZ;
    }

    public void setVelZ(double velZ) {
        this.velZ = velZ;
    }

    public void setDopplerShift(double dopplerShift) {
        this.dopplerShift = dopplerShift;
    }

    public double getDopplerShift() {
        return dopplerShift;
    }

    public void setSSP(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return time + "," + posX + "," + posY + "," + posZ + "," + velX + ","
                + velY + "," + velZ + "," + range + "," + dopplerShift;
    }
}