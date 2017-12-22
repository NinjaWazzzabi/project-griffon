package arduino;

import jssc.SerialPort;
import jssc.SerialPortList;
import lombok.Getter;
import utils.ByteConversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utils.ByteConversion.byteArrayToCharArray;
import static utils.ByteConversion.charArrayToByteArray;

public class ArduinoFactory {
    @Getter
    private static final ArduinoFactory instance = new ArduinoFactory();
    private static final int BAUD = SerialPort.BAUDRATE_9600;
    private static final int GET_DESC_TIMEOUT = 10000;

    private final List<Arduino> arduinos;

    ArduinoFactory() {
        this.arduinos = new ArrayList<>();
    }

    public Arduino getArduino(Descriptions desc) throws IllegalStateException{
        if (getAllPortNames().size() != arduinos.size()) {
            updateList();
        }

        for (Arduino arduino : arduinos) {
            if (arduino.getDesc().contains(desc.getDescValue())) {
                return arduino;
            }
        }
        throw new IllegalStateException("No Arduino with description: \"" + desc + "\" found");
    }

    private void updateList() {
        for (String portName : getAllPortNames()) {
            boolean portNameUsed = false;
            for (Arduino arduino : arduinos) {
                if (arduino.getDesc().contains(portName)) {
                    portNameUsed = true;
                }
            }
            if (!portNameUsed) {
                try {
                    arduinos.add(connectToArduino(portName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Arduino connectToArduino(String portName) throws Exception {
        SerialPort serialPort = new SerialPort(portName);
        serialPort.openPort();

        serialPort.setParams(BAUD,
                SerialPort.DATABITS_8,
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        serialPort.setFlowControlMode(
                SerialPort.FLOWCONTROL_RTSCTS_IN |
                        SerialPort.FLOWCONTROL_RTSCTS_OUT
        );

        SerialReader serialReader = new SerialReader(serialPort);
        serialPort.addEventListener(serialReader, SerialPort.MASK_RXCHAR);

        SerialWriter writer = new SerialWriter(serialPort);


        String desc = getArduinoDescription(serialReader, writer);

        return new Arduino(portName + "," + desc ,writer,serialReader,serialPort);
    }

    private String getArduinoDescription(SerialReader reader, SerialWriter writer) {
        StringBuilder desc = new StringBuilder();

        reader.setOnInformationReceived(bytes -> {
            StringBuilder currentDesc = new StringBuilder();
            StringBuilder lastDesc = new StringBuilder(" ");

            for (char aChar : byteArrayToCharArray(bytes)) {
                if (aChar == ArduinoCommunicationValues.EOT) {
                    // Check that two reads in a row gives the same description
                    if (currentDesc.toString().equals(lastDesc.toString())) {
                        reader.setOnInformationReceived(null);
                        writer.write(charArrayToByteArray(currentDesc.toString().toCharArray()));
                        desc.append(currentDesc.toString());
                        return;
                    } else {
                        lastDesc = currentDesc;
                        currentDesc = new StringBuilder();
                    }
                } else {
                    currentDesc.append(aChar);
                }
            }
        });

        long startTime = System.currentTimeMillis();
        while (desc.length() == 0 && System.currentTimeMillis() - startTime <= GET_DESC_TIMEOUT) {
            Thread.yield();
        }

        return desc.toString();
    }

    public static List<String> getAllPortNames() {
        List<String> portNames = new ArrayList<>();
        portNames.addAll(Arrays.asList(SerialPortList.getPortNames()));
        return portNames;
    }
}
