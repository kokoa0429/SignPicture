package com.kamesuta.mc.signpic.plugin.gui;

import com.kamesuta.mc.bnnwidget.WBox;
import com.kamesuta.mc.bnnwidget.WEvent;
import com.kamesuta.mc.bnnwidget.position.Area;
import com.kamesuta.mc.bnnwidget.position.Point;
import com.kamesuta.mc.bnnwidget.position.R;

public abstract class GuiMouseOver extends WBox {
	protected final Controllable frame;
	protected Point openMenuPoint;

	public GuiMouseOver(final R position, final Controllable frame) {
		super(position);
		this.frame = frame;
	}

	public void setOpenMenuPoint(final Point p) {
	}

	public boolean isOpenMenu() {
		return getContainer().size()!=0;
	}

	@Override
	protected void initWidget() {
		super.initWidget();
		invokeLater(new Runnable() {
			@Override
			public void run() {
				if (getContainer().size()>=1)
					getContainer().clear();
			}
		});
	}

	@Override
	public void update(final WEvent ev, final Area pgp, final Point p) {
		if (this.openMenuPoint!=null) {
			openMenu(ev, pgp, p);
			this.openMenuPoint = null;
		}
		super.update(ev, pgp, p);
	}

	protected void openMenu(final WEvent ev, final Area pgp, final Point p) {
	}

	@Override
	public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
		if (!isOpenMenu())
			drawMouseOver(ev, pgp, p, frame, popacity);
		super.draw(ev, pgp, p, frame, popacity);
	}

	protected void drawMouseOver(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
	}
}