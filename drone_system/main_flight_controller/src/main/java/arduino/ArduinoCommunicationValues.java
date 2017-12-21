package arduino;

/**
 * Special values for communication to and from the arduino. Reference: http://www.asciitable.com/
 */
public class ArduinoCommunicationValues {

    public final static int SOH = 1;    //Start of Heading
    public final static int STX = 2;    //Start of Text
    public final static int ETX = 3;    //End of Text
    public final static int EOT = 4;    //End of Transmission
    public final static int ENQ = 5;    //Enquiry
    public final static int ACK = 6;    //Acknowledge
    public final static int LF  = 12;   //NL line feed, new line

}
