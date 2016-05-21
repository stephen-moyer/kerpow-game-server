package com.kerpow.games.server;

import com.kerpow.games.packets.PacketProcessor;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public abstract class Server<TInfo extends ServerInfo> {

    public static final AttributeKey<Player> PLAYER_ATTRIBUTE_KEY = AttributeKey.newInstance("Channel.player");

    private final PacketProcessor packetHandler;
    private final TInfo serverInfo;

    public Server(TInfo serverInfo, PacketProcessor packetHandler) {
        this.serverInfo = serverInfo;
        this.packetHandler = packetHandler;
    }

    public final void onConnect(Channel channel) {
        Player player = createPlayer(channel);
        channel.attr(PLAYER_ATTRIBUTE_KEY).set(player);
        connected(player);
    }

    public final void onDisconnect(Channel channel) {
        Player player = channel.attr(PLAYER_ATTRIBUTE_KEY).get();
        if (player == null) {
            return;
        }
        disconnected(player);
    }

    public PacketProcessor getPacketHandler() {
        return packetHandler;
    }

    public TInfo getServerInfo() {
        return serverInfo;
    }

    /**
     * Creates a Player object to attach to this channel
     * @param channel the netty channel
     * @return the created player
     */
    public abstract Player createPlayer(Channel channel);

    /**
     * Notifies the implementation that a player connected
     * @param player the player that connected
     */
    public abstract void connected(Player player);

    /**
     * Notifies the implementation that a player disconnected
     * @param player the player that disconnected
     */
    public abstract void disconnected(Player player);


}
