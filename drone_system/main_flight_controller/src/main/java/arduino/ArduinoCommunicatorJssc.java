package arduino;

import jssc.*;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static arduino.ArduinoCommunicationValues.*;

public class ArduinoCommunicatorJssc {

    private boolean arduinoReady;

    private SerialWriter serialWriter;
    private SerialReader serialReader;
    private SerialPort serialPort;

}
