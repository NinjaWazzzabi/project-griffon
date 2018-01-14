package arduino;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.io.InputStream;
import java.util.function.Consumer;

/**
 * Reads Serial input from {@link InputStream}.
 */
class SerialReader implements SerialPortEventListener {

    private SerialPort serialPort;
    private Consumer<byte[]> onInformationReceived;

    SerialReader(SerialPort serialPort) throws SerialPortException {
        this.serialPort = serialPort;
        serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
    }

    void setOnInformationReceived(Consumer<byte[]> consumer) {
        this.onInformationReceived = consumer;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                byte[] bytes = serialPort.readBytes();
                if (onInformationReceived != null) {
                    onInformationReceived.accept(bytes);
                }
            }
            catch (SerialPortException ex) {
                System.out.println("ERROR: Error in receiving string from COM-port: " + ex);
            }
        }
    }
}
