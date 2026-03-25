package com.hconex.core.protocol;

import com.hconex.utils.ByteUtils;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Habbo Protocol Parser
 * Handles parsing and encoding of Habbo packets
 */
public class HabboProtocol {
    
    public static final int HEADER_SIZE = 4;
    public static final int PACKET_ID_SIZE = 2;

    public List<com.hconex.core.packets.Packet> parseIncoming(byte[] data) {
        return parsePackets(data, com.hconex.core.packets.Packet.Direction.INCOMING);
    }

    public List<com.hconex.core.packets.Packet> parseOutgoing(byte[] data) {
        return parsePackets(data, com.hconex.core.packets.Packet.Direction.OUTGOING);
    }

    private List<com.hconex.core.packets.Packet> parsePackets(
            byte[] data,
            com.hconex.core.packets.Packet.Direction direction) {
        List<com.hconex.core.packets.Packet> packets = new ArrayList<>();
        if (data == null || data.length < HEADER_SIZE + PACKET_ID_SIZE) {
            return packets;
        }

        int offset = 0;
        while (offset + HEADER_SIZE + PACKET_ID_SIZE <= data.length) {
            int packetLength = ByteUtils.readInt(data, offset);
            if (packetLength < PACKET_ID_SIZE) {
                break;
            }

            int packetEnd = offset + HEADER_SIZE + packetLength;
            if (packetEnd > data.length) {
                break;
            }

            int packetId = ByteUtils.readShort(data, offset + HEADER_SIZE);
            int payloadLength = packetLength - PACKET_ID_SIZE;
            byte[] payload = new byte[Math.max(payloadLength, 0)];
            if (payloadLength > 0) {
                System.arraycopy(
                        data,
                        offset + HEADER_SIZE + PACKET_ID_SIZE,
                        payload,
                        0,
                        payloadLength);
            }

            packets.add(new com.hconex.core.packets.Packet(packetId, payload, direction));
            offset = packetEnd;
        }

        return packets;
    }
    
    /**
     * Parse packet header to get packet length
     */
    public static int getPacketLength(byte[] data) {
        if (data == null || data.length < HEADER_SIZE) {
            return -1;
        }
        return ByteUtils.readInt(data, 0);
    }
    
    /**
     * Parse packet ID from data
     */
    public static int getPacketId(byte[] data) {
        if (data == null || data.length < HEADER_SIZE + PACKET_ID_SIZE) {
            return -1;
        }
        return ByteUtils.readShort(data, HEADER_SIZE);
    }
    
    /**
     * Get packet payload (data after header and ID)
     */
    public static byte[] getPacketPayload(byte[] data) {
        if (data == null || data.length <= HEADER_SIZE + PACKET_ID_SIZE) {
            return new byte[0];
        }
        
        byte[] payload = new byte[data.length - HEADER_SIZE - PACKET_ID_SIZE];
        System.arraycopy(data, HEADER_SIZE + PACKET_ID_SIZE, payload, 0, payload.length);
        return payload;
    }
    
    /**
     * Read a string from packet payload at offset
     */
    public static String readString(byte[] payload, int offset) {
        if (payload == null || offset < 0 || offset >= payload.length) {
            return "";
        }
        
        // Read string length (2 bytes)
        int length = ByteUtils.readShort(payload, offset);
        if (length <= 0 || offset + 2 + length > payload.length) {
            return "";
        }
        
        return new String(payload, offset + 2, length);
    }
    
    /**
     * Read an integer from payload at offset
     */
    public static int readInt(byte[] payload, int offset) {
        if (payload == null || offset + 4 > payload.length) {
            return 0;
        }
        return ByteUtils.readInt(payload, offset);
    }
    
    /**
     * Read a short from payload at offset
     */
    public static short readShort(byte[] payload, int offset) {
        if (payload == null || offset + 2 > payload.length) {
            return 0;
        }
        return ByteUtils.readShort(payload, offset);
    }
    
    /**
     * Read a byte from payload at offset
     */
    public static byte readByte(byte[] payload, int offset) {
        if (payload == null || offset >= payload.length) {
            return 0;
        }
        return payload[offset];
    }
    
    /**
     * Check if packet is complete (has full length)
     */
    public static boolean isPacketComplete(byte[] data) {
        if (data == null || data.length < HEADER_SIZE) {
            return false;
        }
        
        int packetLength = getPacketLength(data);
        return data.length >= packetLength + HEADER_SIZE;
    }
    
    /**
     * Build a complete packet (header + id + payload)
     */
    public static byte[] buildPacket(int packetId, byte[] payload) {
        if (payload == null) {
            payload = new byte[0];
        }
        
        int totalLength = PACKET_ID_SIZE + payload.length;
        byte[] packet = new byte[HEADER_SIZE + totalLength];
        
        // Write length header
        ByteUtils.writeInt(packet, 0, totalLength);
        
        // Write packet ID
        ByteUtils.writeShort(packet, HEADER_SIZE, (short) packetId);
        
        // Write payload
        if (payload.length > 0) {
            System.arraycopy(payload, 0, packet, HEADER_SIZE + PACKET_ID_SIZE, payload.length);
        }
        
        return packet;
    }
    
    /**
     * Get human-readable packet info
     */
    public static String getPacketInfo(byte[] data) {
        if (data == null || data.length < HEADER_SIZE + PACKET_ID_SIZE) {
            return "Invalid packet";
        }
        
        int length = getPacketLength(data);
        int id = getPacketId(data);
        int payloadSize = data.length - HEADER_SIZE - PACKET_ID_SIZE;
        
        return String.format("Length: %d, ID: %d (0x%04X), Payload: %d bytes", 
                             length, id, id, payloadSize);
    }
}
