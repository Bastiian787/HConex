package com.hconex.core.protocol;

import com.hconex.utils.ByteUtils;
import java.nio.ByteBuffer;

/**
 * Encodes data into Habbo packets
 */
public class PacketEncoder {
    
    /**
     * Encode a string value
     */
    public static byte[] encodeString(String value) {
        if (value == null) {
            value = "";
        }
        
        byte[] stringBytes = value.getBytes();
        byte[] encoded = new byte[2 + stringBytes.length];
        
        // Write length (2 bytes)
        ByteUtils.writeShort(encoded, 0, (short) stringBytes.length);
        
        // Write string
        System.arraycopy(stringBytes, 0, encoded, 2, stringBytes.length);
        
        return encoded;
    }
    
    /**
     * Encode an integer value
     */
    public static byte[] encodeInt(int value) {
        byte[] encoded = new byte[4];
        ByteUtils.writeInt(encoded, 0, value);
        return encoded;
    }
    
    /**
     * Encode a short value
     */
    public static byte[] encodeShort(short value) {
        byte[] encoded = new byte[2];
        ByteUtils.writeShort(encoded, 0, value);
        return encoded;
    }
    
    /**
     * Encode a single byte
     */
    public static byte[] encodeByte(byte value) {
        return new byte[] { value };
    }
    
    /**
     * Combine multiple byte arrays
     */
    public static byte[] combine(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] arr : arrays) {
            if (arr != null) {
                totalLength += arr.length;
            }
        }
        
        byte[] combined = new byte[totalLength];
        int offset = 0;
        
        for (byte[] arr : arrays) {
            if (arr != null) {
                System.arraycopy(arr, 0, combined, offset, arr.length);
                offset += arr.length;
            }
        }
        
        return combined;
    }
    
    /**
     * Create a complete packet with payload
     */
    public static byte[] createPacket(int packetId, byte[]... payloadParts) {
        byte[] payload = combine(payloadParts);
        return HabboProtocol.buildPacket(packetId, payload);
    }
    
    /**
     * Encode a boolean value (stored as 0 or 1)
     */
    public static byte[] encodeBoolean(boolean value) {
        return new byte[] { (byte) (value ? 1 : 0) };
    }
}
