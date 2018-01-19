package drone;

import com.google.gson.Gson;
import geocoordinate.MovableCoordinate;
import lombok.Getter;
import lombok.Setter;

public class MockLoiterDrone extends BaseDrone{

    private Thread autoUpdate;
    private double currentRadian;
    private long lastUpdate;

    @Getter
    @Setter
    private double speed, radius;

    private MovableCoordinate movableCoordinate;

    private double loiterCenterLongitude;
    private double loiterCenterLatitude;

    public MockLoiterDrone(int id, Gson gson) {
        super(id, gson);
        speed = 0;
        radius = 0;
        currentRadian = 0;
        loiterCenterLongitude = 0;
        loiterCenterLatitude = 0;
        lastUpdate = 0;
        movableCoordinate = new MovableCoordinate(this.coordinate);
        coordinate = movableCoordinate;
    }

    public void setLoiterCenter(double longitude, double latitude) {
        loiterCenterLongitude = longitude;
        loiterCenterLatitude = latitude;
    }

    public void startLoiter() {
        lastUpdate = System.currentTimeMillis();
        autoUpdate = new Thread(() -> {
            while (true) {
                if (Thread.interrupted()) {
                    break;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                update();
            }
        });
        autoUpdate.start();
    }

    public void stopLoiter() {
        if (autoUpdate != null && autoUpdate.isAlive()) {
            autoUpdate.interrupt();
        }
    }

    private void update() {
        // Check time passed
        long currentTime = System.currentTimeMillis();
        double secondsPassed = ((double) currentTime - (double) lastUpdate) / 1000;
        lastUpdate = currentTime;

        // Update radian
        double circumference = 2 * Math.PI * radius;
        double radiansTraveled = ((speed * secondsPassed)/ circumference) * 2 * Math.PI;
        currentRadian = (currentRadian + radiansTraveled) % (2 * Math.PI);

        // Update position
        movableCoordinate.set(loiterCenterLongitude,loiterCenterLatitude);
        movableCoordinate.offsetMeters(Math.sin(currentRadian) * radius, Math.cos(currentRadian) * radius);

        // Set rotation
        this.roll = (float) (-90 / radius);
        this.pitch = 5;
        this.yaw = (float) Math.toDegrees(-currentRadian);
    }
}
