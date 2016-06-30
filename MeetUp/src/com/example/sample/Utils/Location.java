package com.example.sample.Utils;

/**
 * Created by Piotr on 29.05.2016.
 */
public class Location<Lat, Lon> {

    private final Lat latitude;
    private final Lon longitude;

    public Location(Lat latitude, Lon right) {
        this.latitude = latitude;
        this.longitude = right;
    }

    public Lat getLatitude() { return latitude; }
    public Lon getLongitude() { return longitude; }

    @Override
    public int hashCode() { return latitude.hashCode() ^ longitude.hashCode(); }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Location)) return false;
        Location pairo = (Location) o;
        return this.latitude.equals(pairo.getLatitude()) &&
                this.longitude.equals(pairo.getLongitude());
    }

}