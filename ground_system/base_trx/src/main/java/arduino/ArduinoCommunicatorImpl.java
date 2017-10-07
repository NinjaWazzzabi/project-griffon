package arduino;

import jssc.*;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class ArduinoCommunicatorImpl implements ArduinoCommunicator, SerialPortEventListener {

    private final List<Consumer<byte[]>> arduinoByteDataReceivers;
    private final List<Consumer<String>> arduinoStringDataReceivers;
    @Getter
    private final SerialPort serialPort;

    ArduinoCommunicatorImpl(SerialPort serialPort) {
        this.serialPort = serialPort;
        arduinoByteDataReceivers = new ArrayList<>();
        arduinoStringDataReceivers = new ArrayList<>();
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
            try {
                alertArduinoListeners(serialPort.readBytes());
            } catch (SerialPortException ex) {
                System.out.println("Error in receiving string from COM-port: " + ex);
            }
        }
    }

    @Override
    public String getPortName() {
        return serialPort.getPortName();
    }

    @Override
    public boolean write(byte data) {
        try {
            serialPort.writeByte(data);
            return true;
        } catch (SerialPortException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean write(byte[] data) {
        try {
            serialPort.writeBytes(data);
            return true;
        } catch (SerialPortException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void addArduinoByteDataReceiver(Consumer<byte[]> arduinoDataReceiver) {
        synchronized (arduinoByteDataReceivers) {
            arduinoByteDataReceivers.add(arduinoDataReceiver);
        }
    }

    @Override
    public void removeArduinoByteReceiver(Consumer<byte[]> arduinoDataReceiver) {
        synchronized (arduinoByteDataReceivers) {
            arduinoByteDataReceivers.remove(arduinoDataReceiver);
        }
    }

    @Override
    public void addArduinoStringDataReceiver(Consumer<String> arduinoDataReceiver) {
        synchronized (arduinoStringDataReceivers) {
            arduinoStringDataReceivers.add(arduinoDataReceiver);
        }
    }

    @Override
    public void removeArduinoStringReceiver(Consumer<String> arduinoDataReceiver) {
        synchronized (arduinoStringDataReceivers) {
            arduinoStringDataReceivers.remove(arduinoDataReceiver);
        }
    }

    private void alertArduinoListeners(byte[] bytes) {
        String stringFromBytes = new String(bytes, StandardCharsets.UTF_8);
        synchronized (arduinoByteDataReceivers) {
            arduinoByteDataReceivers.forEach(consumer -> consumer.accept(bytes));
        }
        synchronized (arduinoStringDataReceivers) {
            arduinoStringDataReceivers.forEach(stringConsumer -> stringConsumer.accept(stringFromBytes));
        }
    }
}
