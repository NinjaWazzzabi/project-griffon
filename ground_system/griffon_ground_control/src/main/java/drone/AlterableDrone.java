package drone;

import com.google.gson.Gson;
import geocoordinate.GeoCoordinate;

public class AlterableDrone extends BaseDrone {

    AlterableDrone(int id, Gson gson) {
        super(id, gson);
    }

    public void setCoordinate(GeoCoordinate coordinate) {
        this.coordinate = coordinate.clone();
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
    public void setRoll(float roll) {
        this.roll = roll;
    }
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
