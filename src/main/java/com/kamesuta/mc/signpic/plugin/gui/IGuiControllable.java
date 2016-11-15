package com.kamesuta.mc.signpic.plugin.gui;

import com.kamesuta.mc.bnnwidget.WCommon;

public interface IGuiControllable extends IKeyControllable {

	void setGuiControllable(WCommon gui);

	void setControllable(WCommon gui);

	boolean isGuiControllable();

	boolean isControllable();

}
