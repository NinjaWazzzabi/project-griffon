package geocoordinate;

public interface GeoCoordinate {

    double getLongitude();
    double getLatitude();

    GeoCoordinate clone();
}
