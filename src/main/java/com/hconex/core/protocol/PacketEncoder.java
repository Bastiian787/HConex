package com.hconex.core.protocol;

import com.hconex.core.packets.Packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Encodes {@link Packet} objects into the Habbo wire format.
 * <p>
 * Habbo packets are encoded as:
 * <pre>
 *   [4 bytes: body length (big-endian int)] = opcode (2) + payload (N)
 *   [2 bytes: opcode (big-endian short)]
 *   [N bytes: payload]
 * </pre>
 * </p>
 */
public final class PacketEncoder {

    private PacketEncoder() {
        throw new UnsupportedOperationException("PacketEncoder is a utility class");
    }

    /**
     * Encodes a {@link Packet} into its raw byte representation.
     *
     * @param packet the packet to encode
     * @return a byte array containing the full encoded packet
     */
    public static byte[] encode(Packet packet) {
        byte[] payload = packet.getPayload();
        int bodyLength = 2 + payload.length; // opcode (2) + payload

        ByteBuffer buf = ByteBuffer.allocate(4 + bodyLength)
                .order(ByteOrder.BIG_ENDIAN);
        buf.putInt(bodyLength);
        buf.putShort((short) (packet.getHeaderId() & 0xFFFF));
        buf.put(payload);

        return buf.array();
    }

    /**
     * Builds a raw Habbo packet from an opcode and a pre-serialised payload.
     *
     * @param headerId the 2-byte packet opcode
     * @param payload  the payload bytes
     * @return the encoded packet bytes
     */
    public static byte[] encode(int headerId, byte[] payload) {
        int bodyLength = 2 + payload.length;
        ByteBuffer buf = ByteBuffer.allocate(4 + bodyLength)
                .order(ByteOrder.BIG_ENDIAN);
        buf.putInt(bodyLength);
        buf.putShort((short) (headerId & 0xFFFF));
        buf.put(payload);
        return buf.array();
    }

    /**
     * Serialises a big-endian {@code int} to a 4-byte array.
     *
     * @param value the integer value to write
     * @return 4-byte representation
     */
    public static byte[] writeInt(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).array();
    }

    /**
     * Serialises a big-endian {@code short} to a 2-byte array.
     *
     * @param value the short value to write
     * @return 2-byte representation
     */
    public static byte[] writeShort(short value) {
        return ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort(value).array();
    }

    /**
     * Serialises a length-prefixed UTF-8 string.
     * <p>
     * Format: {@code [2-byte big-endian length][UTF-8 bytes]}
     * </p>
     *
     * @param value the string to serialise
     * @return the length-prefixed byte array
     */
    public static byte[] writeString(String value) {
        byte[] strBytes = value.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buf = ByteBuffer.allocate(2 + strBytes.length)
                .order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) strBytes.length);
        buf.put(strBytes);
        return buf.array();
    }
}
