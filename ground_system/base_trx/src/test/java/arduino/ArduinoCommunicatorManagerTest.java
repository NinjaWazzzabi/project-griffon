package arduino;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// TODO: 07/10/2017 Add more tests
class ArduinoCommunicatorManagerTest {
    @BeforeEach
    void setUp() {
    }

    @Test
    void getAllAvailablePorts() {
    }

    @Test
    void getConnectedPortName() {
        ArduinoCommunicatorImpl mock = mock(ArduinoCommunicatorImpl.class);
        when(mock.getPortName()).thenReturn("port");

        assertEquals("port",ArduinoCommunicatorManager.getConnectedPortName(mock));
    }

}