package geocoordinate;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Represents longitude and latitude that are saved in decimal degrees.
 * Longitude: vertical, Latitude: horizontal
 */
public class BaseCoordinate implements GeoCoordinate{

    @Getter
    double longitude;
    @Getter
    double latitude;

    public BaseCoordinate(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public BaseCoordinate(GeoCoordinate coordinate) {
        this.longitude = coordinate.getLongitude();
        this.latitude = coordinate.getLatitude();
    }

    @Override
    public GeoCoordinate clone() {
        return new BaseCoordinate(longitude,latitude);
    }
}