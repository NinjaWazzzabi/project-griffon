package drone;

import java.util.function.Consumer;

public interface ConnectiveLink {

    void setOnInputReceived(Consumer<String> consumer);
    void sendData(String data);

    void startLink();
    void terminateLink();

    boolean isConnected();
}
