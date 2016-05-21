package com.kerpow.games.packets.exceptions;

/**
 * Created by Steveadoo on 5/16/2016.
 */
public class MessageHandlerException extends Throwable {

    public MessageHandlerException(String message, Object identifier) {
        super(message + " for packet: " + identifier);
    }

}
