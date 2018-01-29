package arduino;

import jssc.SerialPort;
import jssc.SerialPortList;
import lombok.Getter;
import utils.ByteConversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArduinoFactory {
    private static final byte ASCII_COMMA = (byte) 44;
    private static final byte ASCII_GREATER_THAT = (byte) 62;
    private static final byte ASCII_LESS_THAT = (byte) 60;

    private static final String regex = "(?<=[\\x01])[\\w,]+(?=[\\x04])";

    @Getter
    private static final ArduinoFactory instance = new ArduinoFactory();
    private static final int BAUD = SerialPort.BAUDRATE_9600;
    private static final int GET_DESC_TIMEOUT = 5000;

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

    // TODO: 13/01/2018 Fix getting arduino description!!!
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

        SerialWriter writer = new SerialWriter(serialPort);


        String desc = getArduinoDescription(serialReader, writer);
        return new Arduino(portName + "," + desc ,writer,serialReader,serialPort);
    }

    private String getArduinoDescription(SerialReader reader, SerialWriter writer) {
        RegexFinder sf = new RegexFinder(regex);

        reader.setOnInformationReceived(bytes -> {
            for (byte aByte : bytes) {
                sf.appendStringData(String.valueOf((char) aByte));
            }
        });

        String desc = sf.startSearch(4, 5000);

        writer.write(ByteConversion.charArrayToByteArray(desc.toCharArray()));

        return desc;
    }

    public static List<String> getAllPortNames() {
        List<String> portNames = new ArrayList<>();
        portNames.addAll(Arrays.asList(SerialPortList.getPortNames()));
        return portNames;
    }
}
