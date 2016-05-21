package com.kerpow.games.packets.coder.decoder;

import com.kerpow.games.packets.MessageHandler;
import com.kerpow.games.packets.exceptions.MessageHandlerException;
import com.kerpow.games.packets.exceptions.MissingHandlerException;
import com.kerpow.games.packets.Packet;
import com.kerpow.games.packets.coder.Coder;
import com.kerpow.games.packets.coder.HandlerStorage;

import java.util.List;

public class PacketDecoder extends Coder<Integer, MessageHandler> {

    public PacketDecoder(HandlerStorage<Integer, MessageHandler> storage, List<MessageHandler> handlers) {
        super(storage);
        init(handlers);
    }

    public PacketDecoder(List<MessageHandler> handlers) {
        this(Coder.<Integer, MessageHandler>getStorageForSize(handlers.size()), handlers);
    }

    private void init(List<MessageHandler> handlers) {
        for(MessageHandler handler : handlers) {
            addHandler(handler.opcode, handler);
        }
    }

    public <T> T decodePacket(Packet packet) throws MissingHandlerException, MessageHandlerException {
        MessageHandler handler = getHandler(packet.opcode);
        if (handler == null) {
            throw new MissingHandlerException(packet.opcode);
        }
        if (handler.decoder == null) {
            throw new MessageHandlerException("No decoder defined", packet.opcode);
        }
        return (T)handler.decoder.decode(packet.data);
    }

}
