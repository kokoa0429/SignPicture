package com.kamesuta.mc.signpic.plugin.packet;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.kamesuta.mc.signpic.Client;
import com.kamesuta.mc.signpic.plugin.gui.GuiManager;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class PacketHandler {
	public static PacketHandler instance = new PacketHandler();

	public static Gson gson = new Gson();

	public static FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("signpic.list");

	private PacketHandler() {
	}

	public static void init() {
		channel.register(instance);
	}

	/**
	 * サーバーから受信したときに呼ばれる
	 */
	@SubscribeEvent
	public void onClientPacket(final FMLNetworkEvent.ClientCustomPacketEvent event) {
		final Packet<INetHandlerPlayServer>  packet = event.packet.toC17Packet();
		if (packet instanceof C17PacketCustomPayload){
			final C17PacketCustomPayload pluginmessage = (C17PacketCustomPayload) packet;
			final String str = new String(pluginmessage.getBufferData().array());
			onPacket(str);
		}
	}

	public void onPacket(final String data) {
		final SignPicturePacket packet = gson.fromJson(data, SignPicturePacket.class);
		if (packet!=null) {
			if (StringUtils.equals(packet.command, "open")) {
				Client.handler.openLater(new GuiManager(packet.token, packet.data));
			} else if (StringUtils.equals(packet.command, "data")) {
				if (Client.mc.currentScreen instanceof GuiManager)
					((GuiManager) Client.mc.currentScreen).data(packet.token, packet.data);
			}
		}
	}

	public void sendPacket(final SignPicturePacket p) {
		final byte[] message = gson.toJson(p).getBytes();
		final ByteBuf data = Unpooled.wrappedBuffer(message);
		final C17PacketCustomPayload packet = new C17PacketCustomPayload("signpic.list", new PacketBuffer(data));
		final FMLProxyPacket pkt = new FMLProxyPacket(packet);
		channel.sendToServer(pkt);
	}

	public static class SignPicturePacket {
		public String command;
		public String token;
		public String data;

		public SignPicturePacket(final String command, final String token, final String data) {
			this.command = command;
			this.token = token;
			this.data = data;
		}
	}
}