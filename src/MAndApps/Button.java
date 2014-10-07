package MAndApps;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import static MAndApps.Engine.mouseX;
import static MAndApps.Engine.mouseY;
import static MAndApps.Engine.mouse;

public class Button {
	private int clickLevel = 0;
	private Image normal, hover, down;
	private int x, y, width, height;
	private int clicks = 0;
	private int leftPadding, bottomPadding;
	private String text;
	private static final int DEPTH_ON_CLICK = 7;
	private double depth = DEPTH_ON_CLICK, desiredDepth = DEPTH_ON_CLICK;
	private int ID;
	private boolean active;

	private static final int shadowOffset = 10;
	private static boolean debug = false;
	
	public static void setDebug(boolean debug) {
		Button.debug = debug;
	}
	
	public Button(int id, Color c, int x, int y, int width, int height, String text, int leftPadding, int bottomPadding) {
		this(id, c, x, y, width, height, text, leftPadding, bottomPadding, true);
	}
	
	public Button(int id, Color c, int x, int y, int width, int height, String text, int leftPadding, int bottomPadding, boolean active) {

		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		normal = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		hover = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		down = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);

		normal = ImageCreator.createImageCloud(width, height, c);
		hover = ImageCreator.createImageCloud(width, height, c.brighter());
		down = ImageCreator.createImageCloud(width, height, c.darker());

		this.text = text;
		this.leftPadding = leftPadding;
		this.bottomPadding = bottomPadding;

		this.ID = id;

		this.active = active;
	}

	public void poll() {

		if (active) {
			// figure out the new click level of the button and add a click if
			// we clicked it
			if (clickLevel == 0) {
				if (inBoundingBox(mouseX, mouseY) && !mouse) {
					clickLevel = 1;
				}
			} else if (clickLevel == 1) {
				if (inBoundingBox(mouseX, mouseY) && mouse) {
					clickLevel = 2;
				} else {
					if (!inBoundingBox(mouseX, mouseY)) {
						clickLevel = 0;
					}
				}
			} else if (clickLevel == 2) {
				if (inBoundingBox(mouseX, mouseY) && !mouse) {
					clickLevel = 1;
					clicks++;

				} else if (!inBoundingBox(mouseX, mouseY)) {
					clickLevel = 3;
				}
			} else if (clickLevel == 3) {
				if (!mouse) {
					clickLevel = 0;
				} else {
					if (inBoundingBox(mouseX, mouseY)) {
						clickLevel = 2;
					}
				}
			}
		}else{
			clickLevel = 4;
		}
		// uh... UIFM
		desiredDepth = clickLevel == 1 ? 0 : clickLevel == 2 ? DEPTH_ON_CLICK * 2 : DEPTH_ON_CLICK;
		depth += ((desiredDepth - depth) / 4d);
	}

	public boolean hasNextClick() {
		if (clicks > 0) {
			clicks--;
			return true;
		} else
			return false;
	}

	public void render(Graphics2D g) {

		// draw shadow

		// set color to black
		g.setColor(Color.BLACK);

		// set alpha for draw
		AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
		g.setComposite(composite);

		// draw
		g.fillRect(x + shadowOffset - (int)((depth - 10)/2d), y + shadowOffset, width, height);

		// reset alpha
		composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
		g.setComposite(composite);

		// depending on the click level, draw the corresponding representative
		// image
		// tightened that up a bit by making the image an in line if
		g.drawImage((clickLevel == 1 ? hover : (clickLevel == 2 || clickLevel == 4) ? down : normal), x + (int) (depth - DEPTH_ON_CLICK), y + (int) (depth - DEPTH_ON_CLICK), null);

		// where to put the text
		// edit:
		// okay seriously this makes no sense.
		// FIX! i don't know what to fix though, its THAT borked.
		// edit:
		// FIXED! FUCK IT! I REMOVED IT ALL!

		// see that code ^^^^^^^
		// no?
		// good.
		// so i decided to bring it back... xD im not quite sure
		// what was here but... text is going here now so :P
		g.setColor(Color.BLACK);
		g.drawString(text, x + leftPadding + (int) (depth - DEPTH_ON_CLICK), y - bottomPadding + height + (int) (depth - DEPTH_ON_CLICK));

		// because depth is complicated, thats why.
		if (debug) {
			// render the bounding box...
			g.setColor(Color.RED);
			int x = this.x + (depth < DEPTH_ON_CLICK ? (int) (depth - DEPTH_ON_CLICK) : 0);
			int y = this.y + (depth < DEPTH_ON_CLICK ? (int) (depth - DEPTH_ON_CLICK) : 0);
			int width = this.width - (depth < DEPTH_ON_CLICK ? (int) (depth - DEPTH_ON_CLICK) : 0) + (depth > DEPTH_ON_CLICK ? (int) (depth - DEPTH_ON_CLICK) : 0);
			int height = this.height - (depth < DEPTH_ON_CLICK ? (int) (depth - DEPTH_ON_CLICK) : 0) + (depth > DEPTH_ON_CLICK ? (int) (depth - DEPTH_ON_CLICK) : 0);
			g.drawRect(x, y, width, height);
		}

	}

	private boolean inBoundingBox(int mouseX, int mouseY) {
		int x = this.x + (depth < DEPTH_ON_CLICK ? (int) (depth - DEPTH_ON_CLICK) : 0);
		int y = this.y + (depth < DEPTH_ON_CLICK ? (int) (depth - DEPTH_ON_CLICK) : 0);
		int width = this.width - (depth < DEPTH_ON_CLICK ? (int) (depth - DEPTH_ON_CLICK) : 0) + (depth > DEPTH_ON_CLICK ? (int) (depth - DEPTH_ON_CLICK) : 0);
		int height = this.height - (depth < DEPTH_ON_CLICK ? (int) (depth - DEPTH_ON_CLICK) : 0) + (depth > DEPTH_ON_CLICK ? (int) (depth - DEPTH_ON_CLICK) : 0);
		return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
	}

	public void updatePosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void move(int dx, int dy) {
		x += dx;
		y += dy;
	}

	public void resetButtonState() {
		clickLevel = 0;
	}

	public String getNameID() {
		return text.toUpperCase().replace(" ", "");
	}

	public int getID() {
		return ID;
	}

	public void changeName(String name) {
		this.text = name;
	}
}
