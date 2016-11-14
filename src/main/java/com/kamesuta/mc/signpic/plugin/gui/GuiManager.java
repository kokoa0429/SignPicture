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
import net.minecraft.client.resources.I18n;

public class GuiManager extends WFrame {
	public static int row = 4;

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
			if (GuiScreen.isCtrlKeyDown()) {
				if (scroll<0) {
					if (GuiManager.row<=10)
						GuiManager.row++;
				} else if (GuiManager.row>3)
					GuiManager.row--;
			} else
				this.offset.stop().add(Easings.easeOutSine.move(.25f, Math.min(0, Math.max(-(GuiManager.this.size/GuiManager.row-GuiManager.row+2)*(GuiManager.this.height*(1f/(GuiManager.row+.3f))), this.offset.get()+scroll)))).start();

			return super.mouseScrolled(ev, pgp, p, scroll);
		}

		@Override
		public void update(final WEvent ev, final Area pgp, final Point p) {

			super.update(ev, pgp, p);
		}

		@Override
		public boolean onClosing(final WEvent ev, final Area pgp, final Point p) {
			Keyboard.enableRepeatEvents(false);
			return super.onClosing(ev, pgp, p);
		}

		public class MouseOverPanel extends WPanel {
			protected SignData data;
			protected ContentId id;
			protected String leftURI;
			protected String owner;
			protected Point openMenuPoint;

			public MouseOverPanel(final R position) {
				super(position);
				this.owner = I18n.format("signpic.gui.manager.owner")+":";
			}

			public void setData(final SignData data) {
				this.data = data;
				if (this.data!=null) {
					this.id = new EntryId(data.sign).getContentId();
					final String uri = this.id.getURI();
					this.leftURI = uri.length()>30 ? uri.substring(0, 30)+"..." : uri;
				}
			}

			public void openMenu(final Point p) {
				this.openMenuPoint = p;
			}

			@Override
			public void update(final WEvent ev, final Area pgp, final Point p) {
				final Area a = getGuiPosition(pgp);
				if (this.openMenuPoint!=null) {
					if (getContainer().size()>=1)
						remove(getContainer().get(0));

					final float left = this.openMenuPoint.x()<80 ? 0 : this.openMenuPoint.x()-80;
					final float top = this.openMenuPoint.y()>a.y2()/2 ? this.openMenuPoint.y()-100 : this.openMenuPoint.y();
					final R position = new R(Coord.left(left), Coord.top(top), Coord.height(100), Coord.width(80));
					add(new GuiClickMenu(position, this, this.data) {

						@Override
						protected void initWidget() {

							add(new ClickMenuPanel(new R(Coord.left(1), Coord.top(3), Coord.height(15), Coord.width(77.7f)), "sushi") {
								{
									setEmphasis(true);
								}

							});

							add(new ClickMenuPanel(new R(Coord.left(1), Coord.top(18), Coord.height(15), Coord.width(77.7f)), "sushi") {

							});

							add(new ClickMenuPanel(new R(Coord.left(1), Coord.top(33), Coord.height(15), Coord.width(77.7f)), "sushi") {

							});

							super.initWidget();
						}
					});
					this.openMenuPoint = null;
				}
				super.update(ev, pgp, p);
			}

			@Override
			public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
				final Area a = getGuiPosition(pgp);
				if (this.data!=null) {
					final float x1 = p.x()<a.x2()-140 ? p.x()+8 : p.x()-140;
					final float x2 = p.x()+140<a.x2() ? p.x()+140 : p.x()-8;
					final float y1 = p.y()>25 ? p.y()-25 : p.y();
					final float y2 = p.y()>25 ? p.y() : p.y()+25;
					final Area overlay = new Area(x1, y1, x2, y2);
					glColor4f(0, 0, 0, 1);
					RenderHelper.startShape();
					draw(overlay, GL_QUADS);
					glLineWidth(4f);
					glColor4f(.1f, 0, .2f, 1);
					draw(overlay, GL_LINE_LOOP);
					glPushMatrix();
					glTranslated(overlay.minX()+overlay.w()/2, overlay.minY()+overlay.h()/2, 0);
					RenderHelper.startTexture();
					drawString(this.owner+this.data.owner_name, overlay.minX()-overlay.maxX()+70, overlay.minY()-overlay.maxY()+15, 0xffffff);
					drawString(this.leftURI, overlay.minX()-overlay.maxX()+70, overlay.minY()-overlay.maxY()+26, 0xffffff);
					glPopMatrix();
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

			int rowCache;

			@Override
			public void update(final WEvent ev, final Area pgp, final Point p) {
				final Area a = getGuiPosition(pgp);

				for (final Map.Entry<GalleryLabel, Boolean> line : this.labels.entrySet()) {
					final GalleryLabel label = line.getKey();
					label.selected = line.getValue();
					if (this.rowCache!=GuiManager.row)
						label.setPosition(getNewLabelPosition(label, GuiManager.row));
				}

				int i;
				while ((i = getContainer().size())<=Math.min(((a.h()-GuiGallery.this.offset.get())/(((i%GuiManager.row)/(float) GuiManager.row)*GuiManager.this.size))*GuiManager.row, GuiManager.this.size))
					add(i, GuiManager.row);

				this.rowCache = GuiManager.row;

				if (!this.labelsMouseInside)
					GuiGallery.this.overPanel.setData(null);
				this.labelsMouseInside = false;

				super.update(ev, pgp, p);
			}

			public void add(final int i, final int row) {
				final GalleryLabel label = new GalleryLabel(new R(Coord.pleft((i%row)/(float) row), Coord.top((i/row)*((GuiManager.this.height/row)+3)), Coord.pwidth(1f/(row+.3f)), Coord.height(GuiManager.this.height*(1f/(row+.3f)))), i);
				add(label);
				this.labels.put(label, false);
			}

			public R getNewLabelPosition(final GalleryLabel label, final int row) {
				final int i = label.i;
				return new R(Coord.pleft((i%row)/(float) row), Coord.top((i/row)*((GuiManager.this.height/row)+3)), Coord.pwidth(1f/(row+.3f)), Coord.height(GuiManager.this.height*(1f/(row+.3f))));
			}

			protected Area selectArea;
			private boolean drawSelectArea;
			private Point startSelectPoint;

			@Override
			public boolean mouseDragged(final WEvent ev, final Area pgp, final Point p, final int button, final long time) {
				if (button<=1&&!GuiScreen.isCtrlKeyDown()&&!GuiScreen.isShiftKeyDown()&&this.startSelectPoint!=null) {
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
				protected int i;
				protected boolean selected;

				public GalleryLabel(final R position, final int i) {
					super(position);
					this.i = i;
				}

				public boolean isDefault() {
					return this.i==0;
				}

				@Override
				public void onAdded() {
					if (isDefault())
						setEntryId(new EntryId("!signpic:textures/logo.png[]"));
					PacketHandler.instance.sendPacket(new SignPicturePacket("data", GuiManager.this.key, Integer.toString(this.i)));
				}

				@Override
				public void update(final WEvent ev, final Area pgp, final Point p) {
					final Area a = getGuiPosition(pgp);
					final SignData e = GuiManager.this.data.get(this.i-1);
					if (this.entryId==null&&e!=null)
						setEntryId(new EntryId(e.sign));

					if (a.pointInside(p)) {
						GalleryPanel.this.labelsMouseInside = true;
						if (isDefault())
							GuiGallery.this.overPanel.setData(null);
						if (e!=null)
							GuiGallery.this.overPanel.setData(e);
					}

					if (GalleryPanel.this.selectArea!=null)
						GalleryPanel.this.labels.put(this, GalleryPanel.this.selectArea.areaOverlap(a));
				}

				@Override
				public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float opacity) {
					super.draw(ev, pgp, p, frame, opacity);
					final Area a = getGuiPosition(pgp);
					if (!isDefault()) {
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
					if (a.pointInside(p)) {
						if (button==0) {
							if (!this.selected) {
								if (!GuiScreen.isShiftKeyDown()) {
									if (!GuiScreen.isCtrlKeyDown())
										selectAll(false);
								} else
									selectSoFar(this.i);
								select();
							} else {
								if (!GuiScreen.isCtrlKeyDown()) {
									selectAll(false);
									select();
								} else
									GalleryPanel.this.labels.put(this, false);
							}
						} else if (button==1) {
							GuiGallery.this.overPanel.openMenu(p);
						}
					}
					return super.mouseClicked(ev, pgp, p, button)||a.pointInside(p);
				}

				public void select() {
					if (!isDefault())
						GalleryPanel.this.labels.put(this, true);
				}
			}
		}
	}
}