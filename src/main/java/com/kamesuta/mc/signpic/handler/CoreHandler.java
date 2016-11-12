package com.kamesuta.mc.signpic.handler;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Timer;

import com.kamesuta.mc.signpic.Apis;
import com.kamesuta.mc.signpic.Client;
import com.kamesuta.mc.signpic.Config;
import com.kamesuta.mc.signpic.entry.EntryManager;
import com.kamesuta.mc.signpic.entry.EntrySlot;
import com.kamesuta.mc.signpic.entry.content.ContentManager;
import com.kamesuta.mc.signpic.gui.OverlayFrame;
import com.kamesuta.mc.signpic.information.Informations;
import com.kamesuta.mc.signpic.plugin.packet.PacketHandler;
import com.kamesuta.mc.signpic.render.SignPicRender;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class CoreHandler {
	public final Config configHandler = Config.instance;
	public final KeyHandler keyHandler = new KeyHandler();
	public final SignHandler signHandler = new SignHandler();
	public final EntryManager signEntryManager = EntryManager.instance;
	public final ContentManager contentManager = ContentManager.instance;
	public final SignPicRender renderHandler = new SignPicRender();
	public final OverlayFrame overlayHandler = OverlayFrame.instance;
	public final Informations informationHandler = Informations.instance;
	public final Apis apiHandler = Apis.instance;

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		KeyHandler.init();
		SignHandler.init();
		this.informationHandler.init();
		this.apiHandler.init();
		PacketHandler.init();
	}

	private GuiScreen guiLater;
	public void openLater(final GuiScreen s) {
		this.guiLater = s;
	}

	@SubscribeEvent
	public void onKeyInput(final InputEvent event) {
		this.keyHandler.onKeyInput(event);
	}

	@SubscribeEvent
	public void onRenderTick(final TickEvent.RenderTickEvent event) {
		Timer.tick();
	}

	@SubscribeEvent
	public void onSign(final GuiOpenEvent event) {
		this.signHandler.onSign(event);
	}

	@SubscribeEvent
	public void onClick(final MouseEvent event) {
		this.signHandler.onClick(event);
	}

	@SubscribeEvent
	public void onRender(final RenderWorldLastEvent event) {
		this.renderHandler.onRender(event);
	}

	@SubscribeEvent()
	public void onDraw(final RenderGameOverlayEvent.Post event) {
		this.renderHandler.onDraw(event);
		this.overlayHandler.onDraw(event);
	}

	@SubscribeEvent()
	public void onDraw(final GuiScreenEvent.DrawScreenEvent.Post event) {
		this.overlayHandler.onDraw(event);
	}

	@SubscribeEvent
	public void onText(final RenderGameOverlayEvent.Text event) {
		this.renderHandler.onText(event);
	}

	@SubscribeEvent
	public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
		this.configHandler.onConfigChanged(eventArgs);
	}

	@SubscribeEvent
	public void onTick(final ClientTickEvent event) {
		if (event.phase == Phase.END) {
			if (this.guiLater!=null) {
				Client.mc.displayGuiScreen(this.guiLater);
				this.guiLater = null;
			}
			Client.startSection("signpic_load");
			debugKey();
			this.signEntryManager.onTick();
			this.contentManager.onTick();
			this.overlayHandler.onTick(event);
			this.informationHandler.onTick(event);
			EntrySlot.Tick();
			Client.endSection();
		}
	}

	boolean debugKey;

	void debugKey() {
		if (Keyboard.isKeyDown(Keyboard.KEY_I)&&Keyboard.isKeyDown(Keyboard.KEY_O)&&Keyboard.isKeyDown(Keyboard.KEY_P)) {
			if (!this.debugKey)
				debug();
			this.debugKey = true;
		} else
			this.debugKey = false;
	}

	void debug() {
		Client.openEditor();
	}
}
