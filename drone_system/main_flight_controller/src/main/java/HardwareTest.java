import arduino.Arduino;
import arduino.ArduinoFactory;
import arduino.Descriptions;
import flight_control.SystemController;

import java.util.Scanner;

import static flight_control.SystemController.DirectControl.THROTTLE;

public class HardwareTest {

    public HardwareTest() {

        Arduino arduino = ArduinoFactory.getInstance().getArduino(Descriptions.HAS_SERVO_CONTROL);
        SystemController systemController = new SystemController(arduino);

        new Thread(() -> {
            askToQuit();
            if (arduino != null) {
                arduino.disconnect();
                System.out.println("Arduino disconnected!");
            }
            System.exit(0);
        }).start();

        int x = 0;

        while (true) {
            systemController.setDirectControl(x, THROTTLE);
            systemController.update();

            x++;
            if (x > 100) {
                x = 0;
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void askToQuit() {
        System.out.println("Press \"q\" to quit");
        Scanner sc = new Scanner(System.in);
        while (true) {
            String line = sc.nextLine();
            if (line.equals("q")) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        new HardwareTest();
    }
}
