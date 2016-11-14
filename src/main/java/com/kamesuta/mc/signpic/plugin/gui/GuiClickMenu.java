package com.kamesuta.mc.signpic.plugin.gui;

import static org.lwjgl.opengl.GL11.*;

import com.kamesuta.mc.bnnwidget.WEvent;
import com.kamesuta.mc.bnnwidget.WPanel;
import com.kamesuta.mc.bnnwidget.position.Area;
import com.kamesuta.mc.bnnwidget.position.Point;
import com.kamesuta.mc.bnnwidget.position.R;
import com.kamesuta.mc.signpic.plugin.SignData;
import com.kamesuta.mc.signpic.plugin.gui.GuiManager.GuiGallery.MouseOverPanel;
import com.kamesuta.mc.signpic.render.RenderHelper;

import net.minecraft.client.renderer.GlStateManager;

public class GuiClickMenu extends WPanel {
	protected MouseOverPanel panel;
	protected SignData data;

	public GuiClickMenu(final R position, final MouseOverPanel panel, final SignData data) {
		super(position);
		this.panel = panel;
		this.data = data;
	}

	@Override
	public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
		final Area a = getGuiPosition(pgp);
		glColor4f(0, 0, 0, .6f);
		RenderHelper.startShape();
		draw(new Area(a.x1()+4, a.y1()+4, a.x2()+1, a.y2()+1.5f), GL_QUADS);
		glColor4f(.85f, .85f, .85f, 1);
		draw(a, GL_QUADS);
		glLineWidth(1f);
		glColor4f(.6f, .6f, .6f, 1);
		draw(a, GL_LINE_LOOP);

		super.draw(ev, pgp, p, frame, popacity);
	}

	@Override
	public boolean mouseClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
		final Area a = getGuiPosition(pgp);
		if (!a.pointInside(p))
			this.panel.remove(this);
		return super.mouseClicked(ev, pgp, p, button);
	}

	public static class ClickMenuPanel extends WPanel {
		protected String text;
		protected int textcolor = 0xffffff;
		protected boolean emphasis;

		public ClickMenuPanel(final R position, final String text) {
			super(position);
			this.text = text;
			setColor(0x00000);
		}

		public void setColor(final int color) {
			this.textcolor = color;
		}

		public int getColor() {
			return this.textcolor;
		}

		public String getText() {
			return this.text;
		}

		public void setEmphasis(final boolean emphasis) {
			this.emphasis = emphasis;
		}

		public boolean isEmphasis() {
			return this.emphasis;
		}

		@Override
		public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
			final Area a = getGuiPosition(pgp);
			GlStateManager.pushMatrix();
			if (a.pointInside(p)) {
				GlStateManager.color(.7f, .7f, .7f, .7f);
				RenderHelper.startShape();
				draw(a, GL_QUADS);
			}
			GlStateManager.translate(this.emphasis ? a.minX()+2.5f : a.minX(), a.minY(), 0);
			if (this.emphasis)
				GlStateManager.scale(.84f, 1, 1);
			RenderHelper.startTexture();
			font().drawString(this.emphasis ? "§l"+getText() : getText(), 15, 2, getColor());
			GlStateManager.popMatrix();

			super.draw(ev, pgp, p, frame, popacity);
		}

		@Override
		public boolean mouseClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
			final Area a = getGuiPosition(pgp);
			if (a.pointInside(p))
				onClicked(ev, pgp, p, button);
			return super.mouseClicked(ev, pgp, p, button);
		}

		public void onClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
		}
	}
}
