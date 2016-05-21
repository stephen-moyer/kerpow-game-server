package com.kerpow.games.server;

public class ServerInfo {

    public final int port;

    /**
     * Constructs a ServerInfo that will tell the server listen on the provided port
     * @param port The port to listen on
     */
    public ServerInfo(int port) {
        this.port = port;
    }

}