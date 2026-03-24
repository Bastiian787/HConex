package com.hconex.utils;

/**
 * Utility methods for hex encoding and decoding.
 */
public final class HexUtils {

    private static final char[] HEX_CHARS =
            {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    private HexUtils() {
        throw new UnsupportedOperationException("HexUtils is a utility class");
    }

    /**
     * Converts a byte array to its uppercase hexadecimal string representation.
     *
     * @param data the bytes to convert
     * @return hex string (e.g. {@code "DEADBEEF"})
     */
    public static String toHex(byte[] data) {
        if (data == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(HEX_CHARS[(b >> 4) & 0xF]);
            sb.append(HEX_CHARS[b & 0xF]);
        }
        return sb.toString();
    }

    /**
     * Converts a byte array to a space-separated hex string for easy reading.
     *
     * @param data the bytes to convert
     * @return spaced hex string (e.g. {@code "DE AD BE EF"})
     */
    public static String toHexSpaced(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(data.length * 3 - 1);
        for (int i = 0; i < data.length; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(HEX_CHARS[(data[i] >> 4) & 0xF]);
            sb.append(HEX_CHARS[data[i] & 0xF]);
        }
        return sb.toString();
    }

    /**
     * Decodes a hexadecimal string into a byte array.
     *
     * @param hex the hex string to decode (case-insensitive, may contain spaces)
     * @return decoded bytes
     * @throws IllegalArgumentException if the hex string has an odd length after
     *                                  removing spaces
     */
    public static byte[] fromHex(String hex) {
        if (hex == null || hex.isEmpty()) {
            return new byte[0];
        }
        String clean = hex.replaceAll("\\s", "");
        if (clean.length() % 2 != 0) {
            throw new IllegalArgumentException(
                    "Hex string must have an even number of characters: " + hex);
        }
        byte[] result = new byte[clean.length() / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(clean.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }

    /**
     * Converts a single byte to a two-character hex string.
     *
     * @param b the byte to convert
     * @return two-character uppercase hex string
     */
    public static String byteToHex(byte b) {
        return String.valueOf(HEX_CHARS[(b >> 4) & 0xF]) + HEX_CHARS[b & 0xF];
    }
}
