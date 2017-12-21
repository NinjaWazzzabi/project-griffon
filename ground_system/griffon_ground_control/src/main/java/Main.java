import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;


public class Main {
    private List<Drone> droneList = new ArrayList<>();
    private Gson gson = new Gson();
    private Drone specDrone;

    Main() {
        specDrone = new Drone(25,9001);
        droneList.add(specDrone);
        DroneLink droneLink = new DroneLink(specDrone);
        droneLink.setOnInputReceived(s -> {
            Drone mockDrone = gson.fromJson(s, Drone.class);
            specDrone.copyStatsFromDrone(mockDrone);
        });
        droneLink.start();

        initWeb();
    }


    private void initWeb() {
        port(4545);
        get("/drones",(req,res) -> getDroneListJson());
        for (Drone drone : droneList) {
            get("/drone" + drone.getId() ,(req,res) -> gson.toJson(drone));
        }
    }

    private String getDroneListJson() {
        JsonArray jsonArray = new JsonArray();

        droneList.forEach(drone -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id",drone.getId());
            jsonArray.add(jsonObject);
        });

        return jsonArray.toString();
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }
}
