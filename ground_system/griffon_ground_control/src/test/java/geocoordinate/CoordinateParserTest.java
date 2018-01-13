package geocoordinate;

import org.junit.jupiter.api.Test;

import static geocoordinate.CoordinateParser.CoordinateFormat.*;
import static org.junit.jupiter.api.Assertions.*;

class CoordinateParserTest {

    @Test
    void dmsConversion() {
        GeoCoordinate coordinate1 = CoordinateParser.parseStringToCoordinate("40 26 46 N 79 58 56 W", DEGREES_MINUTES_SECONDS);
        GeoCoordinate coordinate2 = CoordinateParser.parseStringToCoordinate("40° 26′ 46″ N 79° 58′ 56″ W", DEGREES_MINUTES_SECONDS);
        GeoCoordinate coordinate3 = CoordinateParser.parseStringToCoordinate("63° 37' 46.542'' N 19° 38' 14.6256'' W", DEGREES_MINUTES_SECONDS);

        double realLong1 = 40.446111;
        double realLat1 = -79.982222;
        assertEquals(realLat1, coordinate1.getLatitude(), 0.001);
        assertEquals(realLong1, coordinate1.getLongitude(), 0.001);

        assertEquals(realLat1, coordinate2.getLatitude(), 0.001);
        assertEquals(realLong1, coordinate2.getLongitude(), 0.001);

        double realLong3 = 63.629595;
        double realLat3 = -19.637396;

        assertEquals(realLat3, coordinate3.getLatitude(), 0.001);
        assertEquals(realLong3, coordinate3.getLongitude(), 0.001);
    }

    @Test
    void ddConversion() {
        GeoCoordinate coordinate1 = CoordinateParser.parseStringToCoordinate("63.629642, -19.637375", DECIMAL_DEGREES);
        GeoCoordinate coordinate2 = CoordinateParser.parseStringToCoordinate("63.629642 19.637375 W", DECIMAL_DEGREES);
        GeoCoordinate coordinate3 = CoordinateParser.parseStringToCoordinate("63.629642 S 19.637375", DECIMAL_DEGREES);

        double realLong1 = 63.629642;
        double realLat1 = -19.637375;
        assertEquals(realLat1, coordinate1.getLatitude(), 0.001);
        assertEquals(realLong1, coordinate1.getLongitude(), 0.001);

        assertEquals(realLat1, coordinate2.getLatitude(), 0.001);
        assertEquals(realLong1, coordinate2.getLongitude(), 0.001);

        double realLong3 = -63.629595;
        double realLat3 = 19.637396;

        assertEquals(realLat3, coordinate3.getLatitude(), 0.001);
        assertEquals(realLong3, coordinate3.getLongitude(), 0.001);
    }

}