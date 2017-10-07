package arduino;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


class ArduinoCommunicatorImplTest {
    private static final String SERIAL_PORT_ID = "port";
    private static final int RXCHAR_EVENT = 1;

    private static final byte[] BYTE_VALUES = {65,66,67};
    private static final String STRING_VALUE = "ABC";
    private static final String PORT_NAME = "port";

    private ArduinoCommunicatorImpl arduinoCommunicator;
    private SerialPort mockedPort;

    private String lastReceivedString = null;
    private byte[] lastReceivedByte;

    @BeforeEach
    void setUp() throws SerialPortException {
        mockedPort = mock(SerialPort.class);
        when(mockedPort.getPortName()).thenReturn(PORT_NAME);
        when(mockedPort.readBytes()).thenReturn(BYTE_VALUES);

        arduinoCommunicator = new ArduinoCommunicatorImpl(mockedPort);
        arduinoCommunicator.addArduinoByteDataReceiver(bytes -> lastReceivedByte = bytes);
        arduinoCommunicator.addArduinoStringDataReceiver(s -> lastReceivedString = s);
    }

    @Test
    void serialEvent() {
        arduinoCommunicator.serialEvent(new SerialPortEvent(SERIAL_PORT_ID,RXCHAR_EVENT, 1));

        assertEquals(STRING_VALUE, lastReceivedString);
        assertEquals(BYTE_VALUES, lastReceivedByte);
    }

    @Test
    void getPortName() {
        assertEquals(PORT_NAME, arduinoCommunicator.getPortName());
    }

    @Test
    void write() throws SerialPortException {
        arduinoCommunicator.write((byte)1);

        verify(mockedPort).writeByte((byte)1);
    }

    @Test
    void write1() throws SerialPortException {
        arduinoCommunicator.write(BYTE_VALUES);
        verify(mockedPort).writeBytes(BYTE_VALUES);
    }

    @Test
    void getSerialPort() {
        assertEquals(PORT_NAME, arduinoCommunicator.getPortName());
    }
}