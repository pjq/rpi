package com.aliyun.iot.util;

import java.io.UnsupportedEncodingException;

/**
 * Created by Gary on 14-5-6.
 */
public class ProtocolUtils {

    public static final String CHAR_SET    = "UTF-8";

    public static final int    BUFFER_SIZE = 1024;

    /**
     * Return the bytes with string encoded as MSB, LSB and UTF-8 encoded string content.
     */
    public static byte[] encodeString(String str) {

        if (str == null) {

            str = "";
        }

        DynamicByteBuffer out = DynamicByteBuffer.allocate(2);
        byte[] raw;
        try {
            raw = str.getBytes(CHAR_SET);
            // NB every Java platform has got UTF-8 encoding by default, so this
            // exception are never raised.
        } catch (UnsupportedEncodingException ex) {
            return null;
        }

        writeWord(out, raw.length);
        out.put(raw);

        return out.array();
    }

    /**
     * Load a string from the given buffer, reading first the two bytes of len and then the UTF-8 bytes of the string.
     * 
     * @return the decoded string or null if NEED_DATA
     */
    public static String decodeString(DynamicByteBuffer in) {
        if (in.remaining() < 2) {
            return null;
        }

        in.mark();

        int strLen = readWord(in);
        if (in.remaining() < strLen) {
            in.reset();
            return null;
        }

        byte[] strRaw = new byte[strLen];
        in.get(strRaw);

        try {

            return new String(strRaw, CHAR_SET);
        } catch (UnsupportedEncodingException ex) {

            return null;
        }
    }

    /**
     * Read 2 bytes from in buffer first MSB, and then LSB returning as int.
     */
    private static int readWord(DynamicByteBuffer in) {
        int msb = in.get() & 0x00FF; // remove sign extension due to casting
        int lsb = in.get() & 0x00FF;
        msb = (msb << 8) | lsb;
        return msb;
    }

    /**
     * Writes as 2 bytes the int value into buffer first MSB, and then LSB.
     */
    private static void writeWord(DynamicByteBuffer out, int value) {
        out.put((byte) ((value & 0xFF00) >> 8)); // msb
        out.put((byte) (value & 0x00FF)); // lsb
    }

    // private static long readDWord(DynamicByteBuffer in) {
    // return (long) (in.get() << 24 & 0xFF000000l) +
    // (in.get() << 16 & 0xFF0000) +
    // (in.get() << 8 & 0xFF00) +
    // (in.get() << 0 & 0xFF);
    // }
    //
    // private static void writeDWord(DynamicByteBuffer out, long value) {
    // out.put((byte) ((value & 0xFF000000l) >> 24));
    // out.put((byte) ((value & 0xFF0000) >> 16));
    // out.put((byte) ((value & 0xFF00) >> 8));
    // out.put((byte) (value & 0x00FF));
    // }

    public static byte[] encodeVariableNumber(long value) {

        DynamicByteBuffer out = DynamicByteBuffer.allocate(8);
        encodeVariableNumber(out, value);

        return out.array();
    }

    public static void encodeVariableNumber(DynamicByteBuffer out, long value) {

        byte digit;

        do {

            digit = (byte) (value % 128);
            value = value / 128;
            // if there are more digits to encodeBody, set the top bit of this digit
            if (value > 0) {
                digit = (byte) (digit | 0x80);
            }

            out.put(digit);
        } while (value > 0);
    }

    public static long decodeVariableNumber(DynamicByteBuffer in) {
        // TODO 长度要做一个防范
        long multiplier = 1;
        long value = 0;
        byte digit;

        do {
            if (in.remaining() < 1) {

                return -1;
            }

            digit = in.get();
            value += (digit & 0x7F) * multiplier;
            multiplier *= 128;
        } while ((digit & 0x80) != 0);

        return value;
    }
    
   
}
