package com.kamesuta.mc.signpic.plugin.gui;

import com.kamesuta.mc.bnnwidget.WEvent;
import com.kamesuta.mc.bnnwidget.WPanel;
import com.kamesuta.mc.bnnwidget.position.Area;
import com.kamesuta.mc.bnnwidget.position.Coord;
import com.kamesuta.mc.bnnwidget.position.Point;
import com.kamesuta.mc.bnnwidget.position.R;
import com.kamesuta.mc.signpic.plugin.SignData;

public class GuiDataClickMenu extends GuiClickMenu {
	private final SignData data;

	public GuiDataClickMenu(final R position, final WPanel panel, final SignData data) {
		super(position, panel);
		this.data = data;
	}

	public SignData getData() {
		return this.data;
	}

	@Override
	protected void initWidget() {
		add(new ClickMenuPanel(new R(Coord.left(1), Coord.top(3), Coord.height(15), Coord.width(77.7f)), "sushi") {
			{
				setEmphasis(true);
			}

			@Override
			public void onClicked(final WEvent ev, final Area pgp, final Point p, final int button) {

			}
		});

		add(new ClickMenuPanel(new R(Coord.left(1), Coord.top(18), Coord.height(15), Coord.width(77.7f)), "sushi") {
			@Override
			public void onClicked(final WEvent ev, final Area pgp, final Point p, final int button) {

			}
		});

		add(new ClickMenuPanel(new R(Coord.left(1), Coord.top(33), Coord.height(15), Coord.width(77.7f)), "sushi") {
			@Override
			public void onClicked(final WEvent ev, final Area pgp, final Point p, final int button) {

			}
		});
		super.initWidget();
	}
}
