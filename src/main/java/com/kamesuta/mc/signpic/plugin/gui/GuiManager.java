package com.kamesuta.mc.signpic.plugin.gui;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kamesuta.mc.bnnwidget.WCommon;
import com.kamesuta.mc.bnnwidget.WEvent;
import com.kamesuta.mc.bnnwidget.WFrame;
import com.kamesuta.mc.bnnwidget.WPanel;
import com.kamesuta.mc.bnnwidget.motion.Easings;
import com.kamesuta.mc.bnnwidget.motion.MCoord;
import com.kamesuta.mc.bnnwidget.position.Area;
import com.kamesuta.mc.bnnwidget.position.Coord;
import com.kamesuta.mc.bnnwidget.position.Point;
import com.kamesuta.mc.bnnwidget.position.R;
import com.kamesuta.mc.signpic.Reference;
import com.kamesuta.mc.signpic.entry.EntryId;
import com.kamesuta.mc.signpic.gui.SignPicLabel;
import com.kamesuta.mc.signpic.plugin.SignData;
import com.kamesuta.mc.signpic.plugin.gui.GuiManager.GuiGallery.GalleryPanel.GalleryLabel;
import com.kamesuta.mc.signpic.plugin.packet.PacketHandler;
import com.kamesuta.mc.signpic.plugin.packet.PacketHandler.SignPicturePacket;
import com.kamesuta.mc.signpic.render.RenderHelper;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiManager extends WFrame implements IGuiControllable {
	public static int row = 4;

	public String key;
	protected final GuiGallery gallery;
	protected int size;
	protected final Map<Integer, SignData> data = Maps.newHashMap();

	private WCommon controllGui;
	private WCommon keyControllGui;

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
		Keyboard.enableRepeatEvents(true);
		add(this.gallery);
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		super.onGuiClosed();
	}

	@Override
	public void setControllable(final WCommon gui) {
		this.controllGui = gui;
		this.keyControllGui = gui;
	}

	@Override
	public boolean isControllable() {
		return this.controllGui==null||this.keyControllGui==null;
	}

	@Override
	public void setGuiControllable(final WCommon gui) {
		this.controllGui = gui;
	}

	@Override
	public boolean isGuiControllable() {
		return this.controllGui==null;
	}

	@Override
	public void setKeyControllable(final WCommon gui) {
		this.keyControllGui = gui;
	}

	@Override
	public boolean isKeyControllable() {
		return this.keyControllGui==null;
	}

	@Override
	protected void mouseClicked(final int x, final int y, final int button) throws IOException {
		if (isGuiControllable()) {
			super.mouseClicked(x, y, button);
		} else {
			this.mousebutton = button;
			final Area gp = getAbsolute();
			final Point p = getMouseAbsolute();
			this.controllGui.mouseClicked(this.event, gp, p, button);
		}
	}

	@Override
	protected void keyTyped(final char c, final int keycode) {
		if (isControllable()) {
			super.keyTyped(c, keycode);
		} else {
			final Area gp = getAbsolute();
			final Point p = getMouseAbsolute();
			this.controllGui.keyTyped(this.event, gp, p, c, keycode);
		}
	}

	@Override
	protected void mouseClickMove(final int x, final int y, final int button, final long time) {
		if (isGuiControllable()) {
			super.mouseClickMove(x, y, button, time);
		} else {
			final Area gp = getAbsolute();
			final Point p = getMouseAbsolute();
			this.controllGui.mouseDragged(this.event, gp, p, button, time);
		}
	}

	public class GuiGallery extends WPanel {
		protected GalleryPanel panel;
		protected MouseOverPanel overPanel;
		protected MCoord offset;
		protected int lastSelect = -1;

		public GuiGallery(final R position) {
			super(position);
			this.offset = MCoord.top(0);
			this.panel = new GalleryPanel(new R(Coord.left(0), this.offset, Coord.right(0), Coord.bottom(0)));
			this.overPanel = new MouseOverPanel(new R(Coord.left(0), Coord.top(0), Coord.right(0), Coord.bottom(0)));
		}

		private Area selectArea;
		private Point startSelectPoint;
		private float selectAbsY;
		private boolean areaSelect;

		public Area getSelectArea() {
			return this.selectArea;
		}

		public int getLine(final int i) {
			return (int) Math.ceil((i+1)/(float) GuiManager.row);
		}

		public float getPanelHeight() {
			return height()*(1f/(row+.3f));
		}

		int lastSelectCache = -1;

		@Override
		public void update(final WEvent ev, final Area pgp, final Point p) {
			if (this.areaSelect&&!isControllable()) {
				this.selectArea = null;
				this.startSelectPoint = null;
				this.areaSelect = false;
			}
			if (this.areaSelect&&this.startSelectPoint!=null) {
				this.selectArea = new Area(this.startSelectPoint.x(), this.selectAbsY+this.offset.get(), p.x(), p.y());
				if (height()-3<=p.y())
					scroll(ev, pgp, p, -20);
				if (p.y()<=3)
					scroll(ev, pgp, p, 20);
			}

			if (this.lastSelectCache!=this.lastSelect) {
				final Area a = getGuiPosition(pgp);
				final Area panelArea = new R(Coord.pleft((this.lastSelect%row)/(float) row), Coord.top((this.lastSelect/row)*((height()/row)+3)+this.offset.get()), Coord.pwidth(1f/(row+.3f)), Coord.height(getPanelHeight())).getAbsolute(a);
				if (!a.areaInside(panelArea)) {
					final int lastSelectLine = getLine(this.lastSelect);
					final int lastSelectCacheLine = getLine(this.lastSelectCache);
					float move = 0;
					if (lastSelectLine==lastSelectCacheLine)
						if (height()-panelArea.maxY()<panelArea.minY())
							move = this.offset.get()+(height()-panelArea.maxY());
						else
							move = this.offset.get()-panelArea.minY();
					else if (this.lastSelect>this.lastSelectCache)
						move = this.offset.get()+(height()-panelArea.maxY());
					else
						move = this.offset.get()-panelArea.minY();
					this.offset.stop().add(Easings.easeOutSine.move(.25f, move)).start();
				}
			}
			this.lastSelectCache = this.lastSelect;
			super.update(ev, pgp, p);
		}

		@Override
		public boolean mouseDragged(final WEvent ev, final Area pgp, final Point p, final int button, final long time) {
			if (button<=1&&!GuiScreen.isCtrlKeyDown()&&!GuiScreen.isShiftKeyDown()&&!GuiGallery.this.overPanel.isOpenMenu()&&this.startSelectPoint!=null)
				this.areaSelect = true;
			return super.mouseDragged(ev, pgp, p, button, time);
		}

		@Override
		public boolean mouseClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
			if (button<=1) {
				this.startSelectPoint = p;
				this.selectAbsY = p.y()-this.offset.get();
			}
			return super.mouseClicked(ev, pgp, p, button);
		}

		@Override
		public boolean mouseReleased(final WEvent ev, final Area pgp, final Point p, final int button) {
			this.selectArea = null;
			this.areaSelect = false;
			return super.mouseReleased(ev, pgp, p, button);
		}

		@Override
		public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
			RenderHelper.startShape();
			GlStateManager.color(0f, 0f, 0f, .5f);
			drawRect(getGuiPosition(pgp));
			super.draw(ev, pgp, p, frame, popacity);
			if (this.selectArea!=null) {
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
		protected void initWidget() {
			add(this.panel);
			add(this.overPanel);
		}

		@Override
		public boolean mouseScrolled(final WEvent ev, final Area pgp, final Point p, final int scroll) {
			if (GuiScreen.isCtrlKeyDown()) {
				if (scroll<0) {
					if (GuiManager.row<=10) {
						GuiManager.row++;
					}
				} else if (GuiManager.row>3)
					GuiManager.row--;
				scroll(ev, pgp, p, 0);
			} else
				scroll(ev, pgp, p, scroll/2);
			return super.mouseScrolled(ev, pgp, p, scroll);
		}

		public void scroll(final WEvent ev, final Area pgp, final Point p, final int scroll) {
			if (isControllable()) {
				final float lines = getLine(GuiManager.this.size);
				final float nowheight = Math.abs(this.offset.get());
				final float maxheight = ((height()/row)+3)*lines-height();
				final float pscroll = nowheight-scroll>maxheight ? -maxheight : this.offset.get()+scroll;
				final float move = Math.min(0, Math.max(-(lines*(height()*(1f/(GuiManager.row+.3f)))), pscroll));
				this.offset.stop().add(Easings.easeOutSine.move(.25f, move)).start();
			}
		}

		protected Map<GalleryLabel, Boolean> labels = Maps.newLinkedHashMap();

		public List<GalleryLabel> getSelectLabel() {
			final List<GalleryLabel> list = Lists.newLinkedList();
			for (final Map.Entry<GalleryLabel, Boolean> line : this.labels.entrySet())
				if (line.getValue())
					list.add(line.getKey());
			return list;
		}

		public void select(final int number) {
			int i = 0;
			for (final Map.Entry<GalleryLabel, Boolean> line : this.labels.entrySet()) {
				if (i!=number)
					line.setValue(false);
				else
					line.setValue(true);
				i++;
			}
		}

		public void selectAll(final boolean select) {
			for (final Map.Entry<GalleryLabel, Boolean> line : this.labels.entrySet())
				line.setValue(select);
		}

		public void selectSoFar(final int number) {
			int i = 0;
			boolean select = false;
			for (final Map.Entry<GalleryLabel, Boolean> line : this.labels.entrySet()) {
				select = line.getValue();
				if (select)
					break;
				i++;
			}
			final int selectFirst = Math.min(i, number);
			final int selectEnd = Math.max(i, number);
			int ii = 0;
			for (final Map.Entry<GalleryLabel, Boolean> line : this.labels.entrySet()) {
				if (ii>=selectFirst&&selectEnd>=ii)
					line.setValue(true);
				ii++;
			}
		}

		@Override
		public boolean keyTyped(final WEvent ev, final Area pgp, final Point p, final char c, final int keycode) {
			if (GuiScreen.isCtrlKeyDown()&&keycode==Keyboard.KEY_A)
				selectAll(true);

			if (keycode==Keyboard.KEY_UP||keycode==Keyboard.KEY_DOWN||keycode==Keyboard.KEY_LEFT||keycode==Keyboard.KEY_RIGHT) {
				int next = 0;
				if (keycode==Keyboard.KEY_DOWN) {
					if (this.lastSelect<0) {
						next = GuiManager.row;
					} else {
						final boolean nextLine = getLine(GuiManager.this.size)>(int) Math.ceil((this.lastSelect)/(float) GuiManager.row);
						if (nextLine)
							next = this.lastSelect+GuiManager.row<=GuiManager.this.size ? this.lastSelect+GuiManager.row : GuiManager.this.size;
						else
							next = GuiManager.this.size;
						next = next!=0 ? GuiManager.this.size<next ? GuiManager.this.size : next : 1;
					}
				} else if (keycode==Keyboard.KEY_UP) {
					final boolean nextLine = (int) Math.ceil((this.lastSelect)/(float) GuiManager.row)!=1;
					Reference.logger.info((int) Math.ceil((this.lastSelect)/(float) GuiManager.row));
					if (nextLine)
						next = this.lastSelect-GuiManager.row>=1 ? this.lastSelect-GuiManager.row : 1;
					else
						next = this.lastSelect;
				} else if (keycode==Keyboard.KEY_LEFT) {
					next = this.lastSelect-1>0 ? this.lastSelect-1 : this.lastSelect;
				} else if (keycode==Keyboard.KEY_RIGHT) {
					next = this.lastSelect+1<=GuiManager.this.size ? this.lastSelect+1 : this.lastSelect;
				}
				if (!GuiScreen.isCtrlKeyDown())
					if (GuiScreen.isShiftKeyDown())
						selectSoFar(next);
					else
						select(next);
				this.lastSelect = next;
			}
			return super.keyTyped(ev, pgp, p, c, keycode);
		}

		public class MouseOverPanel extends WPanel {
			protected final String localizationOwner;
			protected String uri;
			protected String world;
			protected String owner;
			protected GalleryLabel label;

			public MouseOverPanel(final R position) {
				super(position);
				this.localizationOwner = I18n.format("signpic.gui.manager.owner");
			}

			public void setLabel(final GalleryLabel label) {
				if (this.label==label)
					return;

				this.label = label;
				if (this.label!=null) {
					final SignData data = this.label.getData();
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

			public void openMenu(final Point p) {
				if (this.label!=null&&!isOpenMenu())
					this.openMenuPoint = p;
			}

			public boolean isOpenMenu() {
				return getContainer().size()!=0;
			}

			@Override
			protected void initWidget() {
				invokeLater(new Runnable() {
					@Override
					public void run() {
						if (getContainer().size()>=1)
							getContainer().clear();
					}
				});
				super.initWidget();
			}

			Point openMenuPoint;

			@Override
			public void update(final WEvent ev, final Area pgp, final Point p) {
				if (this.openMenuPoint!=null) {
					final Area a = getGuiPosition(pgp);
					final float left = this.openMenuPoint.x()<80 ? 0 : this.openMenuPoint.x()-80;
					final float top = this.openMenuPoint.y()>a.y2()-100 ? this.openMenuPoint.y()-100 : this.openMenuPoint.y();
					final R position = new R(Coord.left(left), Coord.top(top), Coord.height(80), Coord.width(115));
					add(new GuiClickMenu(position, this, GuiManager.this) {
						@Override
						protected void initWidget() {
							add(new ClickMenuPanel(I18n.format("signpic.gui.manager.open")) {
								{
									setEmphasis(true);
									setIcon(new ResourceLocation("signpic", "textures/logo.png"));
								}

								@Override
								public boolean onClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
									return true;
								}
							});
							add(new ClickMenuPanel(I18n.format("signpic.gui.manager.openbrowzer")) {
								@Override
								public boolean onClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
									return true;
								}
							});
							add(new ClickMenuPanel("sushi") {
								@Override
								public boolean onClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
									return true;
								}
							});
							add(new ClickMenuPanel("sushi") {
								@Override
								public boolean onClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
									return true;
								}
							});
							add(new ClickMenuPanel("sushi") {
								@Override
								public boolean onClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
									return true;
								}
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
				if (this.label!=null&&!isOpenMenu()) {
					final float x1 = p.x()<a.x2()-140 ? p.x()+8 : p.x()-140;
					final float x2 = p.x()+140<a.x2() ? p.x()+140 : p.x()-8;
					final float y1 = p.y()>25 ? p.y()-25 : p.y();
					final float y2 = p.y()>25 ? p.y() : p.y()+25;
					final Area overlay = new Area(x1, y1, x2, y2);
					glColor4f(0, 0, 0, 1);
					RenderHelper.startShape();
					drawRect(overlay);
					glLineWidth(4f);
					glColor4f(.1f, 0, .2f, 1);
					draw(overlay, GL_LINE_LOOP);
					glPushMatrix();
					glTranslated(overlay.minX()+overlay.w()/2, overlay.minY()+overlay.h()/2, 0);
					RenderHelper.startTexture();
					drawString(this.owner, overlay.minX()-overlay.maxX()+70, overlay.minY()-overlay.maxY()+15, 0xffffff);
					drawStringR(this.world, overlay.minX()-overlay.maxX()+195, overlay.minY()-overlay.maxY()+15, 0xffffff);
					drawString(this.uri, overlay.minX()-overlay.maxX()+70, overlay.minY()-overlay.maxY()+26, 0xffffff);
					glPopMatrix();
				}

				super.draw(ev, pgp, p, frame, popacity);
			}
		}

		public class GalleryPanel extends WPanel {
			protected boolean labelsMouseInside;

			public GalleryPanel(final R position) {
				super(position);
			}

			int rowCache;

			@Override
			public void update(final WEvent ev, final Area pgp, final Point p) {
				final Area a = getGuiPosition(pgp);

				for (final Map.Entry<GalleryLabel, Boolean> line : GuiGallery.this.labels.entrySet()) {
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
					GuiGallery.this.overPanel.setLabel(null);
				this.labelsMouseInside = false;

				super.update(ev, pgp, p);
			}

			public void add(final int i, final int row) {
				final GalleryLabel label = new GalleryLabel(new R(Coord.pleft((i%row)/(float) row), Coord.top((i/row)*((height()/row)+3)), Coord.pwidth(1f/(row+.3f)), Coord.height(getPanelHeight())), i);
				add(label);
				GuiGallery.this.labels.put(label, false);
			}

			public R getNewLabelPosition(final GalleryLabel label, final int row) {
				final int i = label.i;
				return new R(Coord.pleft((i%row)/(float) row), Coord.top((i/row)*((height()/row)+3)), Coord.pwidth(1f/(row+.3f)), Coord.height(getPanelHeight()));
			}

			@Override
			public boolean mouseClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
				if (!super.mouseClicked(ev, pgp, p, button)) {
					selectAll(false);
					return true;
				} else
					return false;
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

				public SignData getData() {
					return GuiManager.this.data.get(this.i-1);
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
							GuiGallery.this.overPanel.setLabel(null);
						if (e!=null)
							GuiGallery.this.overPanel.setLabel(this);
					}

					if (getSelectArea()!=null)
						GuiGallery.this.labels.put(this, getSelectArea().areaOverlap(a));
				}

				@Override
				public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float opacity) {
					final Area a = getGuiPosition(pgp);
					if (!pgp.areaOverlap(a))
						return;
					super.draw(ev, pgp, p, frame, opacity);
					if (!isDefault()) {
						if (a.pointInside(p)||this.selected) {
							//							if (GuiManager.this.getContainer().size()<=1)
							//								glColor4f(.6f, .6f, .6f, .7f);
							//							else
							glColor4f(.4f, .7f, 1, this.selected ? .7f : .4f);
							RenderHelper.startShape();
							drawRect(a);
						}
						if ((this.selected||GuiGallery.this.lastSelect==this.i)&&GuiManager.this.getContainer().size()<=1) {
							glLineWidth(1);
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
						if (button<=1) {
							if (!this.selected) {
								if (!GuiScreen.isShiftKeyDown()) {
									if (!GuiScreen.isCtrlKeyDown())
										selectAll(false);
								} else
									selectSoFar(this.i);
								if (!isDefault()) {
									GuiGallery.this.labels.put(this, true);
									GuiGallery.this.lastSelect = this.i;
								}
							} else {
								if (!GuiScreen.isCtrlKeyDown()) {
									select(this.i);
								} else
									GuiGallery.this.labels.put(this, false);
							}
						}
						if (button==1) {
							GuiGallery.this.overPanel.openMenu(p);
						}
					}
					return super.mouseClicked(ev, pgp, p, button)||a.pointInside(p);
				}
			}
		}
	}
}