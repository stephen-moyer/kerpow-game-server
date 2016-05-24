package com.kerpow.games.server.net;

import com.kerpow.games.packets.exceptions.MessageHandlerException;
import com.kerpow.games.packets.exceptions.MissingHandlerException;
import com.kerpow.games.server.Server;
import com.kerpow.games.packets.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class KerpowServerHandler extends SimpleChannelInboundHandler<Packet> {

	private static Logger logger = Logger.getLogger("KerpowServerHandler");

	private final Server server;

	public KerpowServerHandler(Server server) {
		this.server = server;
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) {
		server.onConnect(ctx.channel());
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) {
		server.onDisconnect(ctx.channel());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
		try {
			server.getPacketProcessor().processPacket(channelHandlerContext.channel(), packet);
		} catch (MissingHandlerException | MessageHandlerException e) {
			logger.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		}
	}
}
