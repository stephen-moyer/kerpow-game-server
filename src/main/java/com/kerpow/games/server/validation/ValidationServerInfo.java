package com.kerpow.games.server.validation;

import com.kerpow.games.server.ServerInfo;

public class ValidationServerInfo extends ServerInfo {

    public final int validationTimeoutMillis;

    /**
     * Constructs a ServerInfo that will tell the server listen on the provided port
     *
     * @param port The port to listen on
     * @param validationTimeoutMillis how long a player has until they send a validation message before theyre dced
     */
    public ValidationServerInfo(int port, int validationTimeoutMillis) {
        super(port);
        this.validationTimeoutMillis = validationTimeoutMillis;
    }
}
