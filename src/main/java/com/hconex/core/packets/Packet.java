package com.hconex.core.packets;

import java.time.Instant;

public class Packet {
    public enum Direction { INCOMING, OUTGOING }
    
    private int id;
    private byte[] data;
    private Direction direction;
    private Instant timestamp;
    
    public Packet(int id, byte[] data, Direction direction) {
        this.id = id;
        this.data = data;
        this.direction = direction;
        this.timestamp = Instant.now();
    }

    public Packet(int id, byte[] data, Direction direction, Instant timestamp) {
        this.id = id;
        this.data = data;
        this.direction = direction;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }
    
    public int getId() { return id; }
    public int getHeaderId() { return id; }
    public byte[] getData() { return data; }
    public byte[] getPayload() { return data; }
    public int getPayloadLength() { return data != null ? data.length : 0; }
    public Direction getDirection() { return direction; }
    public Instant getTimestamp() { return timestamp; }
}
