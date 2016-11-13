package com.kamesuta.mc.signpic.plugin.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kamesuta.mc.bnnwidget.WEvent;
import com.kamesuta.mc.bnnwidget.WFrame;
import com.kamesuta.mc.bnnwidget.WPanel;
import com.kamesuta.mc.bnnwidget.motion.Easings;
import com.kamesuta.mc.bnnwidget.motion.MCoord;
import com.kamesuta.mc.bnnwidget.position.Area;
import com.kamesuta.mc.bnnwidget.position.Coord;
import com.kamesuta.mc.bnnwidget.position.Point;
import com.kamesuta.mc.bnnwidget.position.R;
import com.kamesuta.mc.signpic.entry.EntryId;
import com.kamesuta.mc.signpic.entry.content.ContentId;
import com.kamesuta.mc.signpic.gui.SignPicLabel;
import com.kamesuta.mc.signpic.plugin.SignData;
import com.kamesuta.mc.signpic.plugin.packet.PacketHandler;
import com.kamesuta.mc.signpic.plugin.packet.PacketHandler.SignPicturePacket;
import com.kamesuta.mc.signpic.render.RenderHelper;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class GuiManager extends WFrame {
	public String key;
	protected final GuiGallery gallery;
	protected int size;
	protected final Map<Integer, SignData> data = Maps.newHashMap();

	public GuiManager(final String data, final String size) {
		this.key = data;
		this.size = NumberUtils.toInt(size);
		this.gallery = new GuiGallery(new R(Coord.left(0), Coord.top(0), Coord.right(0), Coord.bottom(0)));
	}

	public void data(final String token, final String s) {
		final int id = NumberUtils.toInt(token);
		final SignData d = PacketHandler.gson.fromJson(s, SignData.class);
		this.data.put(id, d);
	}

	@Override
	protected void init() {
		add(this.gallery);
	}

	public class GuiGallery extends WPanel {
		protected GalleryPanel panel;
		protected MouseOverPanel overPanel;
		protected MCoord offset;

		public GuiGallery(final R position) {
			super(position);
			this.offset = MCoord.top(0);
			this.panel = new GalleryPanel(new R(Coord.left(0), this.offset, Coord.right(0), Coord.bottom(0)));
			this.overPanel = new MouseOverPanel(new R(Coord.left(0), Coord.top(0), Coord.right(0), Coord.bottom(0)));
		}

		@Override
		protected void initWidget() {
			Keyboard.enableRepeatEvents(true);
			add(this.panel);
			add(this.overPanel);
		}

		@Override
		public boolean mouseScrolled(final WEvent ev, final Area pgp, final Point p, final int scroll) {
			this.offset.stop().add(Easings.easeOutSine.move(.25f, Math.min(0, Math.max(-(GuiManager.this.size/4)*80, this.offset.get()+scroll)))).start();

			return super.mouseScrolled(ev, pgp, p, scroll);
		}

		@Override
		public boolean onClosing(final WEvent ev, final Area pgp, final Point p) {
			Keyboard.enableRepeatEvents(false);
			return super.onClosing(ev, pgp, p);
		}

		public class MouseOverPanel extends WPanel {
			protected SignData d;
			protected ContentId i;
			protected String leftURI;

			public MouseOverPanel(final R position) {
				super(position);
			}

			public void setData(final SignData data) {
				this.d = data;
				if (this.d!=null) {
					this.i = new EntryId(data.sign).getContentId();
					final String uri = this.i.getURI();
					this.leftURI = uri.length()>30 ? uri.substring(0, 30)+"..." : uri;
				}
			}

			@Override
			public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
				final Area a = getGuiPosition(pgp);
				if (this.d!=null) {
					final float x1 = p.x()<a.x2()-180 ? p.x()+8 : p.x()-180;
					final float x2 = p.x()+180<a.x2() ? p.x()+180 : p.x()-8;
					final float y1 = p.y()>30 ? p.y()-30 : p.y();
					final float y2 = p.y()>30 ? p.y() : p.y()+30;
					final Area overlay = new Area(x1, y1, x2, y2);
					glColor4f(0, 0, 0, 1);
					RenderHelper.startShape();
					draw(overlay, GL_QUADS);
					glLineWidth(4f);
					glColor4f(.1f, 0, .2f, 1);
					draw(overlay, GL_LINE_LOOP);
					RenderHelper.startTexture();
					drawString("Owner:"+this.d.owner_name, overlay.minX()+3, overlay.minY()+4, 0xffffff);
					drawString(this.leftURI, overlay.minX()+3, overlay.minY()+17, 0xffffff);
				}

				super.draw(ev, pgp, p, frame, popacity);
			}
		}

		public class GalleryPanel extends WPanel {
			protected Map<GalleryLabel, Boolean> labels = Maps.newLinkedHashMap();
			protected boolean labelsMouseInside = false;

			public GalleryPanel(final R position) {
				super(position);
			}

			public List<GalleryLabel> getSelectLabel() {
				final List<GalleryLabel> list = Lists.newLinkedList();
				for (final Map.Entry<GalleryLabel, Boolean> line : this.labels.entrySet())
					if (line.getValue())
						list.add(line.getKey());
				return list;
			}

			public void selectAll(final boolean select) {
				for (final Map.Entry<GalleryLabel, Boolean> line : this.labels.entrySet())
					line.setValue(select);
			}

			public void selectSoFar(final int number) {
				int i = 0;
				boolean select = false;
				for (final Map.Entry<GalleryLabel, Boolean> line : this.labels.entrySet()) {
					if (select&&i<=number)
						line.setValue(true);
					if (!select)
						select = line.getValue();
					i++;
				}
			}

			@Override
			public void update(final WEvent ev, final Area pgp, final Point p) {
				final Area a = getGuiPosition(pgp);

				int i;
				while ((i = getContainer().size())<=Math.min(((a.h()-GuiGallery.this.offset.get())/80)*4, GuiManager.this.size))
					add(i);

				for (final Map.Entry<GalleryLabel, Boolean> line : this.labels.entrySet()) {
					line.getKey().selected = line.getValue();
				}
				if (!this.labelsMouseInside)
					GuiGallery.this.overPanel.setData(null);
				this.labelsMouseInside = false;
				super.update(ev, pgp, p);
			}

			public void add(final int i) {
				final GalleryLabel label = new GalleryLabel(new R(Coord.pleft((i%4)/4f), Coord.top((i/4)*80), Coord.pwidth(1f/4.3f), Coord.height(80)), i);
				add(label);
				this.labels.put(label, false);
			}

			protected Area selectArea;
			private boolean drawSelectArea;
			private Point startSelectPoint;

			@Override
			public boolean mouseDragged(final WEvent ev, final Area pgp, final Point p, final int button, final long time) {
				if (button<=1&&time>100&&this.startSelectPoint!=null) {
					this.drawSelectArea = true;
					this.selectArea = new Area(this.startSelectPoint.x(), this.startSelectPoint.y(), p.x(), p.y());
				}
				return super.mouseDragged(ev, pgp, p, button, time);
			}

			@Override
			public boolean mouseClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
				if (button<=1) {
					this.startSelectPoint = p;
					if (!super.mouseClicked(ev, pgp, p, button)) {
						selectAll(false);
						return true;
					}
				}
				return super.mouseClicked(ev, pgp, p, button);
			}

			@Override
			public boolean mouseReleased(final WEvent ev, final Area pgp, final Point p, final int button) {
				this.drawSelectArea = false;
				this.selectArea = null;
				return super.mouseReleased(ev, pgp, p, button);
			}

			@Override
			public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
				super.draw(ev, pgp, p, frame, popacity);
				if (this.drawSelectArea&&this.selectArea!=null) {
					glColor4f(.25f, .3f, 1, .4f);
					RenderHelper.startShape();
					w.begin(GL_QUADS, DefaultVertexFormats.POSITION);
					w.pos(this.selectArea.minX(), this.selectArea.minY(), 0).endVertex();
					w.pos(this.selectArea.minX(), this.selectArea.maxY(), 0).endVertex();
					w.pos(this.selectArea.maxX(), this.selectArea.maxY(), 0).endVertex();
					w.pos(this.selectArea.maxX(), this.selectArea.minY(), 0).endVertex();
					t.draw();
					glLineWidth(1.5f);
					glColor4f(.2f, .3f, 1, .6f);
					draw(this.selectArea, GL_LINE_LOOP);
				}
			}

			@Override
			public boolean keyTyped(final WEvent ev, final Area pgp, final Point p, final char c, final int keycode) {
				if (GuiScreen.isCtrlKeyDown()&&keycode==Keyboard.KEY_A)
					selectAll(true);
				return super.keyTyped(ev, pgp, p, c, keycode);
			}

			public class GalleryLabel extends SignPicLabel {
				protected EntryId Default = new EntryId("!signpic:textures/logo.png[]");

				protected int i;
				protected boolean selected;

				public GalleryLabel(final R position, final int i) {
					super(position);
					this.i = i;
				}

				@Override
				public void onAdded() {
					setEntryId(this.Default);
					PacketHandler.instance.sendPacket(new SignPicturePacket("data", GuiManager.this.key, Integer.toString(this.i)));
				}

				@Override
				public void update(final WEvent ev, final Area pgp, final Point p) {
					final Area a = getGuiPosition(pgp);
					final SignData e = GuiManager.this.data.get(this.i);
					if (this.entryId==null||this.entryId==this.Default) {
						if (e!=null)
							setEntryId(new EntryId(e.sign));
					}

					if (GalleryPanel.this.selectArea!=null)
						GalleryPanel.this.labels.put(this, GalleryPanel.this.selectArea.areaOverlap(a));

					if (a.pointInside(p)) {
						GalleryPanel.this.labelsMouseInside = true;
						if (this.entryId==this.Default)
							GuiGallery.this.overPanel.setData(null);
						if (e!=null)
							GuiGallery.this.overPanel.setData(e);
					}
				}

				public boolean overlay(final WEvent ev, final Area pgp, final Point p) {
					final Area a = getGuiPosition(pgp);
					final SignData e = GuiManager.this.data.get(this.i);
					if (a.pointInside(p)) {
						if (e!=null)
							GuiGallery.this.overPanel.setData(e);
						return true;
					}
					return false;
				}

				@Override
				public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float opacity) {
					super.draw(ev, pgp, p, frame, opacity);
					final Area a = getGuiPosition(pgp);
					if (this.entryId!=this.Default) {
						if (a.pointInside(p)||this.selected) {
							glColor4f(.4f, .7f, 1, this.selected ? .7f : .4f);
							RenderHelper.startShape();
							draw(a, GL_QUADS);
						}
						if (this.selected) {
							glLineWidth(3f);
							glColor4f(.4f, .7f, 1, .8f);
							RenderHelper.startShape();
							draw(a, GL_LINE_LOOP);
						}
					}
				}

				@Override
				public boolean mouseClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
					final Area a = getGuiPosition(pgp);
					if (a.pointInside(p)&&button==0) {
						if (!this.selected) {
							if (!GuiScreen.isShiftKeyDown()) {
								if (!GuiScreen.isCtrlKeyDown())
									selectAll(false);
							} else
								selectSoFar(this.i);
						} else
							selectAll(false);
						if (this.entryId!=this.Default)
							GalleryPanel.this.labels.put(this, true);
					}
					return super.mouseClicked(ev, pgp, p, button)||a.pointInside(p);
				}
			}
		}
	}
}
