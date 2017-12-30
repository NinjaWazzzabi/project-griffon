package drone;

import com.google.gson.Gson;

public class AlterableDrone extends BaseDrone {

    AlterableDrone(int id, Gson gson) {
        super(id, gson);
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
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
