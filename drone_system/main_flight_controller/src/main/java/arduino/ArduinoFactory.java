package arduino;

import jssc.SerialPort;
import jssc.SerialPortList;
import lombok.Getter;
import utils.ByteConversion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArduinoFactory {
    @Getter
    private static final ArduinoFactory instance = new ArduinoFactory();
    private static final int BAUD = SerialPort.BAUDRATE_9600;

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
        final StringBuilder sb = new StringBuilder();
        long currentTime = System.currentTimeMillis();
        // TODO: 21/12/2017 I think it works, but it's kinda ugly
        Thread getDesc = new Thread(() -> {
            final boolean[] descReceived = {false};
            reader.setOnInformationReceived(bytes -> {
                boolean first = true;
                char[] chars = ByteConversion.byteArrayToCharArray(bytes);
                for (char aChar : chars) {
                    if (aChar == ArduinoCommunicationValues.EOT) {
                        if (first) {
                            sb.delete(0,sb.length());
                            first = false;
                        } else {
                            reader.setOnInformationReceived(null);
                            descReceived[0] = true;
                            System.out.println(sb.toString());
                            writer.write(utils.ByteConversion.charArrayToByteArray(sb.toString().toCharArray()));
                            return;
                        }
                    } else {
                        sb.append(aChar);
                    }
                }
            });

            long timeOut = 10000;
            long startTime = System.currentTimeMillis();
            while (!descReceived[0] && System.currentTimeMillis() - startTime <= timeOut) {
                Thread.yield();
            }
        });

        getDesc.start();
        try {
            getDesc.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("-------------------------------------------------------------");
        System.out.println("Desc: " + sb.toString());
        System.out.println("Time Taken: " + (System.currentTimeMillis() - currentTime));
        System.out.println("-------------------------------------------------------------");
        return sb.toString();
    }

    public static List<String> getAllPortNames() {
        List<String> portNames = new ArrayList<>();
        portNames.addAll(Arrays.asList(SerialPortList.getPortNames()));
        System.out.println(Arrays.toString(portNames.toArray()));
        return portNames;
    }
}
