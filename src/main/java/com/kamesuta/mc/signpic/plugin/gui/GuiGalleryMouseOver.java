package com.kamesuta.mc.signpic.plugin.gui;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Desktop;
import java.net.URI;

import com.kamesuta.mc.bnnwidget.WEvent;
import com.kamesuta.mc.bnnwidget.position.Area;
import com.kamesuta.mc.bnnwidget.position.Coord;
import com.kamesuta.mc.bnnwidget.position.Point;
import com.kamesuta.mc.bnnwidget.position.R;
import com.kamesuta.mc.signpic.entry.EntryId;
import com.kamesuta.mc.signpic.entry.content.ContentId;
import com.kamesuta.mc.signpic.gui.OverlayFrame;
import com.kamesuta.mc.signpic.plugin.SignData;
import com.kamesuta.mc.signpic.render.RenderHelper;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiGalleryMouseOver extends GuiMouseOver {
	protected final String localizationOwner;
	protected String uri;
	protected String world;
	protected String owner;

	public GuiGalleryMouseOver(final R position, final Controllable frame) {
		super(position, frame);
		this.localizationOwner = I18n.format("signpic.gui.manager.owner");
	}

	@Override
	public void setSignPicData(final ISignPicData label) {
		super.setSignPicData(label);
		if (this.data!=null) {
			final SignData data = this.data.getData();
			this.owner = this.localizationOwner+":"+data.owner_name;
			this.world = "World:"+data.world;
			final String uri = new EntryId(data.sign).getContentId().getURI();
			String substringURI = uri;
			while (getStringWidth(substringURI)>120)
				substringURI = substringURI.substring(0, substringURI.length()-1);
			if (uri.length()==substringURI.length())
				this.uri = substringURI;
			else
				this.uri = substringURI+"...";
		}
	}

	@Override
	protected void openMenu(final WEvent ev, final Area pgp, final Point p) {
		final Area a = getGuiPosition(pgp);
		final float left = this.openMenuPoint.x()<115 ? 0 : this.openMenuPoint.x()-115;
		final float top = this.openMenuPoint.y()>a.y2()-80 ? this.openMenuPoint.y()-80 : this.openMenuPoint.y();
		final R position = new R(Coord.left(left), Coord.top(top), Coord.height(79), Coord.width(115));
		add(new GuiClickMenu(position, this.frame) {
			@Override
			protected void initWidget() {
				add(new ClickMenuPanel(I18n.format("signpic.gui.manager.open")) {
					{
						setEmphasis(true);
						setIcon(new ResourceLocation("signpic", "textures/logo.png"));
					}

					@Override
					public boolean onEnter(final WEvent ev, final Area pgp, final Point p) {
						return true;
					}
				});
				add(new ClickMenuPanel(I18n.format("signpic.gui.manager.openbrowzer")) {
					{
						this.id = GuiGalleryMouseOver.this.data.getEntryId().getContentId();
						if (this.id.getID()==this.id.getURI())
							setAvailable(false);
					}

					private ContentId id;

					@Override
					public boolean onEnter(final WEvent ev, final Area pgp, final Point p) {
						try {
							final URI uri = new URI(this.id.getURI());
							Desktop.getDesktop().browse(uri);
						} catch (final Exception e) {
							OverlayFrame.instance.pane.addNotice1(I18n.format("signpic.gui.notice.openblowzerfaild", e.getClass().getName()), 2);
						}
						return true;
					}
				});
				add(new ClickMenuPanel("sushi") {
					@Override
					public boolean onEnter(final WEvent ev, final Area pgp, final Point p) {
						return true;
					}
				});
				add(new ClickMenuPanel("sushi") {
					@Override
					public boolean onEnter(final WEvent ev, final Area pgp, final Point p) {
						return true;
					}
				});
				add(new ClickMenuPanel("sushi") {
					@Override
					public boolean onEnter(final WEvent ev, final Area pgp, final Point p) {
						return true;
					}
				});
				super.initWidget();
			}
		});
	}

	@Override
	public void drawMouseOver(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
		final Area a = getGuiPosition(pgp);
		final boolean longName = getStringWidth(this.owner)+getStringWidth(this.world)<115;
		final float height = longName ? 25 : 35;
		final float x1 = p.x()<a.x2()-140 ? p.x()+8 : p.x()-140;
		final float y1 = p.y()>height ? p.y()-height : p.y();
		final float x2 = p.x()+140<a.x2() ? p.x()+140 : p.x()-8;
		final float y2 = p.y()>height ? p.y() : p.y()+height;
		final Area overlay = new Area(x1, y1, x2, y2);
		GlStateManager.color(0, 0, 0, 1);
		RenderHelper.startShape();
		draw(overlay);
		glLineWidth(4f);
		GlStateManager.color(.1f, 0, .2f, 1);
		draw(overlay, GL_LINE_LOOP);
		GlStateManager.pushMatrix();
		GlStateManager.translate(overlay.minX()+overlay.w()/2, overlay.minY()+overlay.h()/2, 0);
		RenderHelper.startTexture();
		if (longName) {
			fontColor(0xffffff);
			drawString(this.owner, overlay.minX()-overlay.maxX()+70, overlay.minY()-overlay.maxY()+15, 0, 0, Align.LEFT, VerticalAlign.TOP, false);
			fontColor(0xffffff);
			drawString(this.world, overlay.minX()-overlay.maxX()+195, overlay.minY()-overlay.maxY()+15, 0, 0, Align.RIGHT, VerticalAlign.TOP, false);
			fontColor(0xffffff);
			drawString(this.uri, overlay.minX()-overlay.maxX()+70, overlay.minY()-overlay.maxY()+26, 0, 0, Align.LEFT, VerticalAlign.TOP, false);
		} else {
			fontColor(0xffffff);
			drawString(this.owner, overlay.minX()-overlay.maxX()+70, overlay.minY()-overlay.maxY()+20, 0, 0, Align.LEFT, VerticalAlign.TOP, false);
			fontColor(0xffffff);
			drawString(this.world, overlay.minX()-overlay.maxX()+70, overlay.minY()-overlay.maxY()+31, 0, 0, Align.LEFT, VerticalAlign.TOP, false);
			fontColor(0xffffff);
			drawString(this.uri, overlay.minX()-overlay.maxX()+70, overlay.minY()-overlay.maxY()+42, 0, 0, Align.LEFT, VerticalAlign.TOP, false);
		}
		GlStateManager.popMatrix();
	}

	@Override
	public boolean mouseClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
		if (super.mouseClicked(ev, pgp, p, button)) {
			removeAll();
			this.frame.setKeyControllable(null);
			return true;
		}
		return false;
	}

	@Override
	public boolean keyTyped(final WEvent ev, final Area pgp, final Point p, final char c, final int keycode) {
		if (super.keyTyped(ev, pgp, p, c, keycode)) {
			removeAll();
			this.frame.setKeyControllable(null);
			return true;
		}
		return false;
	}
}