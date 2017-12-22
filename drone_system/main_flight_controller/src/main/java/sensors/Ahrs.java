package sensors;

import arduino.Arduino;
import arduino.ArduinoFactory;
import arduino.Descriptions;
import lombok.Setter;

public class Ahrs {

    private float[] ahrsValues = {0.0f,0.0f,0.0f};  // Pitch, Roll, Yaw

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
        return ahrsValues[0];
    }

    public float getRoll() {
        return ahrsValues[1];
    }

    public float getYaw() {
        return ahrsValues[2];
    }

    // Reading from the arduino the first time will almost always give garbage data
    private boolean firstRead = true;
    private void parseArduinoData(String data) {
        if (firstRead) {
            firstRead = false;
            return;
        }

        // TODO: 21/12/2017 Improve protocol
        String[] splitData;
        try {
            data = data.substring(data.indexOf("<") + 1, data.indexOf(">"));
            splitData = data.split(",");
        } catch (StringIndexOutOfBoundsException oobe) {
            System.out.println("String :" + data);
            oobe.printStackTrace();
            return;
        }

        for (int i = 0; i < splitData.length; i++) {
            try {
                float f = Float.valueOf(splitData[i]);
                ahrsValues[i] = f;
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
        }

        if (onUpdatedValues != null) {
            onUpdatedValues.run();
        }
    }

}
