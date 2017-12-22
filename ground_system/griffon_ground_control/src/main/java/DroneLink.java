import lombok.Setter;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.function.Consumer;

public class DroneLink {
    private static final String DRONE_IP = "192.168.0.10";

    private StreamReader streamReader;
    private PrintStream outputStream;
    private Socket droneLink;
    private DataInputStream inputStream;
    private Drone drone;

    private StringBuilder input;

    @Setter
    private Consumer<String> onInputReceived;

    public DroneLink(Drone drone) {
        input = new StringBuilder();
        this.drone = drone;
    }

    public void start() {
        new Thread(this::reconnectLink).start();
    }

    public void sendData(String data) {
        if (outputStream != null) {
            outputStream.print(data);
            outputStream.flush();
            outputStream.write((byte) 2);
            outputStream.flush();
        }
    }
    public boolean isConnected() {
        return droneLink != null;
    }


    private void reconnectLink() {
        input.delete(0,input.length());
        terminateLink();
        boolean connected = false;
        while (!connected) {
            connected = true;
            try {
                engageLink();
            } catch (IOException e) {
                connected = false;
                System.out.println("WARNING: " + e.getMessage());
            }
        }
    }
    private void engageLink() throws IOException {
        // TODO: 21/12/2017 See what happens if it can't connect
        droneLink = new Socket(DRONE_IP,drone.getPort());
        inputStream = new DataInputStream(droneLink.getInputStream());
        outputStream = new PrintStream(droneLink.getOutputStream());

        streamReader = new StreamReader(inputStream);
        streamReader.setOnInputRead(this::inputReceived);
        streamReader.setOnStreamClosed(this::reconnectLink);
        streamReader.start();
    }
    private void inputReceived(String data) {
        synchronized (input) {
            char[] chars = data.toCharArray();
            for (char aChar : chars) {
                if (aChar == 002) {
                    String finalData = input.toString();
                    input.delete(0,input.length());
                    onInputReceived.accept(finalData);
                } else {
                    input.append(aChar);
                }
            }
        }
    }
    private void terminateLink() {
        try {
            if (droneLink != null) {
                droneLink.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        droneLink = null;
        inputStream = null;
        outputStream = null;
    }
}
