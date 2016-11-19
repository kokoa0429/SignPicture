package com.kamesuta.mc.signpic.plugin.gui;

import java.util.Map;

import com.kamesuta.mc.bnnwidget.position.Area;

public interface ISelectManager {

	public void select(int i);

	public void selectAll(boolean b);

	public void selectSoFar(int i);

	public int getLastSelect();

	public void setLastSelect(int i);

	public Map<Selectable, Boolean> getSelectables();

	public Area getSelectArea();

	public boolean getLabelsMouseInside();

	public void setLabelsMouseInside(boolean b);
}
