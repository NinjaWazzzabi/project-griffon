import lombok.Setter;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.function.Consumer;

public class NetworkLink implements ConnectiveLink{
    private static final byte END_BYTE = (byte) 4; // From ASCII EOT (End Of Transmission)

    private final String ipAddress;
    private final int port;

    private StreamReader streamReader;
    private PrintStream outputStream;
    private Socket droneLink;
    private DataInputStream inputStream;

    private StringBuilder input;

    @Setter
    private Consumer<String> onInputReceived;
    private Thread linkStartThread;

    public NetworkLink(String ipAddress,int port) {
        this.port = port;
        this.ipAddress = ipAddress;
        input = new StringBuilder();
    }

    @Override
    public void startLink() {
        linkStartThread = new Thread(this::reconnectLink);
        linkStartThread.start();
    }

    public void sendData(String data) {
        if (outputStream != null) {
            outputStream.print(data);
            outputStream.flush();
            outputStream.write(END_BYTE);
            outputStream.flush();
        }
    }

    public boolean isConnected() {
        return droneLink != null && droneLink.isConnected();
    }

    private boolean reconnectLink() {
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

            if (Thread.interrupted()) {
                return false;
            }
        }
        return true;
    }
    private void engageLink() throws IOException {
        // TODO: 21/12/2017 See what happens if it can't connect
        droneLink = new Socket(ipAddress, port);
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
                if (aChar == END_BYTE) {
                    String finalData = input.toString();
                    input.delete(0,input.length());
                    onInputReceived.accept(finalData);
                } else {
                    input.append(aChar);
                }
            }
        }
    }

    public void terminateLink() {
        if (linkStartThread != null && !linkStartThread.isAlive()) {
            linkStartThread.interrupt();
            try {
                linkStartThread.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

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
