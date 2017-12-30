import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

public class LinkedDrone extends BaseDrone {

    private ConnectiveLink droneLink;

    public LinkedDrone(int id, Gson gson, ConnectiveLink droneLink) {
        super(id,gson);
        this.longitude = 0;
        this.latitude = 0;
        this.pitch = 0;
        this.roll = 0;
        this.yaw = 0;

        this.droneLink = droneLink;
        droneLink.setOnInputReceived(s -> {
            Drone mockDrone = gson.fromJson(s, BaseDrone.class);
            copyStatsFromDrone(mockDrone);
        });
        droneLink.startLink();
    }
}
