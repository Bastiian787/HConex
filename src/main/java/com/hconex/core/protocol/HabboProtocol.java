package com.hconex.core.protocol;

import com.hconex.core.packets.Packet;
import com.hconex.core.packets.Packet.Direction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser for the Habbo Hotel wire protocol.
 * <p>
 * Habbo packets have the following structure:
 * <pre>
 *   [4 bytes: total body length (big-endian int)]
 *   [2 bytes: header/opcode (big-endian short)]
 *   [N bytes: payload]
 * </pre>
 * This class provides methods to parse raw byte arrays into {@link Packet}
 * objects for both incoming (server→client) and outgoing (client→server)
 * directions.
 * </p>
 */
public class HabboProtocol {

    private static final Logger logger = LogManager.getLogger(HabboProtocol.class);

    /** Minimum number of bytes required to read a packet header (4 length + 2 opcode). */
    public static final int MIN_PACKET_SIZE = 6;

    /** Byte offset of the length field within a raw packet. */
    public static final int LENGTH_OFFSET = 0;

    /** Byte offset of the opcode/header field within a raw packet. */
    public static final int HEADER_OFFSET = 4;

    /** Byte offset where the payload starts. */
    public static final int PAYLOAD_OFFSET = 6;

    /**
     * Parses one or more outgoing (client→server) packets from a raw byte array.
     *
     * @param raw raw bytes received from the Habbo client
     * @return list of parsed {@link Packet} objects; empty if the data is invalid
     */
    public List<Packet> parseOutgoing(byte[] raw) {
        return parse(raw, Direction.OUTGOING);
    }

    /**
     * Parses one or more incoming (server→client) packets from a raw byte array.
     *
     * @param raw raw bytes received from the habbo.es server
     * @return list of parsed {@link Packet} objects; empty if the data is invalid
     */
    public List<Packet> parseIncoming(byte[] raw) {
        return parse(raw, Direction.INCOMING);
    }

    /**
     * Parses packets from a raw byte array in the given direction.
     *
     * @param raw       raw bytes to parse
     * @param direction direction of the packets
     * @return list of parsed packets
     */
    private List<Packet> parse(byte[] raw, Direction direction) {
        List<Packet> packets = new ArrayList<>();

        if (raw == null || raw.length < MIN_PACKET_SIZE) {
            logger.debug("Received {} bytes – too small to be a valid Habbo packet", raw == null ? 0 : raw.length);
            return packets;
        }

        int offset = 0;
        while (offset + MIN_PACKET_SIZE <= raw.length) {
            ByteBuffer buf = ByteBuffer.wrap(raw, offset, raw.length - offset)
                    .order(ByteOrder.BIG_ENDIAN);

            int bodyLength = buf.getInt();           // 4-byte length field
            if (bodyLength < 2) {                    // must be at least 2 for the opcode
                logger.warn("Invalid packet body length {} at offset {}", bodyLength, offset);
                break;
            }

            int totalLength = 4 + bodyLength;
            if (offset + totalLength > raw.length) {
                logger.debug("Incomplete packet at offset {} (need {} bytes, have {})",
                        offset, totalLength, raw.length - offset);
                break;
            }

            int headerId = buf.getShort() & 0xFFFF; // 2-byte opcode

            int payloadLength = bodyLength - 2;
            byte[] payload = new byte[payloadLength];
            if (payloadLength > 0) {
                buf.get(payload);
            }

            Packet packet = PacketFactory.create(headerId, payload, direction);
            packets.add(packet);

            logger.debug("Parsed {} packet – header=0x{} ({}), payload={} bytes",
                    direction, Integer.toHexString(headerId), headerId, payloadLength);

            offset += totalLength;
        }

        return packets;
    }

    /**
     * Reads a big-endian {@code int} from four consecutive bytes starting at
     * the given offset.
     *
     * @param data   source byte array
     * @param offset starting index
     * @return the integer value
     */
    public static int readInt(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 4).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    /**
     * Reads a big-endian {@code short} from two consecutive bytes starting at
     * the given offset.
     *
     * @param data   source byte array
     * @param offset starting index
     * @return the short value (unsigned, widened to {@code int})
     */
    public static int readShort(byte[] data, int offset) {
        return ByteBuffer.wrap(data, offset, 2).order(ByteOrder.BIG_ENDIAN).getShort() & 0xFFFF;
    }

    /**
     * Reads a length-prefixed UTF-8 string starting at the given offset.
     * <p>
     * The string is preceded by a 2-byte big-endian length.
     * </p>
     *
     * @param data   source byte array
     * @param offset starting index
     * @return the decoded string
     */
    public static String readString(byte[] data, int offset) {
        int length = readShort(data, offset);
        return new String(data, offset + 2, length, java.nio.charset.StandardCharsets.UTF_8);
    }
}
