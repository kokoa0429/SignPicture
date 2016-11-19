package com.kamesuta.mc.signpic.plugin.gui;

import java.util.Map;

import com.kamesuta.mc.bnnwidget.position.Area;

public interface ISelectManager {

	void select(int i);

	void selectAll(boolean b);

	void selectSoFar(int i);

	int getLastSelect();

	void setLastSelect(int i);

	Map<Selectable, Boolean> getSelectables();

	Area getSelectArea();

	boolean getLabelsMouseInside();

	void setLabelsMouseInside(boolean b);

	boolean isActive();
}
