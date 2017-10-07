package arduino;

import java.util.function.Consumer;

public interface ArduinoCommunicator {
    String getPortName();

    boolean write(byte data);
    boolean write(byte[] data);

    void addArduinoByteDataReceiver(Consumer<byte[]> arduinoDataReceiver);
    void removeArduinoByteReceiver(Consumer<byte[]> arduinoDataReceiver);

    void addArduinoStringDataReceiver(Consumer<String> arduinoDataReceiver);
    void removeArduinoStringReceiver(Consumer<String> arduinoDataReceiver);
}
