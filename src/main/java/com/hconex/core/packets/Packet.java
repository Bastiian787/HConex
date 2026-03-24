package com.hconex.core.packets;

import java.time.LocalDateTime;

public class Packet {
    public enum Direction { INCOMING, OUTGOING }
    
    private int id;
    private byte[] data;
    private Direction direction;
    private LocalDateTime timestamp;
    
    public Packet(int id, byte[] data, Direction direction) {
        this.id = id;
        this.data = data;
        this.direction = direction;
        this.timestamp = LocalDateTime.now();
    }
    
    public int getId() { return id; }
    public byte[] getData() { return data; }
    public Direction getDirection() { return direction; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
