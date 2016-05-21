package com.kerpow.games.server.net;

import com.google.protobuf.GeneratedMessage;
import com.kerpow.games.packets.Packet;
import com.kerpow.games.packets.PacketProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.logging.Logger;

public class KerpowGameEncoder extends MessageToByteEncoder<GeneratedMessage> {

    private static Logger logger = Logger.getAnonymousLogger();

    private final PacketProcessor packetHandler;

    public KerpowGameEncoder(PacketProcessor packetHandler) {
        this.packetHandler = packetHandler;
    }

    @Override
    protected void encode(final ChannelHandlerContext ctx, final GeneratedMessage msg, final ByteBuf out) throws Exception {
        Packet packet = packetHandler.encodeMessage(msg);
        out.writeByte(packet.opcode);
        out.writeByte(packet.length);
        if (packet.length > 0)
            out.writeBytes(packet.data);
    }

}
