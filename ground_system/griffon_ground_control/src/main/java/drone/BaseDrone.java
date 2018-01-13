package drone;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import geocoordinate.BaseCoordinate;
import geocoordinate.GeoCoordinate;
import lombok.Getter;

class BaseDrone implements Drone {

    @Getter
    protected final int id;
    protected final Gson gson;

    @Getter
    protected GeoCoordinate coordinate;

    @Getter
    protected float pitch, roll, yaw;

    BaseDrone(int id, Gson gson, GeoCoordinate coordinate) {
        this.id = id;
        this.gson = gson;
        this.coordinate = coordinate.clone();
    }

    BaseDrone(int id, Gson gson) {
        this(id,gson, new BaseCoordinate(0,0));
    }

    void copyStatsFromDrone(Drone drone) {
        this.coordinate = drone.getCoordinate().clone();

        this.pitch = drone.getPitch();
        this.roll = drone.getRoll();
        this.yaw = drone.getYaw();
    }

    @Override
    public String getJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id",id);
        jsonObject.addProperty("longitude",coordinate.getLongitude());
        jsonObject.addProperty("latitude",coordinate.getLatitude());
        jsonObject.addProperty("pitch",pitch);
        jsonObject.addProperty("roll",roll);
        jsonObject.addProperty("yaw",yaw);
        return jsonObject.toString();
    }
}
