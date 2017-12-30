import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static spark.Spark.*;


public class Main {
    private List<LinkedDrone> droneList = new ArrayList<>();
    private Gson gson = new Gson();
    private LinkedDrone specDrone;

    Main() {
        specDrone = new LinkedDrone(25,gson, new NetworkLink("192.168.0.2", 9001));
        droneList.add(specDrone);


        initWeb();
    }


    private void initWeb() {
        port(4545);
        get("/drones",(req,res) -> getDroneListJson());
        for (LinkedDrone drone : droneList) {
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
