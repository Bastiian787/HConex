package com.hconex.utils;

/**
 * Utility class for byte operations
 */
public class ByteUtils {
    
    /**
     * Read a short (2 bytes) from byte array at offset
     */
    public static short readShort(byte[] data, int offset) {
        if (offset + 2 > data.length) {
            throw new IndexOutOfBoundsException("Not enough bytes to read short");
        }
        return (short) (((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF));
    }
    
    /**
     * Read an int (4 bytes) from byte array at offset
     */
    public static int readInt(byte[] data, int offset) {
        if (offset + 4 > data.length) {
            throw new IndexOutOfBoundsException("Not enough bytes to read int");
        }
        return ((data[offset] & 0xFF) << 24) |
               ((data[offset + 1] & 0xFF) << 16) |
               ((data[offset + 2] & 0xFF) << 8) |
               (data[offset + 3] & 0xFF);
    }
    
    /**
     * Write a short to byte array at offset
     */
    public static void writeShort(byte[] data, int offset, short value) {
        if (offset + 2 > data.length) {
            throw new IndexOutOfBoundsException("Not enough space to write short");
        }
        data[offset] = (byte) ((value >> 8) & 0xFF);
        data[offset + 1] = (byte) (value & 0xFF);
    }
    
    /**
     * Write an int to byte array at offset
     */
    public static void writeInt(byte[] data, int offset, int value) {
        if (offset + 4 > data.length) {
            throw new IndexOutOfBoundsException("Not enough space to write int");
        }
        data[offset] = (byte) ((value >> 24) & 0xFF);
        data[offset + 1] = (byte) ((value >> 16) & 0xFF);
        data[offset + 2] = (byte) ((value >> 8) & 0xFF);
        data[offset + 3] = (byte) (value & 0xFF);
    }
    
    /**
     * Read a byte from byte array at offset
     */
    public static byte readByte(byte[] data, int offset) {
        if (offset >= data.length) {
            throw new IndexOutOfBoundsException("Offset out of bounds");
        }
        return data[offset];
    }
    
    /**
     * Write a byte to byte array at offset
     */
    public static void writeByte(byte[] data, int offset, byte value) {
        if (offset >= data.length) {
            throw new IndexOutOfBoundsException("Offset out of bounds");
        }
        data[offset] = value;
    }
}
