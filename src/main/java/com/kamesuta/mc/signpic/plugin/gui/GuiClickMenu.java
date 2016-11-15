package com.kamesuta.mc.signpic.plugin.gui;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;

import com.kamesuta.mc.bnnwidget.WBase;
import com.kamesuta.mc.bnnwidget.WEvent;
import com.kamesuta.mc.bnnwidget.WPanel;
import com.kamesuta.mc.bnnwidget.position.Area;
import com.kamesuta.mc.bnnwidget.position.Coord;
import com.kamesuta.mc.bnnwidget.position.Point;
import com.kamesuta.mc.bnnwidget.position.R;
import com.kamesuta.mc.signpic.render.RenderHelper;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiClickMenu extends WPanel {
	protected WPanel panel;
	protected IKeyControllable controllable;

	public GuiClickMenu(final R position, final WPanel panel, final IKeyControllable controllable) {
		super(position);
		this.panel = panel;
		this.controllable = controllable;
	}

	@Override
	protected void initWidget() {
		this.controllable.setKeyControllable(this);
		super.initWidget();
	}

	@Override
	public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
		final Area a = getGuiPosition(pgp);
		glColor4f(0, 0, 0, .6f);
		RenderHelper.startShape();
		draw(new Area(a.x1()+4, a.y1()+4, a.x2()+1, a.y2()+1.5f), GL_QUADS);
		glPushMatrix();
		glColor4f(.85f, .85f, .85f, 1);
		draw(a, GL_QUADS);
		glLineWidth(1f);
		glColor4f(.6f, .6f, .6f, 1);
		draw(a, GL_LINE_LOOP);
		glPopMatrix();

		super.draw(ev, pgp, p, frame, popacity);
	}

	@Override
	public boolean mouseClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
		final Area a = getGuiPosition(pgp);
		if (!a.pointInside(p))
			close();
		return super.mouseClicked(ev, pgp, p, button);
	}

	@Override
	public boolean keyTyped(final WEvent ev, final Area pgp, final Point p, final char c, final int keycode) {
		if (keycode==Keyboard.KEY_ESCAPE)
			close();
		return super.keyTyped(ev, pgp, p, c, keycode);
	}

	public void close() {
		this.panel.remove(this);
		this.controllable.setKeyControllable(null);
	}

	public class ClickMenuPanel extends WBase {
		protected String text;
		protected int textcolor = 0xffffff;
		protected boolean emphasis;
		protected ResourceLocation icon;

		public ClickMenuPanel(final String text) {
			super(new R(Coord.left(1), Coord.top(3+(15*getContainer().size())), Coord.height(15), Coord.right(2.3f)));
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

		public void setIcon(final ResourceLocation resourceLocation) {
			this.icon = resourceLocation;
		}

		public ResourceLocation getIcon() {
			return this.icon;
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
			if (getIcon()!=null) {
				final Area iconArea = new Area(a.x1()+.25f, a.y1()+.25f, a.x1()+14.75f, a.y2()-.25f);
				texture().bindTexture(getIcon());
				GlStateManager.color(1, 1, 1, 1);
				RenderHelper.startTexture();
				drawTexturedModalRect(iconArea);
			}
			super.draw(ev, pgp, p, frame, popacity);
		}

		@Override
		public boolean mouseClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
			final Area a = getGuiPosition(pgp);
			if (a.pointInside(p)) {
				if (onClicked(ev, pgp, p, button))
					close();
				return true;
			}
			return super.mouseClicked(ev, pgp, p, button);
		}

		public boolean onClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
			return true;
		}
	}
}
