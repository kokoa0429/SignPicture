package com.kamesuta.mc.signpic.plugin;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.kamesuta.mc.signpic.Client;
import com.kamesuta.mc.signpic.plugin.gui.GuiManager;
import com.kamesuta.mc.signpic.plugin.packet.IPacketReciever;
import com.kamesuta.mc.signpic.plugin.packet.PacketHandler;

public class Manager implements IPacketReciever {
	public static Manager instance = new Manager();

	public static Gson gson = new Gson();

	public final PacketHandler handler;

	private Manager() {
		this.handler = new PacketHandler(this).register();
	}

	@Override
	public void onPacket(final String data) {
		final SignPicturePacket packet = gson.fromJson(data, SignPicturePacket.class);
		if (packet!=null) {
			if (StringUtils.equals(packet.command, "open")) {
				Client.mc.displayGuiScreen(new GuiManager(packet.data));
			}
		}
	}

	public void sendPacket() {

	}

	public static class SignPicturePacket {
		public String command;
		public String data;

		public SignPicturePacket(final String command, final String data) {
			this.command = command;
			this.data = data;
		}
	}
}
