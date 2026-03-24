package com.hconex.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Utility methods for common byte-array operations.
 */
public final class ByteUtils {

    private ByteUtils() {
        throw new UnsupportedOperationException("ByteUtils is a utility class");
    }

    /**
     * Concatenates two byte arrays into a single new array.
     *
     * @param a first array
     * @param b second array
     * @return new array containing all bytes of {@code a} followed by all bytes of {@code b}
     */
    public static byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * Returns a sub-array of {@code data} from {@code offset} with length {@code length}.
     *
     * @param data   source array
     * @param offset start index (inclusive)
     * @param length number of bytes to copy
     * @return the sub-array
     */
    public static byte[] slice(byte[] data, int offset, int length) {
        byte[] result = new byte[length];
        System.arraycopy(data, offset, result, 0, length);
        return result;
    }

    /**
     * Reads a big-endian 32-bit integer from the byte array at the given offset.
     *
     * @param data   source array
     * @param offset byte offset
     * @return the integer value
     */
    public static int readInt(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    /**
     * Writes a big-endian 32-bit integer into the byte array at the given offset.
     *
     * @param data   target array (must have at least {@code offset + 4} bytes)
     * @param offset byte offset
     * @param value  integer value to write
     */
    public static void writeInt(byte[] data, int offset, int value) {
        ByteBuffer.wrap(data, offset, 4).order(ByteOrder.BIG_ENDIAN).putInt(value);
    }

    /**
     * Reads a big-endian unsigned 16-bit value from the byte array at the given offset.
     *
     * @param data   source array
     * @param offset byte offset
     * @return the unsigned short value (0–65535)
     */
    public static int readShort(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 2).order(ByteOrder.BIG_ENDIAN).getShort() & 0xFFFF;
    }

    /**
     * Writes a big-endian 16-bit value into the byte array at the given offset.
     *
     * @param data   target array (must have at least {@code offset + 2} bytes)
     * @param offset byte offset
     * @param value  short value to write
     */
    public static void writeShort(byte[] data, int offset, short value) {
        ByteBuffer.wrap(data, offset, 2).order(ByteOrder.BIG_ENDIAN).putShort(value);
    }

    /**
     * Returns {@code true} if {@code data} starts with the bytes in {@code prefix}.
     *
     * @param data   the array to inspect
     * @param prefix the expected prefix
     * @return {@code true} if {@code data} starts with {@code prefix}
     */
    public static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new byte array filled with zeros of the given length.
     *
     * @param length number of bytes
     * @return zero-filled byte array
     */
    public static byte[] zeros(int length) {
        return new byte[length];
    }
}
