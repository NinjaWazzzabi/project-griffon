import lombok.Setter;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class DroneServer {
    private static final byte END_OF_MESSAGE = (byte) 2;

    private final int PORT;
    private final StringBuilder input;
    private ServerSocket serverService;
    private Socket clientSocket = null;
    private PrintStream outputStream;

    @Setter
    private Consumer<String> onInputReceived;

    DroneServer(Drone drone) throws IOException {
        input = new StringBuilder();
        PORT = drone.getPort();
        serverService = new ServerSocket(PORT);

        new Thread(this::autoFindConnection).start();
    }

    private boolean isConnected() {
        return clientSocket != null;
    }

    void sendData(String data) {
        if (isConnected() && outputStream != null) {
            outputStream.print(data);
            outputStream.flush();
            outputStream.write((byte) 2);
            outputStream.flush();
        }
    }

    private void autoFindConnection() {
        // TODO: 21/12/2017 Find a nicer way to search for connections async
        try {
            clientSocket = serverService.accept();
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            StreamReader reader = new StreamReader(input);
            reader.setOnInputRead(this::inputReceived);
            outputStream = new PrintStream(clientSocket.getOutputStream());
            reader.setOnStreamClosed(() -> {
                try {
                    clientSocket.close();
                    outputStream.close();
                    clientSocket = null;
                    outputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.input.delete(0,this.input.length());
                autoFindConnection();
            });
            reader.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void inputReceived(String text) {
        synchronized (input) {
            char[] chars = text.toCharArray();
            for (char aChar : chars) {
                if (aChar == END_OF_MESSAGE) {
                    String data = input.toString();
                    input.delete(0,input.length());
                    onInputReceived.accept(data);
                } else {
                    input.append(aChar);
                }
            }
        }
    }
}