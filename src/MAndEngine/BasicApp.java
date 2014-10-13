package MAndEngine;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * constructor should initialize nothing it shouldn't have to
 * because it will be initialized before the main menu is displayed.
 * and before the main menu is displayed some variables and such may give
 * weird values as they are all changing.
 * preferably, everything should be initialized in the initialize method.<br/><br/>
 * please do not implement a game loop in any of these methods.
 * the tick and render take care of that.
 */
public abstract interface BasicApp {

    /**
     * will only get called when setting the window dimension.
     * otherwise, is stored in main temporarily. (avoiding
     * creating 30+ objects per second...)
     */
    public abstract Dimension getResolution();

    /**
     * the proper place to do initialization
     * note: THIS WILL BE CALLED MULTIPLE TIMES.
     * SO KEEP TRACK OF THAT.
     * if you want you app to work with multitasking
     * make sure to deal with this correctly.
     */
    public abstract void initialize ();

    /**
     * resume from a pause, called after initialization
     */
    public abstract void resumeApp ();

    /**
     * do anything that needs to be done to put the app on pause for a moment.
     * only called when exiting and switching to another app.
     */
	public abstract void pauseApp();

    /**
     * method for applying any ticking logic
     */
    public abstract void tick();

    /**
     * to render things to a canvas
     * @param g
     */
    public abstract void render(Graphics2D g);

	public abstract void keyPressed(KeyEvent e);
	public abstract void keyReleased(KeyEvent e);

    /**
     * return the title of the app to be displayed in the
     * top of the screen and on the home menu.
     */
    public abstract String getTitle();

    /**
     * color of outline of app title border.
     * @return
     */
    public abstract Color getColor();

    /**
     * the framerate that this should be run at.
     * @return
     */
    public abstract int getFramerate();

    /**
     * should this window be resizable
     * @return
     */
    public abstract boolean getResizable();

    /**
     * answers the question if this app should be displayed in the menu
     * useful for apps like ... the menu. or maybe you want to
     * link your app to another, you can do that too. just
     * make sure they're both installed!
     * @return
     */
	public abstract boolean visibleInMenu();

	public abstract void resized(int width, int height);

	public abstract void click();
}