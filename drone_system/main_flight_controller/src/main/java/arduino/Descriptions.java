package arduino;

public enum Descriptions {
    HAS_AHRS ("AHRS"),
    HAS_SERVO_CONTROL ("CTRL"),
    HAS_DISTANCE_SENSOR ("DIST");

    private String value;
    Descriptions(String value){
        this.value = value;
    }
    String getDescValue() {
        return this.value;
    }

}
