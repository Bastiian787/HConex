package com.hconex.extensions;

import com.hconex.core.packets.Packet;

/**
 * Marker interface that all HConex extensions must implement.
 * <p>
 * Extensions are loaded at runtime by {@link ExtensionLoader} and receive
 * packet events through the {@link ExtensionAPI}.
 * </p>
 */
public interface Extension {

    /**
     * Returns the display name of this extension.
     *
     * @return human-readable extension name
     */
    String getName();

    /**
     * Returns the version string of this extension.
     *
     * @return version string (e.g. {@code "1.0.0"})
     */
    String getVersion();

    /**
     * Returns a brief description of what this extension does.
     *
     * @return extension description
     */
    String getDescription();

    /**
     * Called by the framework when the extension is loaded and should start.
     *
     * @param api the {@link ExtensionAPI} instance through which the extension
     *            can interact with HConex
     */
    void onStart(ExtensionAPI api);

    /**
     * Called by the framework when the extension is about to be unloaded.
     * Clean up any resources here.
     */
    void onStop();

    /**
     * Called for every packet intercepted in the <em>incoming</em> direction
     * (server → client).
     *
     * @param packet the captured incoming packet
     */
    void onIncomingPacket(Packet packet);

    /**
     * Called for every packet intercepted in the <em>outgoing</em> direction
     * (client → server).
     *
     * @param packet the captured outgoing packet
     */
    void onOutgoingPacket(Packet packet);
}
