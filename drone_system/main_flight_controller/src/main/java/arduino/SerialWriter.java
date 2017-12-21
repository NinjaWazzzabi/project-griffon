package arduino;

import jssc.SerialPort;
import jssc.SerialPortException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes data to an outputStream.
 */
class SerialWriter {

    private SerialPort output;

    SerialWriter (SerialPort port)
    {
        this.output = port;
    }

    boolean write(byte data) {
        try {
            output.writeByte(data);
            return true;
        } catch (SerialPortException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean write(byte[] data){
        try {
            output.writeBytes(data);
            return true;
        } catch (SerialPortException e) {
            e.printStackTrace();
            return false;
        }
    }

}
