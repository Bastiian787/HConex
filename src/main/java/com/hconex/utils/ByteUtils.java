package com.hconex.utils;

public class ByteUtils {
    public static short readShort(byte[] data, int offset) {
        if (offset + 2 > data.length) throw new IndexOutOfBoundsException();
        return (short) (((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF));
    }
    
    public static int readInt(byte[] data, int offset) {
        if (offset + 4 > data.length) throw new IndexOutOfBoundsException();
        return ((data[offset] & 0xFF) << 24) | ((data[offset + 1] & 0xFF) << 16) |
               ((data[offset + 2] & 0xFF) << 8) | (data[offset + 3] & 0xFF);
    }
}
