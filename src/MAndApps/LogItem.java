package MAndApps;

import java.awt.Graphics;

public class LogItem {
	private int life, LIFE;
	private boolean alive;
	private String log;
	public LogItem(String s, int life){
		log = s;
		alive = true;
		LIFE = life;
		this.life = 0;
		// each instance of the word life, 
		//is one of three different variable :P
		//confused?
		//this.life is the current life counter
		//LIFE is the max counter before it dies
		//life is the number passed in to set as
		//the LIFE or max life
		//confused now?
	}
	
	public void tick(){
		life++;
		if(life >= LIFE){
			alive = false;
		}
	}
	
	public void render(Graphics g, int x, int y){
		if (alive)g.drawString(log, x, y);
	}

	public boolean getAlive() {
		return alive;
	}
}
