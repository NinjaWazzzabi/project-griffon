package arduino;

import jssc.SerialPort;
import jssc.SerialPortException;
import lombok.Getter;
import lombok.Setter;
import utils.AsciiValues;

import java.util.function.Consumer;

import static utils.ByteConversion.byteArrayToCharArray;

public class Arduino {
    @Getter
    private final String desc;

    private SerialWriter serialWriter;
    private SerialReader serialReader;
    private SerialPort serialPort;

    @Setter
    private Consumer<String> onDataReceived;
    private final StringBuilder currentInput;

    public Arduino(String desc, SerialWriter serialWriter, SerialReader serialReader, SerialPort serialPort) {
        currentInput = new StringBuilder();
        serialReader.setOnInformationReceived(this::inputReceived);
        this.desc = desc;
        this.serialWriter = serialWriter;
        this.serialReader = serialReader;
        this.serialPort = serialPort;
    }

    public synchronized void writeTo(String data) {
        char[] chars = data.toCharArray();
        byte[] bytes = new byte[chars.length];

        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }

        serialWriter.write(bytes);
    }

    public String getConnectedPortName() {
        return serialPort.getPortName();
    }

    private synchronized void inputReceived(byte[] data) {
        char[] charArray = byteArrayToCharArray(data);
        for (char c : charArray) {
            if (c == AsciiValues.EOT) {
                String finalData = currentInput.toString();
                currentInput.delete(0,currentInput.length());
                if (onDataReceived != null) {
                    onDataReceived.accept(finalData);
                }
            } else {
                currentInput.append(c);
            }
        }
    }

    public boolean isConnected() {
        return serialPort != null;
    }

    public void disconnect() {
        if (serialPort != null) {
            try {
                serialPort.closePort();
            } catch (SerialPortException e) {
                // TODO: 03/10/2017 Handle exception
                e.printStackTrace();
            }
        }
    }
}
