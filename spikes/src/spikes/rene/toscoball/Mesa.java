package spikes.rene.toscoball;
//This class controls all the balls' functionality
//Mesa(number of balls, window, game)

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class Mesa extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Image
	menuimg=Toolkit.getDefaultToolkit().getImage("menu.png"),
	mesaimg=Toolkit.getDefaultToolkit().getImage("mesa.png");
	private static int numBalls, numHoles, numWalls;
	Ball whiteball;
	Ball[] balls;
	Hole[] holes;
	Wall[] walls;
	JFrame window;
	public boolean isRunning=false;
	
	public Mesa(int n, JFrame w, Game g) {
		setPreferredSize(new Dimension(640,480));
		window=w;
		numBalls=n+1; //white ball into account
		numHoles=4;
		numWalls=2;
		balls = new Ball[numBalls];
		for (int i=0; i<numBalls; i++) balls[i]=new Ball(400,240,false, w, g);
		holes = new Hole[numHoles];
		holes[0]=new Hole(48,48);
		holes[1]=new Hole(592,48);
		holes[2]=new Hole(592,432);
		holes[3]=new Hole(48,432);
		walls = new Wall[numWalls];
		walls[0]=new Wall(32,128,32,256);
		walls[1]=new Wall(576,128,32,256);
		
		whiteball=new Ball(150,240,true, w, g);
		balls[numBalls-1]=whiteball;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawFundo(g);
		if (isRunning) {
			drawThings(g);
			drawTaco(g);
		}
	}
	
	public void stepBalls() {
		if (!isRunning) return;
		for (int i=0; i<numBalls; i++) {
			if (balls[i].isAlive) { 
				balls[i].step();
				for (int i2=0; i2<numBalls; i2++) {
					if (i!=i2 & balls[i2].isAlive) {
						balls[i].checkCollision(balls[i2]);
						//prevent crowding
						balls[i].checkExpulse(balls[i2]);
					}
				}
				for (int i2=0; i2<numHoles; i2++) {
					holes[i2].checkCollision(balls[i]);
				}
				for (int i2=0; i2<numWalls; i2++) {
					walls[i2].checkCollision(balls[i]);
				}
			}
		}
	}
	
	private void drawFundo(Graphics g) {
		g.drawImage((isRunning ? mesaimg : menuimg), 0, 0, null);
	}
	
	private void drawThings(Graphics g) {
		for (int i=0; i<numBalls; i++) balls[i].draw(g);
		for (int i=0; i<numHoles; i++) holes[i].draw(g);
		for (int i=0; i<numWalls; i++) walls[i].draw(g);
	}
	
	private void drawTaco(Graphics g) {
		for (int i=0; i<numBalls; i++) {if (balls[i].isMoving()) return;}
		
		int x=MouseInfo.getPointerInfo().getLocation().x-window.getX()-4;
		int y=MouseInfo.getPointerInfo().getLocation().y-window.getY()-30;
		double d=Math.atan2(whiteball.y-y,x-whiteball.x);
		double xx=whiteball.x+30*Math.cos(d);
		double yy=whiteball.y-30*Math.sin(d);
		g.drawLine(x,y,(int)xx,(int)yy);
	}
	
	public void shoot(MouseEvent e) {
		whiteball.shoot(e);
	}
	
}
