package com.kerpow.games.server.net;

import com.kerpow.games.packets.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.logging.Logger;


public class KerpowGameDecoder extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf buf = (ByteBuf) msg;
            if (buf.isReadable()) {
                if (buf.readableBytes() >= 512) {
                    // Shouldn't happen unless there's huge lag or a player is spamming packets
                    ctx.close();
                    return;
                }
                int opcode = buf.readByte() & 0xFF;
                int length = buf.readByte();

                if (buf.readableBytes() >= length) {
                    byte[] data = new byte[length];
                    buf.readBytes(data, 0, length);
                    Packet packet = new Packet(opcode, data);
                    super.channelRead(ctx, packet);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

}
