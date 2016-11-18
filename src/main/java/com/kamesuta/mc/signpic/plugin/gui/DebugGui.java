package com.kamesuta.mc.signpic.plugin.gui;

import com.kamesuta.mc.bnnwidget.WFrame;

import net.minecraft.client.gui.GuiScreen;

public class DebugGui extends WFrame {

	public DebugGui(final GuiScreen parent) {
		super(parent);
	}

	@Override
	public void initGui() {
		super.initGui();

	}

	@Override
	protected void initWidget() {
		//		add(new WPanel(R.diff(0, 0, 0, 0)) {
		//			@Override
		//			public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
		//				RenderHelper.startShape();
		//				GlStateManager.color(0, 0, 0, .5f);
		//				draw(getGuiPosition(pgp));
		//				super.draw(ev, pgp, p, frame, popacity);
		//			}
		//		});
	}
}
