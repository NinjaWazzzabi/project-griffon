package drone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Reader for handling the input stream of characters
 */
class StreamReader extends Thread {

    private InputStream inputStream;
    private Consumer<String> onInputRead;
    private Runnable onStreamClosed;

    StreamReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }


    @Override
    public void run() {
        try {
            readCharLoop();
        } catch (IOException e) {
            System.out.println("WARNING: " + e.getMessage());
            System.out.println("WARNING: " + "connection lost!");
        }

        if (onStreamClosed != null) {
            onStreamClosed.run();
        }
    }

    void setOnInputRead(Consumer<String> consumer) {
        this.onInputRead = consumer;
    }

    void setOnStreamClosed(Runnable runnable) {
        this.onStreamClosed = runnable;
    }

    private void readCharLoop() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        int receivedValue = 0;
        while (receivedValue != -1) {
            receivedValue = reader.read();
            if (receivedValue != 0) {
                onInputRead.accept(String.valueOf(((char)receivedValue)));
            }
        }
    }
}
