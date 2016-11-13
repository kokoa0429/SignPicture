package com.kamesuta.mc.signpic.plugin.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

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
import com.kamesuta.mc.signpic.gui.SignPicLabel;
import com.kamesuta.mc.signpic.plugin.SignData;
import com.kamesuta.mc.signpic.plugin.packet.PacketHandler;
import com.kamesuta.mc.signpic.plugin.packet.PacketHandler.SignPicturePacket;
import com.kamesuta.mc.signpic.render.RenderHelper;

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
		protected MCoord offset;

		public GuiGallery(final R position) {
			super(position);
			this.offset = MCoord.top(0);
			this.panel = new GalleryPanel(new R(Coord.left(0), this.offset, Coord.right(0), Coord.bottom(0)));
		}

		@Override
		protected void initWidget() {
			add(this.panel);
		}

		@Override
		public boolean mouseScrolled(final WEvent ev, final Area pgp, final Point p, final int scroll) {
			this.offset.stop().add(Easings.easeOutSine.move(.25f, Math.min(0, Math.max(-(GuiManager.this.size/4)*80, this.offset.get()+scroll)))).start();

			return super.mouseScrolled(ev, pgp, p, scroll);
		}

		public class GalleryPanel extends WPanel {
			public GalleryPanel(final R position) {
				super(position);
			}

			@Override
			public void update(final WEvent ev, final Area pgp, final Point p) {
				final Area a = getGuiPosition(pgp);

				int i;
				while ((i = getContainer().size())<=Math.min(((a.h()-GuiGallery.this.offset.get())/80)*4, GuiManager.this.size))
					add(i);

				super.update(ev, pgp, p);
			}

			public void add(final int i) {
				add(new GalleryLabel(new R(Coord.pleft((i%4)/4f), Coord.top((i/4)*80), Coord.pwidth(1f/4f), Coord.height(80)), i));
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
				final Area a = getGuiPosition(pgp);
				if (button<=1) {
					this.selectArea = null;
					this.startSelectPoint = p;
				}
				return super.mouseClicked(ev, pgp, p, button);
			}

			@Override
			public boolean mouseReleased(final WEvent ev, final Area pgp, final Point p, final int button) {
				this.drawSelectArea = false;
				return super.mouseReleased(ev, pgp, p, button);
			}

			@Override
			public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float popacity) {
				super.draw(ev, pgp, p, frame, popacity);
				if (this.drawSelectArea&&this.selectArea!=null) {
					final Area a = getGuiPosition(this.selectArea);
					glColor4f(.25f, .3f, 1, .4f);
					RenderHelper.startShape();
					draw(a, GL_QUADS);
					glLineWidth(1.5f);
					glColor4f(.2f, .3f, 1, .6f);
					draw(a, GL_LINE_LOOP);
				}
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
					if (this.entryId==null||this.entryId==this.Default) {
						final SignData e = GuiManager.this.data.get(this.i);
						if (e!=null) {
							setEntryId(new EntryId(e.sign));
						}
					}

					if (GalleryPanel.this.selectArea!=null) {
						final Area a = getGuiPosition(pgp);
						if (GalleryPanel.this.selectArea.areaOverlap(a))
							this.selected = true;
					}
				}

				@Override
				public void draw(final WEvent ev, final Area pgp, final Point p, final float frame, final float opacity) {
					super.draw(ev, pgp, p, frame, opacity);
					final Area a = getGuiPosition(pgp);
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

				@Override
				public boolean mouseClicked(final WEvent ev, final Area pgp, final Point p, final int button) {
					final Area a = getGuiPosition(pgp);
					if (a.pointInside(p))
						this.selected = !this.selected;
					return super.mouseClicked(ev, pgp, p, button);
				}
			}
		}
	}
}
