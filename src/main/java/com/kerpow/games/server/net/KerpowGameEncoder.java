package com.kerpow.games.server.net;

import com.google.protobuf.GeneratedMessage;
import com.kerpow.games.packets.Packet;
import com.kerpow.games.packets.PacketProcessor;
import com.kerpow.games.packets.exceptions.MessageHandlerException;
import com.kerpow.games.packets.exceptions.MissingHandlerException;
import com.kerpow.games.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.logging.Level;
import java.util.logging.Logger;

public class KerpowGameEncoder extends MessageToByteEncoder<GeneratedMessage> {

    private static Logger logger = Logger.getLogger("KerpowGameEncoder");

    private final Server server;

    public KerpowGameEncoder(Server server) {
        this.server = server;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final GeneratedMessage msg, final ByteBuf out) throws Exception {
        try {
            Packet packet = server.getPacketProcessor().encodeMessage(msg);
            out.writeByte(packet.opcode);
            out.writeByte(packet.length);
            if (packet.length > 0)
                out.writeBytes(packet.data);
        } catch (MissingHandlerException | MessageHandlerException e) {
            logger.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
        }
    }

}
