package com.hconex.logging;

import com.hconex.core.packets.Packet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PacketLogger {
    private static final int MAX_HISTORY = 10000;
    private static final PacketLogger INSTANCE = new PacketLogger();
    private final List<LogEntry> history = new CopyOnWriteArrayList<>();

    public static PacketLogger getInstance() {
        return INSTANCE;
    }
    
    public void log(Packet packet) {
        LogEntry entry = new LogEntry(packet.getHeaderId(), packet.getDirection(),
                                       packet.getData(), packet.getTimestamp());
        history.add(entry);
        if (history.size() > MAX_HISTORY) history.remove(0);
    }
    
    public void logIncoming(byte[] raw) {
        LogEntry entry = new LogEntry(0, Packet.Direction.INCOMING, raw, Instant.now());
        history.add(entry);
        if (history.size() > MAX_HISTORY) history.remove(0);
    }

    public void logOutgoing(byte[] raw) {
        LogEntry entry = new LogEntry(0, Packet.Direction.OUTGOING, raw, Instant.now());
        history.add(entry);
        if (history.size() > MAX_HISTORY) history.remove(0);
    }

    public List<LogEntry> getHistory() { return new ArrayList<>(history); }
    public List<LogEntry> getEntries() { return new ArrayList<>(history); }
    public void clear() { history.clear(); }
    public int getLogCount() { return history.size(); }
    public int size() { return history.size(); }
}
