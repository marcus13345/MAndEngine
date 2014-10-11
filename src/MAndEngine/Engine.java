package MAndEngine;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.*;

public class Engine extends Canvas implements KeyListener, MouseMotionListener, MouseListener, ContainerListener, ComponentListener {

	/**
	 * to track the x and y
	 */
	public static int mouseX = 0, mouseY = 0;

	/**
	 * if the mouse is down
	 */
	public static boolean mouse = false;

	/**
	 * some number that we use in stuff to animate. move 1/8 the distance when
	 * animating.
	 */
	public static final int ANIMATION_CONSTANT = 8;

	/**
	 * object thing that will initialize our apps for us, then give us an array!
	 */
	private static AppHelper appInitializer;

	/**
	 * AER WE SUPER SPEEDING?!?!?
	 */
	private static boolean overclock = false;

	/**
	 * current framerate and time required to sleep to achieve that framerate.
	 */
	private static int frameSync = 50, sleepTime = 1000 / frameSync;

	/**
	 * variables to track the fps, DON'T WORRY ABOUT IT, PAST YOU HAS YOU
	 * COVERED.
	 */
	private static int framesInCurrentSecond = 0, FPS = 0;

	/**
	 * more framerate stuff, again, chill.
	 */
	private static long nextSecond = System.currentTimeMillis() + 1000, startTime = 0;

	/**
	 * if our current framerate is below our expected. its not directly
	 * calculated that way though. in all reality its calculated by if the
	 * dynamic thread sleep tries to sleep negative or not.
	 */
	private static boolean lag = false;

	/**
	 * this helps debugging, but we keep it private because well, it makes no
	 * sense. BUT IT WORKS SO WHATEVER.
	 */
	private static Stack<LogItem> log = new Stack<LogItem>();

	/**
	 * current width and height. again, don't worry about it so much. PAST YOU,
	 * COVERED. also stores current app ID in array. THIS IS WHY MENU GOES FIRST
	 * IN CFG.
	 */
	private static int WIDTH = 800, HEIGHT = 600, app = 0;

	/**
	 * this bit is important. its the array of apps that we reference later on.
	 */
	public static BasicApp[] apps;

	/**
	 * our window object, probs important.
	 */
	private static JFrame frame;

	/**
	 * basic running condition. don't mess. don't mess. don't mess with the best
	 * cause the best don't mess. don't fool, don't fool. don't fool with the
	 * cool cause the cool don't fool don't bite my apple don't shake my tree.
	 * im an eagle don't mess with me! WORD! AND RESPECT! <CrowSound/>
	 */
	private static boolean running = false;

	/**
	 * FOR PROGRESS BAR DURING THE ERRORING PART OF DRAWING! seriously need to
	 * make that so if a game crashes it doesn't look like its reloading.
	 */
	private static double progress = 1;

	/**
	 * These are fonts. because STANDARDIZATION! worked good for oil, WHY NOT
	 * ME?
	 */
	public static final Font largerFont = new Font("Ubuntu", Font.BOLD, 20);
	public static final Font defaultFont = new Font("Ubuntu", Font.BOLD, 11);

	/**
	 * a place to put your keys. current state. GET IT? tough crowd. ever feel
	 * like the compiler just... ignores my puns?
	 */
	public static boolean[] keys;

	/**
	 * SOMETHING NEW I JUST DID NOW GUISE!
	 */
	private static BufferedImage buffer;
	private static Graphics2D g2;

	/**
	 * the main Main object but staticed so we can like use it from that static
	 * context. its not final because its static but don't change it.
	 */
	private static Engine staticMain;

	/**
	 * SRSLY CALL DYS ONCE. DAS IT. ALL YOU GET. ONE SHOT.
	 */
	public Engine(String[] classes, boolean showLoading) {

		// frame.setVisible(true);

		// set static object
		staticMain = this;

		// initialize keys
		keys = new boolean[512];

		// set up window
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		frame.add(this);
		frame.pack();
		frame.setLocationRelativeTo(null);
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		frame.addContainerListener(this);
		frame.addComponentListener(this);
		
		requestFocus();

		if (showLoading)
			frame.setVisible(true);

		// make a new thread of the appinitializer thing
		// and like... make it do things.
		appInitializer = new AppHelper(classes);
		Thread thread = new Thread(appInitializer);
		thread.start();

		// while its faffing about, RENDER THINGS AND MAKE A LOADY THING
		while (!appInitializer.getDone() || !(progress >= 0.999)) {
			try {
				Thread.sleep(17);
			} catch (Exception e) {
			}
			// this is the later part referred to by a few lines back
			repaint();
			progress += (appInitializer.getProgress() - progress) / ANIMATION_CONSTANT;
		}

		// we done now, gather the loot.
		apps = appInitializer.getApps();

		switchApps(0);
		
		createBuffer();
		
	}

	//TODO at some point redo this to allow frame drop
	//if it gets laggy....
	public void run() {

		// REALLY????
		running = true;

		frame.setVisible(true);

		// now we do stuff.
		while (running) {
			// FPS STUFF WORRY NOT, ITS ALL GOOD. MOVE ALONG.
			startTime = System.currentTimeMillis();
			if (System.currentTimeMillis() > nextSecond) {
				nextSecond += 1000;
				FPS = framesInCurrentSecond;
				framesInCurrentSecond = 0;
			}
			framesInCurrentSecond++;

			// tick stuff
			tick();
			// paint the same stuff
			repaint();

			// FRAMERATE OVERCLOCKING AND SUCH, MOVE ALONG.
			try {
				if (!overclock)
					Thread.sleep((long) Math.floor(sleepTime - (System.currentTimeMillis() - startTime)));
				else
					Thread.sleep(0);
				lag = false;
			} catch (Exception e) {
				lag = true;
			}
		}
	}

