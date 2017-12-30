package drone;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

class BaseDrone implements Drone {

    @Getter
    protected final int id;
    protected final Gson gson;

    @Getter
    @Setter
    protected double longitude, latitude;

    @Getter
    @Setter
    protected float pitch, roll, yaw;

    BaseDrone(int id, Gson gson) {
        this.id = id;
        this.gson = gson;
    }

    void copyStatsFromDrone(Drone drone) {
        this.longitude = drone.getLongitude();
        this.latitude = drone.getLatitude();

        this.pitch = drone.getPitch();
        this.roll = drone.getRoll();
        this.yaw = drone.getYaw();
    }

    @Override
    public String getJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id",id);
        jsonObject.addProperty("longitude",longitude);
        jsonObject.addProperty("latitude",latitude);
        jsonObject.addProperty("pitch",pitch);
        jsonObject.addProperty("roll",roll);
        jsonObject.addProperty("yaw",yaw);
        return jsonObject.toString();
    }
}
