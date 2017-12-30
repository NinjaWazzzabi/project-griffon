package drone;

public interface Drone {
    int getId();

    double getLongitude();
    double getLatitude();

    float getPitch();
    float getRoll();
    float getYaw();

    String getJson();
}
