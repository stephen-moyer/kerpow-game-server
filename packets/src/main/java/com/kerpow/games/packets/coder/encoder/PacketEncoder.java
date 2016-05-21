package com.kerpow.games.packets.coder.encoder;

import com.kerpow.games.packets.MessageHandler;
import com.kerpow.games.packets.Packet;
import com.kerpow.games.packets.coder.Coder;
import com.kerpow.games.packets.coder.HandlerStorage;
import com.kerpow.games.packets.exceptions.MessageHandlerException;
import com.kerpow.games.packets.exceptions.MissingHandlerException;

import java.util.List;

public class PacketEncoder extends Coder<Class<?>, MessageHandler> {

    public PacketEncoder(HandlerStorage<Class<?>, MessageHandler> storage, List<MessageHandler> handlers) {
        super(storage);
        init(handlers);
    }

    public PacketEncoder(List<MessageHandler> handlers) {
        this(Coder.<Class<?>, MessageHandler>getStorageForSize(handlers.size()), handlers);
    }

    private void init(List<MessageHandler> handlers) {
        for(MessageHandler handler : handlers) {
            addHandler(handler.messageType, handler);
        }
    }

    public Packet encodePacket(Object message) throws MissingHandlerException, MessageHandlerException {
        MessageHandler handler = getHandler(message.getClass());
        if (handler == null) {
            throw new MissingHandlerException(message.getClass());
        }
        if (handler.decoder == null) {
            throw new MessageHandlerException("No encoder defined", message.getClass());
        }
        //TODO implement a pool for packets
        return new Packet(handler.opcode, handler.encoder.encode(message));
    }

}
