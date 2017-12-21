import lombok.Getter;
import lombok.Setter;

public class Drone {

    @Getter
    private final int id;

    @Getter
    private final int port;

    @Getter
    @Setter
    private double longitude, latitude;

    @Getter
    @Setter
    private float pitch, roll, yaw;

    @Getter
    private String cameraLink = "cam1";

    public Drone(int id, int port) {
        this.id = id;
        this.port = port;
        this.longitude = 0;
        this.latitude = 0;
        this.pitch = 0;
        this.roll = 0;
        this.yaw = 0;
    }

    void copyStatsFromDrone(Drone drone) {
        this.longitude = drone.longitude;
        this.latitude = drone.latitude;

        this.pitch = drone.pitch;
        this.roll = drone.roll;
        this.yaw = drone.yaw;
    }

}
