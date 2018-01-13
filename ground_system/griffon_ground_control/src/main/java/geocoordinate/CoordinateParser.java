package geocoordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CoordinateParser {

    public static GeoCoordinate parseStringToCoordinate(String str, CoordinateFormat format) throws IllegalArgumentException {
        GeoCoordinate geoCoordinate;
        switch (format) {
            case DEGREES_MINUTES_SECONDS:
                geoCoordinate = dmsToGeoCoordinate(str);
                break;
            case DECIMAL_DEGREES:
                geoCoordinate = ddToGeoCoordinate(str);
                break;
            default:
                throw new IllegalArgumentException("WARNING: invalid coordinate format");
        }
        return geoCoordinate;
    }

    private static GeoCoordinate dmsToGeoCoordinate(String str) throws IllegalArgumentException {
        List<Double> values = getDoublesFromString(str);
        if (values.size() != 6) {
            throw new IllegalArgumentException("WARNING: unexpected amount of dms values. Expected: 6, Actual: " + values.size());
        }

        double longitude = values.get(0) + values.get(1) / 60.0 + values.get(2) / 3600.0;
        double latitude = values.get(3) + values.get(4) / 60.0 + values.get(5) / 3600.0;

        if (str.toLowerCase().contains("s")) {
            longitude *= -1;
        }
        if (str.toLowerCase().contains("w")) {
            latitude *= -1;
        }

        return new BaseCoordinate(longitude, latitude);
    }

    private static GeoCoordinate ddToGeoCoordinate(String str) throws IllegalArgumentException {
        List<Double> values = getDoublesFromString(str);
        if (values.size() != 2) {
            throw new IllegalArgumentException("WARNING: unexpected amount of dms values. Expected: 6, Actual: " + values.size());
        }

        double longitude = values.get(0);
        double latitude = values.get(1);

        if (str.toLowerCase().contains("w")) {
            latitude *= -1;
        }

        if (str.toLowerCase().contains("s")) {
            longitude *= -1;
        }

        return new BaseCoordinate(longitude, latitude);
    }

    private static List<Double> getDoublesFromString(String str) throws IllegalArgumentException {
        String[] splitString = str.split("[^\\.\\d-]+");

        List<Double> values = new ArrayList<>();

        for (String number : splitString) {
            try {
                values.add(Double.parseDouble(number));
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("WARNING: coordinate formatting error! Coordinate String: " + str + ", Non valid number: " + number);
            }
        }

        return values;
    }

    private static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public enum CoordinateFormat {
        DEGREES_MINUTES_SECONDS,
        DECIMAL_DEGREES
    }

}
