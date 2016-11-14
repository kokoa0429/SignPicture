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

}
