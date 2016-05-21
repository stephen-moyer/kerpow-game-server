package com.kerpow.games.server;

import io.netty.channel.Channel;

public abstract class Player {

    public final Channel channel;

    public Player(Channel channel) {
        this.channel = channel;
    }

}
