package utils;

/**
 * Special values for communication to and from the arduino. Reference: http://www.asciitable.com/
 */
public class AsciiValues {

    public final static byte SOH = 1;    //Start of Heading
    public final static byte STX = 2;    //Start of Text
    public final static byte ETX = 3;    //End of Text
    public final static byte EOT = 4;    //End of Transmission
    public final static byte ENQ = 5;    //Enquiry
    public final static byte ACK = 6;    //Acknowledge
    public final static byte LF  = 12;   //NL line feed, new line

}
