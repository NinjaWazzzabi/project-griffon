package arduino;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.util.*;

public class ArduinoCommunicatorManager {
    private static final Map<String, ArduinoCommunicatorImpl> CONNECTED_ARDUINOS = new HashMap<>();

    public List<String> getAllAvaliablePorts() {
        List<String> portNames = new ArrayList<>();
        portNames.addAll(Arrays.asList(SerialPortList.getPortNames()));
        return portNames;
    }

    public static String getConnectedPortName(ArduinoCommunicatorImpl arduinoCommunicator) {
        return arduinoCommunicator.getPortName();
    }

    public static ArduinoCommunicator getArduinoConnection(String portName) throws SerialPortException {
        ArduinoCommunicatorImpl arduinoCommunicator = CONNECTED_ARDUINOS.get(portName);
        if (arduinoCommunicator == null) {
            SerialPort serialPort = new SerialPort(portName);

            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            serialPort.setFlowControlMode(
                    SerialPort.FLOWCONTROL_RTSCTS_IN |
                            SerialPort.FLOWCONTROL_RTSCTS_OUT
            );

            arduinoCommunicator = new ArduinoCommunicatorImpl(serialPort);
            serialPort.addEventListener(arduinoCommunicator, SerialPort.MASK_RXCHAR);

        }
        return arduinoCommunicator;
    }

    boolean disconnect(ArduinoCommunicatorImpl arduinoCommunicator) {
        SerialPort serialPort = arduinoCommunicator.getSerialPort();
        if (serialPort != null) {
            try {
                serialPort.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    static boolean isConnected(ArduinoCommunicatorImpl arduinoCommunicator) {
        SerialPort serialPort = arduinoCommunicator.getSerialPort();
        return serialPort != null && serialPort.isOpened();
    }
}
