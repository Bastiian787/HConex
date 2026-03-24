package com.hconex.protocol;

import com.hconex.core.packets.Packet;
import com.hconex.core.protocol.HabboProtocol;
import com.hconex.core.protocol.PacketEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link HabboProtocol}.
 */
@DisplayName("HabboProtocol")
class HabboProtocolTest {

    private HabboProtocol protocol;

    @BeforeEach
    void setUp() {
        protocol = new HabboProtocol();
    }

    @Test
    @DisplayName("parseIncoming returns empty list for null input")
    void parseIncoming_null_returnsEmpty() {
        List<Packet> result = protocol.parseIncoming(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("parseIncoming returns empty list for too-short input")
    void parseIncoming_tooShort_returnsEmpty() {
        List<Packet> result = protocol.parseIncoming(new byte[]{0x00, 0x01});
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("parseIncoming correctly parses a single well-formed packet")
    void parseIncoming_singlePacket_parsesCorrectly() {
        // Build a packet: bodyLen=4 (opcode 2 + payload 2), opcode=0x00FF, payload=0xAA 0xBB
        byte[] raw = PacketEncoder.encode(0x00FF, new byte[]{(byte) 0xAA, (byte) 0xBB});

        List<Packet> packets = protocol.parseIncoming(raw);

        assertEquals(1, packets.size());
        Packet p = packets.get(0);
        assertEquals(0x00FF, p.getHeaderId());
        assertEquals(2, p.getPayloadLength());
        assertEquals(Packet.Direction.INCOMING, p.getDirection());
    }

    @Test
    @DisplayName("parseOutgoing sets direction to OUTGOING")
    void parseOutgoing_setsDirectionOutgoing() {
        byte[] raw = PacketEncoder.encode(0x0042, new byte[0]);
        List<Packet> packets = protocol.parseOutgoing(raw);

        assertEquals(1, packets.size());
        assertEquals(Packet.Direction.OUTGOING, packets.get(0).getDirection());
    }

    @Test
    @DisplayName("parseIncoming parses two back-to-back packets")
    void parseIncoming_twoPackets_parsedBoth() {
        byte[] pkt1 = PacketEncoder.encode(0x0001, new byte[]{0x01});
        byte[] pkt2 = PacketEncoder.encode(0x0002, new byte[]{0x02, 0x03});
        byte[] combined = new byte[pkt1.length + pkt2.length];
        System.arraycopy(pkt1, 0, combined, 0, pkt1.length);
        System.arraycopy(pkt2, 0, combined, pkt1.length, pkt2.length);

        List<Packet> packets = protocol.parseIncoming(combined);

        assertEquals(2, packets.size());
        assertEquals(0x0001, packets.get(0).getHeaderId());
        assertEquals(0x0002, packets.get(1).getHeaderId());
    }

    @Test
    @DisplayName("readInt reads big-endian int correctly")
    void readInt_bigEndian() {
        byte[] data = {0x00, 0x00, 0x00, 0x0A};
        assertEquals(10, HabboProtocol.readInt(data, 0));
    }

    @Test
    @DisplayName("readShort reads big-endian short correctly")
    void readShort_bigEndian() {
        byte[] data = {0x00, (byte) 0xFF};
        assertEquals(255, HabboProtocol.readShort(data, 0));
    }
}
