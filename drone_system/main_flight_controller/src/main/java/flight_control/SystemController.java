package flight_control;

import arduino.Arduino;
import arduino.ArduinoFactory;
import arduino.Descriptions;
import utils.AsciiValues;

/**
 * This class is used to control the main flight controls on a plane, such as:
 * Throttle, Rudder, Elevator and Ailerons.
 *
 * Due to design, this class does not directly control the position of the ailerons and elevator.
 * It will instead set a angle that the plane has to hold, and the low level controller that controls
 * the servos will adapt the position of the flight-surfaces to keep the plane in the desired pitch and roll.
 * This is because the low level controller can react faster to errors in the desired pitch and roll, and
 * thus adjust the position of the control surfaces faster.
 *
 * Throttle and Rudder, on the other hand, are controller directly. The value assigned here will translate to
 * a position on the servos that control the Throttle and rudder.
 *
 * The reason to this difference is because there can be an issue of control-surfaces trying to battle each other.
 * If, for example, the rudder is set to make the plane turn to its right but the ailerons are set to bank the plane left,
 * the control-surfaces will fight each other and the plane could become unstable.
 *
 * Control value ranges:
 *
 *      Throttle: 0.0 to 100.0
 *      Rudder: -100.0 to 100.0
 *
 *      Pitch: -90.0 to 90.0
 *      Roll: -90.0 to 90.0
 *
 */
public class SystemController {
    private Arduino arduino;
    private double[] controlValues = new double[DirectControl.values().length + IndirectControl.values().length];

    SystemController(Arduino arduino) throws IllegalArgumentException {
        if (arduino != null && arduino.hasDescription(Descriptions.HAS_SERVO_CONTROL)) {
            this.arduino = arduino;
        } else {
            throw new IllegalArgumentException("ERROR: Arduino does not have required description");
        }
    }

    public void setDesiredControl(double degrees, IndirectControl control) {
        controlValues[control.servoChannel] = degrees;
    }

    public void setDirectControl(double value, DirectControl control) {
        controlValues[control.servoChannel] = value;
    }

    private synchronized void updateArduino() {
        byte[] dataArray = new byte[controlValues.length + 1];
        for (int i = 0; i < controlValues.length; i++) {
            dataArray[i] = (byte) controlValues[i];
        }

        dataArray[controlValues.length] = AsciiValues.EOT; // End of transmission character
        arduino.writeTo(dataArray);
    }

    public synchronized void update() {
        updateArduino();
    }

    enum IndirectControl {
        PITCH(2),
        ROLL(3);

        private int servoChannel;
        IndirectControl(int servoChannel) {
            this.servoChannel = servoChannel;
        }
    }
    enum DirectControl {
        THROTTLE(0),
        RUDDER(1);

        private int servoChannel;
        DirectControl(int servoChannel) {
            this.servoChannel = servoChannel;
        }
    }
}
