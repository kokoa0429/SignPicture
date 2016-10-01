package com.kamesuta.mc.signpic.plugin.packet;
import com.kamesuta.mc.signpic.handler.CoreEvent;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class PacketHandler {
	public static FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("signpic.list");

	private final IPacketReciever recv;

	public PacketHandler(final IPacketReciever recv) {
		this.recv = recv;
	}

	public PacketHandler register() {
		channel.register(this);
		return this;
	}

	public void send(final String str) {
		final byte[] message = str.getBytes();
		final ByteBuf data = Unpooled.wrappedBuffer(message);
		final C17PacketCustomPayload packet = new C17PacketCustomPayload("signpic.list", new PacketBuffer(data));
		final FMLProxyPacket pkt = new FMLProxyPacket(packet);
		channel.sendToServer(pkt);
	}

	/**
	 * サーバーから受信したときに呼ばれる
	 */
	@CoreEvent
	public void onClientPacket(final FMLNetworkEvent.ClientCustomPacketEvent event) {
		final Packet<INetHandlerPlayServer>  packet = event.packet.toC17Packet();
		if (packet instanceof C17PacketCustomPayload){
			final C17PacketCustomPayload pluginmessage = (C17PacketCustomPayload) packet;
			final String str = new String(pluginmessage.getBufferData().array());
			this.recv.onPacket(str);
		}
	}
}