package com.kamesuta.mc.signpic.plugin.gui;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Mouse;

import com.kamesuta.mc.bnnwidget.WEvent;
import com.kamesuta.mc.bnnwidget.position.Area;
import com.kamesuta.mc.bnnwidget.position.Point;
import com.kamesuta.mc.bnnwidget.position.R;
import com.kamesuta.mc.signpic.entry.EntryId;
import com.kamesuta.mc.signpic.gui.SignPicLabel;
import com.kamesuta.mc.signpic.plugin.SignData;
import com.kamesuta.mc.signpic.plugin.gui.GuiGalleryMouseOver.ISignPicData;
import com.kamesuta.mc.signpic.plugin.packet.PacketHandler;
import com.kamesuta.mc.signpic.plugin.packet.PacketHandler.SignPicturePacket;
import com.kamesuta.mc.signpic.render.RenderHelper;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class GuiGallaryLabel extends SignPicLabel implements Selectable, ISignPicData {
	protected final IToOverlaySelectManager selectManager;
	protected final GuiGalleryMouseOver mouseOver;
	protected final GuiManager manager;
	protected int i;
	protected boolean select;

	public GuiGallaryLabel(final R position, final int i, final IToOverlaySelectManager selectManager, final GuiManager gui) {
		super(position);
		this.i = i;
		this.selectManager = selectManager;
		this.mouseOver = (GuiGalleryMouseOver) this.selectManager.getMouseOver();
		this.manager = gui;
	}

	@Override
	public void setNumber(final int i) {
		this.i = i;
	}

	@Override
	public int getNumber() {
		return this.i;
	}

	@Override
	public void select(final boolean select) {
		this.select = select;
	}

	@Override
	public boolean isSelect() {
		return this.select;
	}

	public boolean isDefault() {
		return this.i==0;
	}

	@Override
	public SignData getData() {
		return this.manager.data.get(this.i-1);
	}

	@Override
	public void onAdded() {
		if (isDefault())
			setEntryId(new EntryId("!signpic:textures/logo.png[]"));
		PacketHandler.instance.sendPacket(new SignPicturePacket("data", this.manager.key, Integer.toString(this.i)));
	}

	@Override
	public void update(final WEvent ev, final Area pgp, final Point p) {
		final Area a = getGuiPosition(pgp);
		final SignData e = this.manager.data.get(this.i-1);
		if (this.entryId==null&&e!=null)
			setEntryId(new EntryId(e.sign));

		if (a.pointInside(p)) {
			this.selectManager.setLabelsMouseInside(true);
			if (isDefault())
				this.mouseOver.setSignPicData(null);
			if (e!=null)
				this.mouseOver.setSignPicData(this);
		}

		if (this.selectManager.getSelectArea()!=null)
			this.selectManager.getSelectables().put(this, this.selectManager.getSelectArea().areaOverlap(a));
	}

	@Override
	public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float opacity) {
		final Area a = getGuiPosition(pgp);
		if (!pgp.areaOverlap(a))
			return;
		super.draw(ev, pgp, p, frame, opacity);

		if (!isDefault()) {
			if (this.select||Mouse.isInsideWindow()) {
				if ((a.pointInside(p)&&!this.mouseOver.isOpenMenu())||this.select) {
					if (this.selectManager.isActive())
						GlStateManager.color(.4f, .7f, 1, this.select ? .6f : .4f);
					else
						GlStateManager.color(.5f, .5f, .5f, .7f);
					RenderHelper.startShape();
					draw(a);
				}
			}
			if ((this.select||this.selectManager.getLastSelect()==this.i)&&this.selectManager.isActive()) {
				glLineWidth(1);
				GlStateManager.color(.4f, .7f, 1, .8f);
				RenderHelper.startShape();
				draw(a, GL_LINE_LOOP);
			}
		}
	}

	@Override
	public boolean mouseClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
		final Area a = getGuiPosition(pgp);
		if (a.pointInside(p)) {
			if (button<=1&&!this.mouseOver.isOpenMenu()) {
				if (!this.select) {
					if (!GuiScreen.isShiftKeyDown()) {
						if (!GuiScreen.isCtrlKeyDown())
							this.selectManager.selectAll(false);
					} else
						this.selectManager.selectSoFar(this.i);
					if (!isDefault()) {
						this.selectManager.getSelectables().put(this, true);
						this.selectManager.setLastSelect(this.i);
					}
				} else {
					if (!GuiScreen.isCtrlKeyDown()) {
						this.selectManager.select(this.i);
						this.selectManager.setLastSelect(this.i);
					} else
						this.selectManager.getSelectables().put(this, false);
				}
			}
			if (button==1)
				this.mouseOver.setOpenMenuPoint(p);
		}
		return super.mouseClicked(ev, pgp, p, button)||a.pointInside(p);
	}
}