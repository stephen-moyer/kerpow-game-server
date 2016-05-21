package com.kerpow.games.packets;

import com.kerpow.games.packets.coder.decoder.PacketDecoder;
import com.kerpow.games.packets.coder.encoder.PacketEncoder;
import com.kerpow.games.packets.exceptions.MessageHandlerException;
import com.kerpow.games.packets.exceptions.MissingHandlerException;

import java.util.LinkedList;
import java.util.List;

public class PacketProcessor {

    private final PacketDecoder decoder;
    private final PacketEncoder encoder;

    private final List<ListenerWrapper> listeners;

    private PacketSenderTransformer senderTransformer;

    public PacketProcessor(PacketDecoder decoder, PacketEncoder encoder) {
        this.listeners = new LinkedList<>();
        this.decoder = decoder;
        this.encoder = encoder;
    }

    public PacketProcessor(List<MessageHandler> handlers) {
        this(new PacketDecoder(handlers), new PacketEncoder(handlers));
    }

    public <T> T decodePacket(Packet packet) throws MissingHandlerException, MessageHandlerException {
        return decoder.decodePacket(packet);
    }

    public Packet encodeMessage(Object message) throws MissingHandlerException, MessageHandlerException {
        return encoder.encodePacket(message);
    }

    /**
     * Processes a received packet
     * @param sender The sender(in Netty, this is the Channel)
     * @param packet The packet received
     * @throws MissingHandlerException If there is no message handler for this packet id
     * @throws MessageHandlerException If there is an error with the message handler for this packet id
     */
    public void processPacket(Object sender, Packet packet) throws MissingHandlerException, MessageHandlerException {
        Object message = decodePacket(packet);
        if (senderTransformer != null) {
            //if the sender transformer returns null then its an invalid session
            if ((sender = senderTransformer.checkAndGet(sender, packet)) == null) {
                return;
            }
        }
        for (ListenerWrapper wrapper : listeners) {
            if (message.getClass().equals(wrapper.clazz)) {
                wrapper.listener.received(sender, message);
            }
        }
    }

    public <TSender, TMessage> void addListener(Class<TMessage> mClass, PacketListener<TSender, TMessage> listener) {
        listeners.add(new ListenerWrapper(mClass, listener));
    }

    /**
     * Transforms the sender object into a new object.
     * TODO probably want to implement some sort of pipeline like netty does?
     * @param transformer the transformer
     */
    public void setSenderTransformer(PacketSenderTransformer transformer) {
        this.senderTransformer = transformer;
    }

    private class ListenerWrapper {

        public final Class<?> clazz;
        public final PacketListener listener;

        public ListenerWrapper(Class<?> clazz, PacketListener listener) {
            this.clazz = clazz;
            this.listener = listener;
        }

    }

    public interface PacketListener<TSender, TMessage> {

        void received(TSender sender, TMessage message);

    }

    /**
     * WHAT SHOULD I NAME THIS :(
     */
    public interface PacketSenderTransformer<TSender, TPacket extends Packet> {

        /**
         * Changes the sender to another type. In Netty we use this to map the Channel to an internal player instance
         * @param sender The sender
         * @param packet The packet, we check the packet id rather than go through and decode the packet
         * @return null if you want the processor to discard the packet and not raise anything(we've used this for an invalid session), otherwise the mapped instance
         */
        Object checkAndGet(TSender sender, TPacket packet);

    }

}
