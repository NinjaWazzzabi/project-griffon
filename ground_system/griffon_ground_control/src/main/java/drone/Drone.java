package drone;

import geocoordinate.GeoCoordinate;

public interface Drone {
    int getId();

    GeoCoordinate getCoordinate();

    float getPitch();
    float getRoll();
    float getYaw();

    String getJson();
}
