package com.hconex.logging;

import com.hconex.core.packets.Packet;

import java.time.Instant;

/**
 * Represents a single entry in the HConex packet log.
 * <p>
 * Each log entry captures the direction of the packet, its raw bytes,
 * the resolved packet opcode, and the timestamp at which it was captured.
 * </p>
 */
public final class LogEntry {

    private final Packet.Direction direction;
    private final byte[] rawData;
    private final int headerId;
    private final Instant timestamp;

    /**
     * Creates a new log entry from a captured packet.
     *
     * @param direction direction of the packet
     * @param rawData   raw bytes as received on the wire (full packet, including length header)
     * @param headerId  packet opcode extracted from the data
     * @param timestamp time at which the packet was captured
     */
    public LogEntry(Packet.Direction direction, byte[] rawData, int headerId, Instant timestamp) {
        this.direction = direction;
        this.rawData = java.util.Arrays.copyOf(rawData, rawData.length);
        this.headerId = headerId;
        this.timestamp = timestamp;
    }

    /**
     * Convenience factory that creates a log entry from a {@link Packet}.
     *
     * @param packet the packet to log
     * @return a new {@link LogEntry}
     */
    public static LogEntry from(Packet packet) {
        return new LogEntry(
                packet.getDirection(),
                packet.getPayload(),
                packet.getHeaderId(),
                packet.getTimestamp());
    }

    /**
     * Returns the direction of this log entry.
     *
     * @return packet direction
     */
    public Packet.Direction getDirection() {
        return direction;
    }

    /**
     * Returns a copy of the raw bytes captured for this entry.
     *
     * @return raw bytes
     */
    public byte[] getRawData() {
        return java.util.Arrays.copyOf(rawData, rawData.length);
    }

    /**
     * Returns the packet opcode.
     *
     * @return header ID
     */
    public int getHeaderId() {
        return headerId;
    }

    /**
     * Returns the capture timestamp.
     *
     * @return timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the number of raw bytes stored in this entry.
     *
     * @return byte count
     */
    public int getDataLength() {
        return rawData.length;
    }

    @Override
    public String toString() {
        return String.format("LogEntry{direction=%s, header=0x%04X (%d), bytes=%d, ts=%s}",
                direction, headerId, headerId, rawData.length, timestamp);
    }
}
