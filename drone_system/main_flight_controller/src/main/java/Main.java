import com.google.gson.Gson;
import drone.AlterableDrone;
import sensors.Ahrs;

import java.io.IOException;

public class Main {
    private static int PORT_NUM = 9001;
    
    private AlterableDrone drone;
    private Server server;
    private Gson gson;

    public Main() throws IOException {
        gson = new Gson();
        drone = new AlterableDrone(25,gson);

        server = new Server(PORT_NUM);
        server.setOnInputReceived(this::inputReceived);

        Ahrs ahrs = new Ahrs();
        ahrs.setOnUpdatedValues(() -> {
            drone.setPitch(ahrs.getPitch());
            drone.setRoll(ahrs.getRoll());
            drone.setYaw(ahrs.getYaw());
        });

        new Thread(() -> {
            while (true) {
                server.sendData(drone.getJson());
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
            // TODO: 17/01/2018 Update drone task
        } catch (IllegalStateException ise) {
            ise.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Main();
    }
}
