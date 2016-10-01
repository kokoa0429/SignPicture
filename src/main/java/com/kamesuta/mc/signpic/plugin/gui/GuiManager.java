package com.kamesuta.mc.signpic.plugin.gui;

import com.kamesuta.mc.bnnwidget.WFrame;
import com.kamesuta.mc.bnnwidget.WPanel;
import com.kamesuta.mc.bnnwidget.position.Coord;
import com.kamesuta.mc.bnnwidget.position.RArea;

public class GuiManager extends WFrame {
	public String key;

	public GuiManager(final String data) {
		this.key = data;
	}

	@Override
	protected void init() {
		add(new WPanel(RArea.diff(0, 0, 0, 0)) {
			@Override
			protected void initWidget() {
				add(new WPanel(new RArea(Coord.left(0),Coord.top(0), Coord.right(0), Coord.bottom(0))) {
					@Override
					protected void initWidget() {
						// TODO 自動生成されたメソッド・スタブ
						super.initWidget();
					}

					public void addComponent(final String id) {

					}
				});
			}
		});
	}
}
