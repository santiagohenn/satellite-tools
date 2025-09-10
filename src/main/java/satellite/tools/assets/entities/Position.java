package satellite.tools.assets.entities;

import satellite.tools.assets.Asset;

public class Position extends Asset {

    public Position() {
        this.setName("DefaultDevice");
    }

    public Position(double lat, double lon, double height) {
        this.setName("DefaultDevice");
        this.setLat(lat);
        this.setLon(lon);
        this.setHeight(height);
    }

    public Position(int id, double lat, double lon, double height) {
        this.setId(id);
        this.setLat(lat);
        this.setLon(lon);
        this.setHeight(height);
    }

    public Position(int id, String name, double lat, double lon, double height) {
        this.setId(id);
        this.setName(name);
        this.setLat(lat);
        this.setLon(lon);
        this.setHeight(height);
    }

}
