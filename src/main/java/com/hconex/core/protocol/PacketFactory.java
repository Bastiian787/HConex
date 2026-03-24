package com.hconex.core.protocol;

import com.hconex.core.packets.Packet;
import com.hconex.core.packets.Packet.Direction;

/**
 * Factory for creating {@link Packet} instances.
 * <p>
 * As the project matures, this factory can return specialised subclasses
 * of {@link Packet} based on the header ID and direction.  For now it always
 * returns a base {@link Packet}.
 * </p>
 */
public final class PacketFactory {

    private PacketFactory() {
        throw new UnsupportedOperationException("PacketFactory is a utility class");
    }

    /**
     * Creates an appropriate {@link Packet} object for the given parameters.
     *
     * @param headerId  packet opcode
     * @param payload   raw payload bytes
     * @param direction direction of the packet
     * @return a new {@link Packet} instance
     */
    public static Packet create(int headerId, byte[] payload, Direction direction) {
        // Future: switch on headerId / direction to return typed subclasses
        return new Packet(headerId, payload, direction);
    }

    /**
     * Creates an outgoing (client→server) {@link Packet}.
     *
     * @param headerId packet opcode
     * @param payload  raw payload bytes
     * @return a new outgoing {@link Packet}
     */
    public static Packet createOutgoing(int headerId, byte[] payload) {
        return create(headerId, payload, Direction.OUTGOING);
    }

    /**
     * Creates an incoming (server→client) {@link Packet}.
     *
     * @param headerId packet opcode
     * @param payload  raw payload bytes
     * @return a new incoming {@link Packet}
     */
    public static Packet createIncoming(int headerId, byte[] payload) {
        return create(headerId, payload, Direction.INCOMING);
    }
}
