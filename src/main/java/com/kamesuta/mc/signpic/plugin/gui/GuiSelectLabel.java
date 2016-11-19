package com.kamesuta.mc.signpic.plugin.gui;

import static org.lwjgl.opengl.GL11.*;

import com.kamesuta.mc.bnnwidget.WBase;
import com.kamesuta.mc.bnnwidget.WEvent;
import com.kamesuta.mc.bnnwidget.position.Area;
import com.kamesuta.mc.bnnwidget.position.Point;
import com.kamesuta.mc.bnnwidget.position.R;
import com.kamesuta.mc.signpic.render.RenderHelper;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiSelectLabel extends WBase implements Selectable {
	protected final ISelectManager selectManager;
	protected int i;
	protected boolean select;
	protected ResourceLocation resource;

	public GuiSelectLabel(final R position, final int i, final ISelectManager selectManager) {
		super(position);
		this.i = i;
		this.selectManager = selectManager;
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

	public void setResource(final ResourceLocation resource) {
		this.resource = resource;
	}

	@Override
	public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
		final Area a = getGuiPosition(pgp);
		if (!pgp.areaOverlap(a))
			return;

		if (a.pointInside(p)||this.select) {
			if (this.selectManager instanceof IToOverlaySelectManager)
				if (((IToOverlaySelectManager) this.selectManager).getMouseOver().isOpenMenu()) {
					if (this.selectManager.isActive())
						GlStateManager.color(.4f, .7f, 1, this.select ? .7f : .4f);
					else
						GlStateManager.color(.6f, .6f, .6f, .7f);
					RenderHelper.startShape();
					draw(a);
				} else {
					if (this.selectManager.isActive())
						GlStateManager.color(.4f, .7f, 1, this.select ? .7f : .4f);
					else
						GlStateManager.color(.6f, .6f, .6f, .7f);
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
		if (this.resource!=null) {
			GlStateManager.pushMatrix();
			texture().bindTexture(this.resource);
			GlStateManager.color(1, 1, 1, 1);
			RenderHelper.startTexture();
			drawTexture(a);
			GlStateManager.popMatrix();
		}

		super.draw(ev, pgp, p, frame, popacity);
	}
}
