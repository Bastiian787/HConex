package com.hconex.logging;

import com.hconex.core.packets.Packet;

import java.time.Instant;

public class LogEntry {
    private int headerId;
    private Packet.Direction direction;
    private byte[] rawData;
    private Instant timestamp;
    
    public LogEntry(int headerId, Packet.Direction direction, byte[] rawData, Instant timestamp) {
        this.headerId = headerId;
        this.direction = direction;
        this.rawData = rawData;
        this.timestamp = timestamp;
    }
    
    public int getPacketId() { return headerId; }
    public int getHeaderId() { return headerId; }
    public Packet.Direction getDirection() { return direction; }
    public byte[] getData() { return rawData; }
    public byte[] getRawData() { return rawData; }
    public Instant getTimestamp() { return timestamp; }
}
