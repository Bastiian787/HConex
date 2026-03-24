package com.hconex.logging;

import java.time.LocalDateTime;

public class LogEntry {
    private int packetId;
    private String direction;
    private byte[] data;
    private LocalDateTime timestamp;
    
    public LogEntry(int packetId, String direction, byte[] data, LocalDateTime timestamp) {
        this.packetId = packetId;
        this.direction = direction;
        this.data = data;
        this.timestamp = timestamp;
    }
    
    public int getPacketId() { return packetId; }
    public String getDirection() { return direction; }
    public byte[] getData() { return data; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
