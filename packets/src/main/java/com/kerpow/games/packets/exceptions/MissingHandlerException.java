package com.kerpow.games.packets.exceptions;

/**
 * Created by Steveadoo on 5/16/2016.
 */
public class MissingHandlerException extends Throwable {

    public MissingHandlerException(Object identifier) {
        super("No handler for identifier: " + identifier);
    }
}
