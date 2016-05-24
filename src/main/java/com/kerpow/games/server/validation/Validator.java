package com.kerpow.games.server.validation;

import com.kerpow.games.server.Player;
import com.kerpow.games.server.Server;

import java.util.concurrent.CompletableFuture;

public interface Validator {

    void init();

    /**
     * Checks if this validator uses this opcode for validation
     * @param opcode the opcode of the message to check
     * @return true if you want to use this message to validate the player
     */
    boolean matches(int opcode);

    /**
     * Starts validating the player using the message
     * @param player The player to validate
     * @param opcode The opcode of this message
     * @param message The decoded message
     */
    CompletableFuture<Boolean> validate(Player player, int opcode, Object message);

}
