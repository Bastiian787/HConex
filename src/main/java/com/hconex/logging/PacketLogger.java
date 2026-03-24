package com.hconex.logging;

import com.hconex.config.HabboConfig;
import com.hconex.core.packets.Packet;
import com.hconex.core.protocol.HabboProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Thread-safe, in-memory packet logger.
 * <p>
 * All captured packets are stored in a bounded circular list up to
 * {@link HabboConfig#LOG_HISTORY_SIZE} entries.  When the limit is reached the
 * oldest entry is discarded to make room for the new one.
 * </p>
 * <p>
 * The logger is a singleton so that the proxy layer and the UI layer can both
 * access the same log without coupling them directly.
 * </p>
 */
public final class PacketLogger {

    private static final Logger logger = LogManager.getLogger(PacketLogger.class);

    private static final class Holder {
        static final PacketLogger INSTANCE = new PacketLogger();
    }

    private final CopyOnWriteArrayList<LogEntry> entries = new CopyOnWriteArrayList<>();
    private final int maxHistorySize;

    private PacketLogger() {
        this.maxHistorySize = HabboConfig.LOG_HISTORY_SIZE;
    }

    /**
     * Returns the singleton instance.
     *
     * @return the {@link PacketLogger} singleton
     */
    public static PacketLogger getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Logs raw bytes arriving from the habbo.es server (incoming direction).
     *
     * @param raw raw bytes as received from the server
     */
    public void logIncoming(byte[] raw) {
        log(raw, Packet.Direction.INCOMING);
    }

    /**
     * Logs raw bytes sent by the Habbo client (outgoing direction).
     *
     * @param raw raw bytes as sent by the client
     */
    public void logOutgoing(byte[] raw) {
        log(raw, Packet.Direction.OUTGOING);
    }

    /**
     * Logs a pre-parsed {@link Packet}.
     *
     * @param packet the packet to log
     */
    public void log(Packet packet) {
        addEntry(LogEntry.from(packet));
    }

    /**
     * Returns an unmodifiable snapshot of all current log entries.
     *
     * @return immutable list of log entries (oldest first)
     */
    public List<LogEntry> getEntries() {
        return Collections.unmodifiableList(new ArrayList<>(entries));
    }

    /**
     * Returns all entries that match the given predicate.
     *
     * @param filter predicate to apply to each entry
     * @return filtered list of entries
     */
    public List<LogEntry> filter(Predicate<LogEntry> filter) {
        return entries.stream().filter(filter).collect(Collectors.toList());
    }

    /**
     * Returns all entries with the given direction.
     *
     * @param direction the direction to filter by
     * @return filtered list
     */
    public List<LogEntry> filterByDirection(Packet.Direction direction) {
        return filter(e -> e.getDirection() == direction);
    }

    /**
     * Returns all entries with the given header ID.
     *
     * @param headerId the opcode to filter by
     * @return filtered list
     */
    public List<LogEntry> filterByHeader(int headerId) {
        return filter(e -> e.getHeaderId() == headerId);
    }

    /**
     * Returns the total number of captured log entries.
     *
     * @return entry count
     */
    public int size() {
        return entries.size();
    }

    /**
     * Clears all log entries.
     */
    public void clear() {
        entries.clear();
        logger.info("Packet log cleared");
    }

    /**
     * Exports all log entries to the provided {@link OutputStream} as plain text.
     *
     * @param out the output stream to write to
     * @throws IOException if writing fails
     */
    public void export(OutputStream out) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(out, StandardCharsets.UTF_8))) {
            for (LogEntry entry : getEntries()) {
                writer.write(entry.toString());
                writer.newLine();
            }
        }
        logger.info("Exported {} log entries", entries.size());
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private void log(byte[] raw, Packet.Direction direction) {
        if (raw == null || raw.length < HabboProtocol.MIN_PACKET_SIZE) {
            return;
        }
        int headerId = HabboProtocol.readShort(raw, HabboProtocol.HEADER_OFFSET);
        addEntry(new LogEntry(direction, raw, headerId, Instant.now()));
    }

    private void addEntry(LogEntry entry) {
        // Trim oldest entries if at capacity
        while (entries.size() >= maxHistorySize) {
            entries.remove(0);
        }
        entries.add(entry);
        logger.debug("Logged {} packet – header=0x{} ({} bytes)",
                entry.getDirection(),
                Integer.toHexString(entry.getHeaderId()),
                entry.getDataLength());
    }
}