	/**
	 * makes a buffer and stuff, called with new windows and things. MOVE ALONG
	 */
	private static void createBuffer() {
		buffer = (new BufferedImage(WIDTH, HEIGHT, BufferedImage.TRANSLUCENT));
		g2 = (Graphics2D) buffer.getGraphics();
	}

	// later make this NOT initialize something new every go...
	// PROBABLY A GOOD IDEA PAST MARCUS, IMMA DO THAT NOW!
	// ~present Marcus
	// thank you my minions ~Future Marcus
	public void update(Graphics g) {
		// Graphics g2 = buffer.getGraphics();
		
		if(buffer.getWidth() != WIDTH || buffer.getHeight() != HEIGHT) {
			System.out.println("bork " + buffer.getWidth());
			System.out.println("bork " + WIDTH);
			
			createBuffer();
		}
		
		paint(g2);
		g.drawImage(buffer, 0, 0, null);
	}

	/**
	 * THIS THING, SWITCHES APPS N STUFF! WOO!
	 * 
	 * @param i
	 * @return
	 */
	public static boolean switchApps(int i) {
		try {
			log("pausing " + apps[app].getTitle());
			apps[app].pauseApp();
			app = i;
			log("initializing " + apps[app].getTitle());
			apps[app].initialize();
			log("resuming " + apps[app].getTitle());
			apps[app].resumeApp();
			log("setting window properties");
			setWindowProperties(apps[app]);
			log("Started up " + apps[app].getTitle());
			
			frame.pack();
			
			// because we now use the ONE buffer system... yeah
			// lets do something about thaaaaaaaaat...

			return true;

		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * sets the window properties for a given app
	 * 
	 * @param app
	 */
	private static void setWindowProperties(BasicApp app) {
		setWindowProperties(app.getResolution(), app.getFramerate(), app.getResizable());
	}

	/**
	 * sets the window properties without an app having to be specified.
	 * 
	 * @param dimension
	 * @param fps
	 * @param resizable
	 */
	private static void setWindowProperties(Dimension dimension, int fps, boolean resizable) {
		frame.setResizable(resizable);
		staticMain.setSize(dimension);
		frame.pack();
		frame.setLocationRelativeTo(null);
		WIDTH = dimension.width;
		HEIGHT = dimension.height;
		setFramerate(fps);
		frame.setResizable(resizable);

	}

	public void paint(Graphics g) {// oh.....

		Graphics2D g2d = (Graphics2D) g;

		render(g2d);

	}

	private static void render(Graphics2D g) {

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setFont(defaultFont);

		try {
			// set default graphics shit
			g.setFont(defaultFont);
			g.setColor(Color.WHITE);

			// try and do the normal thing
			apps[app].render(g);

			// aaaaand back to us!
			g.setFont(defaultFont);
			g.setColor(Color.WHITE);

			// show fps if debug level high enough
			g.drawString("FPS: " + FPS, 20, 20);
			if (overclock)
				g.drawString("Overclocking!", 20, 35);
			g.setColor(Color.RED);
			if (lag)
				g.fillOval(10, 10, 10, 10);

			g.setColor(Color.WHITE);
			if (!(log.size() == 0))
				for (int i = log.size() - 1; i >= 0; i--)
					log.elementAt(i).render(g, WIDTH - 200, HEIGHT - 10 - (i * 12));
		} catch (Exception e) {
			g.setFont(largerFont);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, WIDTH, HEIGHT);
			g.setColor(Color.GREEN);
			g.fillRect(100, 300, (int) (progress * 600), 20);
			g.setColor(Color.RED);
			g.drawRect(100, 300, 600, 20);
			g.setColor(Color.BLUE);
			g.drawString("Loading", WIDTH / 2 - 40, HEIGHT / 2 - 200);

		}

	}

	private void tick() {
		apps[app].tick();
		for (int i = 0; i < log.size(); i++)
			log.elementAt(i).tick();

		int i = 0;
		while (i < log.size()) {
			if (!log.elementAt(i).getAlive())
				log.remove(i);
			else
				i++;
		}

		while (log.size() > 10) {
			log.pop();
		}
	}

	public static void log(String s) {
		log.insertElementAt(new LogItem(s, 100), 0);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		apps[app].keyPressed(e);
		if (e.getKeyCode() == KeyEvent.VK_O && keys[KeyEvent.VK_CONTROL]) {
			overclock = !overclock;
		}
		keys[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		apps[app].keyReleased(e);
		keys[e.getKeyCode()] = false;
	}
	
	@Override
	public void keyTyped(KeyEvent arg0) {
	}
	
	public static void exit() {
		frame.dispose();
		System.exit(0);
	}
	
	private static void setFramerate(int fps) {
		frameSync = fps;
		sleepTime = 1000 / frameSync;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		mouse = true;
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		mouse = false;
	}

	@Override
	public void componentAdded(ContainerEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentRemoved(ContainerEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		setSize(getPreferredSize());
		System.out.println("HEIGHT: " + HEIGHT);
		System.out.println("WIDTH:  " + WIDTH);
		WIDTH = getSize().width;
		HEIGHT = getSize().height;
		createBuffer();
		apps[app].resized(WIDTH, HEIGHT);
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}