package utils;


import java.nio.ByteBuffer;

public class ByteConversion {

    /**
     * Converts a float value to an array, size of 4, of bytes that represent the same value of the float in
     the 32 bit IEEE 754 format
     * @param value Float value used for the conversion
     * @return the size 4 array that represents the value of the float in the IEEE 754 format
     */
    public static byte [] floatToByteArray(float value)
    {
        return ByteBuffer.allocate(4).putFloat(value).array();
    }

    /**
     * Converts an array of bytes to their binary representation
     * @param bytes Array of bytes to be used
     * @param readabilityFormatting Formats the String into a more readable composition (dashes between every byte, start/end brackets)
     * @return The string that represents the array of bytes in their binary format
     */
    public static String toStringInBinary(byte[] bytes, boolean readabilityFormatting){
        StringBuilder sb = new StringBuilder();

        if (readabilityFormatting) {
            sb.append("[");
        }
        for (int i = 0, bytesLength = bytes.length; i < bytesLength; i++) {

            //print binary representation of the byte
            sb.append(Integer.toBinaryString(bytes[i] & 255 | 256).substring(1));

            //Dash separation
            if (i != bytesLength-1 && readabilityFormatting) {
                sb.append("-");
            }
        }

        if (readabilityFormatting) {
            sb.append("]");
        }
        return sb.toString();
    }

    /**
     * Outputs the integer value of a bit from an array of bytes.
     * @param bytes Array of bytes to be read from
     * @param pos Position of the bit in the array
     * @return the integer value of the bit
     */
    public static int bitValue(byte[] bytes, int pos) {
        int posByte = pos/8;
        int posBit = pos%8;
        byte valByte = bytes[posByte];
        int valInt = valByte>>(8-(posBit+1)) & 0x0001;
        return valInt;
    }
}
