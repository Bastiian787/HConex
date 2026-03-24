package com.hconex.logging;

import com.hconex.core.packets.Packet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PacketLogger {
    private static final int MAX_HISTORY = 1000;
    private final List<LogEntry> history = new CopyOnWriteArrayList<>();
    
    public void log(Packet packet) {
        LogEntry entry = new LogEntry(packet.getId(), packet.getDirection().toString(), 
                                       packet.getData(), packet.getTimestamp());
        history.add(entry);
        if (history.size() > MAX_HISTORY) history.remove(0);
    }
    
    public List<LogEntry> getHistory() { return new ArrayList<>(history); }
    public void clear() { history.clear(); }
    public int getLogCount() { return history.size(); }
}
