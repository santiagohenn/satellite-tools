package tools.simulation.assets.entities;

import tools.simulation.assets.Asset;

public class Device extends Asset {

    private int bufferInBytes;
    private long lastTransmissionTime;
    private double blockedTime;
    private double transmitTime;

    public Device() {
        this.setName("DummyDevice");
    }

    public Device(double lat, double lon, double height) {
        this.setName("DummyDevice");
        this.setLat(lat);
        this.setLon(lon);
        this.setHeight(height);
    }

    public Device(int id, double lat, double lon, double height) {
        this.setId(id);
        this.setLat(lat);
        this.setLon(lon);
        this.setHeight(height);
    }

    public Device(int id, String name, double lat, double lon, double height) {
        this.setId(id);
        this.setName(name);
        this.setLat(lat);
        this.setLon(lon);
        this.setHeight(height);
    }

    public int getBufferInBytes() {
        return bufferInBytes;
    }

    public void setBufferInBytes(int bufferInBytes) {
        this.bufferInBytes = bufferInBytes;
    }

    public void decreaseBufferInBytes(int transmittedPackages) {
        this.bufferInBytes -= transmittedPackages;
    }

    public long getLastTransmissionTime() {
        return lastTransmissionTime;
    }

    public void setLastTransmissionTime(long lastTransmissionTime) {
        this.lastTransmissionTime = lastTransmissionTime;
    }

    public double getBlockedTime() {
        return blockedTime;
    }

    public void setBlockedTime(double blockedTime) {
        this.blockedTime = blockedTime;
    }

    public double getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(double transmitTime) {
        this.transmitTime = transmitTime;
    }
}
