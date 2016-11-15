package com.kamesuta.mc.signpic.plugin.gui;

import com.kamesuta.mc.bnnwidget.WCommon;

public interface IGuiControllable extends IKeyControllable {

	void setControllable(WCommon gui);

	boolean isControllable();

	void setGuiControllable(WCommon gui);

	boolean isGuiControllable();

}
