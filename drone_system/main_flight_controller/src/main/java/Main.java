import com.google.gson.Gson;

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

        new Thread(() -> {
            while (true) {
                sendUpdatesToBase();
                mockDroneUpdate(drone);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void sendUpdatesToBase() {
        server.sendData(gson.toJson(drone));
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

    private float pitch = 0;
    private float roll = 0;
    private float yaw = 0;
    private float x = 0;
    private void mockDroneUpdate(Drone drone) {
        pitch = (float) Math.sin(x) * 45;
        roll = (float) Math.sin(x) * 45;
        yaw = (float) Math.sin(x) * 45;

        x += 0.1;

        drone.setPitch(pitch);
        drone.setRoll(roll);
        drone.setYaw(yaw);
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }
}
