import drone.Drone;
import drone.LinkedDrone;
import drone.MockLoiterDrone;
import drone.NetworkLink;
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
        //specDrone = new LinkedDrone(25,gson, new NetworkLink("192.168.0.2", 9001));
        MockLoiterDrone mockDrone = new MockLoiterDrone(25, gson);
        mockDrone.setLoiterCenter(0,0);
        mockDrone.setRadius(50);
        mockDrone.setSpeed(15);
        mockDrone.startLoiter();
        specDrone = mockDrone;
        droneList.add(specDrone);


        initWeb();
    }


    private void initWeb() {
        port(4545);
        get("/drones",(req,res) -> getDroneListJson());
        for (Drone drone : droneList) {
            get("/drone" + drone.getId() ,(req,res) -> drone.getJson());
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
