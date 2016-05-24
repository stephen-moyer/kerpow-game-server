package com.kerpow.games.server;

import com.kerpow.games.packets.Packet;
import com.kerpow.games.packets.PacketProcessor;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public abstract class Server<TInfo extends ServerInfo> implements PacketProcessor.ProcessPipeline {

    public static final AttributeKey<Player> PLAYER_ATTRIBUTE_KEY = AttributeKey.newInstance("Channel.player");

    private final PacketProcessor packetProcessor;
    private final TInfo serverInfo;

    public Server(TInfo serverInfo, PacketProcessor packetProcessor) {
        this.serverInfo = serverInfo;
        this.packetProcessor = packetProcessor;
        this.packetProcessor.setProcessPipeline(this);
    }

    public Object transformSender(Object sender, Packet packet) {
        return ((Channel) sender).attr(PLAYER_ATTRIBUTE_KEY).get();
    }

    public boolean checkMessage(Object player, Packet packet, Object message) {
        return player != null;
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

    public final PacketProcessor getPacketProcessor() {
        return packetProcessor;
    }

    public final TInfo getServerInfo() {
        return serverInfo;
    }

    /**
     * Called when a player connects
     * TODO move this out into some sort of PlayerHandler?
     * @param player the player that connected
     */
    protected void connected(Player player) {
    }

    /**
     * Called when a player disconnects
     * TODO move this out into some sort of PlayerHandler?
     * @param player the player that disconnected
     */
    protected void disconnected(Player player) {
    }

    /**
     * Creates a Player object to attach to this channel
     * TODO move this out into some sort of PlayerHandler?
     * @param channel the netty channel
     * @return the created player
     */
    protected abstract Player createPlayer(Channel channel);

}
