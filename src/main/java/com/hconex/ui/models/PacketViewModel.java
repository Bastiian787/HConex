package com.hconex.ui.models;

import com.hconex.core.packets.Packet;
import com.hconex.utils.HexUtils;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * JavaFX ViewModel wrapping a single captured {@link Packet} for display in
 * the packet log table.
 */
public final class PacketViewModel {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    private final ObjectProperty<Packet.Direction> direction;
    private final IntegerProperty headerId;
    private final StringProperty headerHex;
    private final IntegerProperty payloadLength;
    private final StringProperty hexDump;
    private final ObjectProperty<Instant> timestamp;
    private final StringProperty timestampFormatted;

    /**
     * Creates a ViewModel from a {@link Packet}.
     *
     * @param packet the packet to wrap
     */
    public PacketViewModel(Packet packet) {
        this.direction = new SimpleObjectProperty<>(packet.getDirection());
        this.headerId = new SimpleIntegerProperty(packet.getHeaderId());
        this.headerHex = new SimpleStringProperty(
                String.format("0x%04X", packet.getHeaderId()));
        this.payloadLength = new SimpleIntegerProperty(packet.getPayloadLength());
        this.hexDump = new SimpleStringProperty(HexUtils.toHexSpaced(packet.getPayload()));
        this.timestamp = new SimpleObjectProperty<>(packet.getTimestamp());
        this.timestampFormatted = new SimpleStringProperty(
                TIME_FORMAT.format(packet.getTimestamp()));
    }

    public Packet.Direction getDirection() { return direction.get(); }
    public ObjectProperty<Packet.Direction> directionProperty() { return direction; }

    public int getHeaderId() { return headerId.get(); }
    public IntegerProperty headerIdProperty() { return headerId; }

    public String getHeaderHex() { return headerHex.get(); }
    public StringProperty headerHexProperty() { return headerHex; }

    public int getPayloadLength() { return payloadLength.get(); }
    public IntegerProperty payloadLengthProperty() { return payloadLength; }

    public String getHexDump() { return hexDump.get(); }
    public StringProperty hexDumpProperty() { return hexDump; }

    public Instant getTimestamp() { return timestamp.get(); }
    public ObjectProperty<Instant> timestampProperty() { return timestamp; }

    public String getTimestampFormatted() { return timestampFormatted.get(); }
    public StringProperty timestampFormattedProperty() { return timestampFormatted; }
}
