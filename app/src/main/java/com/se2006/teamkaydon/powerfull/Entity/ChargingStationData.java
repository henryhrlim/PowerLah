package com.se2006.teamkaydon.powerfull.Entity;

/**
 * Charging Station entity. Provides get and set methods.
 */
public class ChargingStationData {
    public int index;
    public String name;
    public String info;
    public int zip;
    public double latitude;
    public double longitude;
    public int chargers;

    public ChargingStationData() { }

    public ChargingStationData(int index, String name, String info, int zip, double latitude, double longitude, int chargers) {
        this.index = index;
        this.name = name;
        this.info = info;
        this.zip = zip;
        this.latitude = latitude;
        this.longitude = longitude;
        this.chargers = chargers;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getChargers() {
        return chargers;
    }

    public void setChargers(int chargers) {
        this.chargers = chargers;
    }
}
