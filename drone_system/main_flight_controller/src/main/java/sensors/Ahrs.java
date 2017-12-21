package sensors;

import arduino.Arduino;
import arduino.ArduinoFactory;
import arduino.Descriptions;
import lombok.Getter;
import lombok.Setter;

import static utils.ByteConversion.byteArrayToFloat;
import static utils.ByteConversion.charArrayToByteArray;

public class Ahrs {

    float[] values = {0.0f,0.0f,0.0f};  // Pitch, Roll, Yaw

    @Setter
    private Runnable onUpdatedValues;
    private static Arduino arduino;

    public Ahrs() {
        if (arduino == null) {
            arduino = ArduinoFactory.getInstance().getArduino(Descriptions.HAS_AHRS);
        }
        arduino.setOnDataReceived(this::parseArduinoData);
    }

    public float getPitch() {
        return values[0];
    }

    public float getRoll() {
        return values[1];
    }

    public float getYaw() {
        return values[2];
    }

    private void parseArduinoData(String data) {
        // TODO: 21/12/2017 Improve protocol
        String[] splitData;
        try {
            data = data.substring(data.indexOf("<") + 1, data.indexOf(">"));
            splitData = data.split(",");
        } catch (StringIndexOutOfBoundsException oobe) {
            oobe.printStackTrace();
            return;
        }


        for (int i = 0; i < splitData.length; i++) {
            try {
                float f = Float.valueOf(splitData[i]);
                values[i] = f;
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }

        if (onUpdatedValues != null) {
            onUpdatedValues.run();
        }
    }

}
