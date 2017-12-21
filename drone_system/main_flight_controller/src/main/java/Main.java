import com.google.gson.Gson;
import sensors.Ahrs;

import java.io.IOException;

public class Main {

    private Drone drone;
    private DroneServer server;
    private Gson gson;

    public Main() throws IOException {
        gson = new Gson();
        drone = new Drone(25,9001);

        server = new DroneServer(drone);
        server.setOnInputReceived(this::inputReceived);

        Ahrs ahrs = new Ahrs();
        ahrs.setOnUpdatedValues(() -> {
            drone.setPitch(ahrs.getPitch());
            drone.setRoll(ahrs.getRoll());
            drone.setYaw(ahrs.getYaw());
        });

        new Thread(() -> {
            while (true) {
                server.sendData(gson.toJson(drone));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void inputReceived(String data) {
        try {
            Drone mockDrone = gson.fromJson(data, Drone.class);
            // TODO: 21/12/2017 This will copy old and false sensor data as well, it should only copy command data
            this.drone.copyStatsFromDrone(mockDrone);
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }
}
