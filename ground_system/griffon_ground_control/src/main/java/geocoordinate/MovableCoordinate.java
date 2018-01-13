package geocoordinate;

import java.util.concurrent.locks.ReentrantLock;

public class MovableCoordinate extends BaseCoordinate {
    private static final double EARTH_RADIUS = 6367445;
    private ReentrantLock variableLock;

    public MovableCoordinate(double longitude, double latitude) {
        super(longitude,latitude);
        variableLock = new ReentrantLock();
    }

    public MovableCoordinate(GeoCoordinate coordinate) {
        this(coordinate.getLongitude(),coordinate.getLatitude());
    }

    public void set(double longitude, double latitude) {
        variableLock.lock();
        this.longitude = longitude;
        this.latitude = latitude;
        variableLock.unlock();
    }

    public void set(GeoCoordinate coordinate) {
        variableLock.lock();
        this.longitude = coordinate.getLongitude();
        this.latitude = coordinate.getLatitude();
        variableLock.unlock();
    }

    public void offsetDeg(double longitude, double latitude) {
        variableLock.lock();
        this.longitude += longitude;
        this.latitude += latitude;
        variableLock.unlock();
    }

    public void offsetMeters(double metersNorth, double metersEast) {
        double dLat = metersEast/EARTH_RADIUS;
        double latOffset = dLat * 180/Math.PI;


        // TODO: 13/01/2018 Fix high error at large longitudes
        double dLong = metersNorth/(EARTH_RADIUS*Math.cos(Math.PI*latitude/180));
        double longOffset = dLong * 180/Math.PI;

        variableLock.lock();
        latitude += latOffset;
        longitude += longOffset;
        variableLock.unlock();
    }

    @Override
    public double getLongitude() {
        variableLock.lock();
        double value = super.getLongitude();
        variableLock.unlock();
        return value;
    }

    @Override
    public double getLatitude() {
        variableLock.lock();
        double value = super.getLatitude();
        variableLock.unlock();
        return value;
    }

    @Override
    public GeoCoordinate clone() {
        return new MovableCoordinate(super.clone());
    }
}
