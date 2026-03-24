package com.hconex.core.protocol;

/**
 * Factory for creating and identifying Habbo packets
 */
public class PacketFactory {
    
    // Common Habbo packet IDs (these are examples)
    public static final int HANDSHAKE = 4000;
    public static final int AUTH_TICKET = 4001;
    public static final int AUTH_RESPONSE = 4002;
    public static final int USER_OBJECT = 4004;
    public static final int ROOM_READY = 4007;
    public static final int CHAT_MESSAGE = 4009;
    public static final int MOVE_AVATAR = 4010;
    public static final int OBJECT_UPDATE = 4012;
    public static final int DISCONNECT = 4013;
    
    /**
     * Get packet name from ID
     */
    public static String getPacketName(int packetId) {
        switch (packetId) {
            case HANDSHAKE:
                return "Handshake";
            case AUTH_TICKET:
                return "AuthTicket";
            case AUTH_RESPONSE:
                return "AuthResponse";
            case USER_OBJECT:
                return "UserObject";
            case ROOM_READY:
                return "RoomReady";
            case CHAT_MESSAGE:
                return "ChatMessage";
            case MOVE_AVATAR:
                return "MoveAvatar";
            case OBJECT_UPDATE:
                return "ObjectUpdate";
            case DISCONNECT:
                return "Disconnect";
            default:
                return String.format("Unknown (0x%04X)", packetId);
        }
    }
    
    /**
     * Check if packet is incoming (server to client)
     */
    public static boolean isIncoming(int packetId) {
        return packetId >= 4000 && packetId < 5000;
    }
    
    /**
     * Check if packet is outgoing (client to server)
     */
    public static boolean isOutgoing(int packetId) {
        return packetId >= 3000 && packetId < 4000;
    }
    
    /**
     * Get packet category
     */
    public static String getPacketCategory(int packetId) {
        if (packetId >= 4000 && packetId < 4100) {
            return "Connection";
        } else if (packetId >= 4100 && packetId < 4200) {
            return "Chat";
        } else if (packetId >= 4200 && packetId < 4300) {
            return "Avatar";
        } else if (packetId >= 4300 && packetId < 4400) {
            return "Room";
        } else if (packetId >= 3000 && packetId < 3100) {
            return "Auth";
        }
        return "Other";
    }
    
    /**
     * Create packet info string
     */
    public static String createPacketInfo(int packetId, byte[] data) {
        String name = getPacketName(packetId);
        String category = getPacketCategory(packetId);
        int size = data != null ? data.length : 0;
        
        return String.format("[%s] %s (ID: 0x%04X, Size: %d bytes)", 
                             category, name, packetId, size);
    }
}
