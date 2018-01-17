package flight_control;

import arduino.Arduino;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.AsciiValues;

import static arduino.Descriptions.HAS_SERVO_CONTROL;
import static flight_control.SystemController.DirectControl.RUDDER;
import static flight_control.SystemController.DirectControl.THROTTLE;
import static flight_control.SystemController.IndirectControl.PITCH;
import static flight_control.SystemController.IndirectControl.ROLL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SystemControllerTest {

    SystemController systemController;
    Arduino mockino;

    @BeforeEach
    void setUp() {
        mockino = mock(Arduino.class);
        when(mockino.hasDescription(any())).thenReturn(false);
        when(mockino.hasDescription(HAS_SERVO_CONTROL)).thenReturn(true);
        when(mockino.getDesc()).thenReturn("AHRS");
        when(mockino.getConnectedPortName()).thenReturn("COM5");
        when(mockino.isConnected()).thenReturn(true);

        systemController = new SystemController(mockino);
    }

    @Test
    public void arduinoAcceptance() {
        Arduino wrongino = mock(Arduino.class);
        when(wrongino.hasDescription(any())).thenReturn(false);

        Assertions.assertThrows(IllegalArgumentException.class, () -> new SystemController(wrongino));

        Arduino ard = mock(Arduino.class);
        when(ard.hasDescription(HAS_SERVO_CONTROL)).thenReturn(true);

        new SystemController(ard);
        verify(ard).hasDescription(HAS_SERVO_CONTROL);
    }

    @Test
    public void sendingValuesToArduino() {
        byte[] values = {90,5,30,45, AsciiValues.EOT};

        systemController.setDirectControl(values[0], THROTTLE);
        systemController.setDirectControl(values[1], RUDDER);

        systemController.setDesiredControl(values[2], PITCH);
        systemController.setDesiredControl(values[3], ROLL);
        systemController.update();

        verify(mockino).writeTo(values);
    }

}