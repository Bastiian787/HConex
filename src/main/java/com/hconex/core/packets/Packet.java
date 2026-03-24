package com.hconex.core.packets;

import java.time.Instant;
import java.util.Arrays;

/**
 * Base class representing a single Habbo Hotel protocol packet.
 * <p>
 * Every packet has:
 * <ul>
 *   <li>a numeric header ID (opcode)</li>
 *   <li>a raw payload (bytes following the opcode)</li>
 *   <li>a direction (incoming from server, or outgoing from client)</li>
 *   <li>a UTC timestamp recording when the packet was captured</li>
 * </ul>
 * </p>
 */
public class Packet {

    /**
     * Indicates the direction of travel for a packet.
     */
    public enum Direction {
        /** Packet sent by the server to the client (server → client). */
        INCOMING,
        /** Packet sent by the client to the server (client → server). */
        OUTGOING
    }

    private final int headerId;
    private final byte[] payload;
    private final Direction direction;
    private final Instant timestamp;

    /**
     * Creates a new Packet.
     *
     * @param headerId  the 2-byte packet opcode / header ID
     * @param payload   the raw payload bytes (may be empty, must not be {@code null})
     * @param direction direction of the packet
     */
    public Packet(int headerId, byte[] payload, Direction direction) {
        this.headerId = headerId;
        this.payload = Arrays.copyOf(payload, payload.length);
        this.direction = direction;
        this.timestamp = Instant.now();
    }

    /**
     * Returns the header ID (opcode) of this packet.
     *
     * @return packet header ID
     */
    public int getHeaderId() {
        return headerId;
    }

    /**
     * Returns a copy of the raw payload bytes.
     *
     * @return payload bytes
     */
    public byte[] getPayload() {
        return Arrays.copyOf(payload, payload.length);
    }

    /**
     * Returns the length of the payload in bytes.
     *
     * @return payload length
     */
    public int getPayloadLength() {
        return payload.length;
    }

    /**
     * Returns the direction this packet was travelling when captured.
     *
     * @return packet direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Returns the UTC timestamp at which this packet was captured.
     *
     * @return capture timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Returns {@code true} if this packet originated from the server.
     *
     * @return {@code true} for incoming packets
     */
    public boolean isIncoming() {
        return direction == Direction.INCOMING;
    }

    /**
     * Returns {@code true} if this packet originated from the client.
     *
     * @return {@code true} for outgoing packets
     */
    public boolean isOutgoing() {
        return direction == Direction.OUTGOING;
    }

    @Override
    public String toString() {
        return String.format("Packet{header=0x%04X (%d), direction=%s, payloadLen=%d, ts=%s}",
                headerId, headerId, direction, payload.length, timestamp);
    }
}
