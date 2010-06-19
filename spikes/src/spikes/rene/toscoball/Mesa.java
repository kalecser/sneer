package spikes.rene.toscoball;
//This class controls all the balls' functionality
//and the background graphics.
//Mesa(number of balls, window, game)

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class Mesa extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Image
	menuimg=Toolkit.getDefaultToolkit().getImage(Mesa.class.getResource("menu.png")),
	mesaimg=Toolkit.getDefaultToolkit().getImage(Mesa.class.getResource("mesa.png"));
	private static final Image[] cross =new Image[] {
		img("cross0.png"),
		img("cross0.png"),
		img("cross1.png"),
		img("cross2.png"),
		img("cross3.png"),
		img("cross3.png"),
		img("cross2.png"),
		img("cross1.png")};
	private static double angle=0;
	private static int numBalls, numHoles, numWalls, dist=95, c=0;
	Ball whiteball;
	Ball[] balls;
	Hole[] holes;
	Wall[] walls;
	JFrame window;
	public boolean isRunning=false;
	
	public Mesa(int n, JFrame w, Game g) {
		setPreferredSize(new Dimension(512,480));
		window=w;
		numBalls=n+1; //white ball into account
		numHoles=6;
		numWalls=6;
		//balls = new Ball[numBalls];
		//for (int i=0; i<numBalls; i++) balls[i]=new Ball(400,240,i+1, g);
		balls=new Ball[] {
				new Ball(161,281,0,g),
				new Ball(321,281,1,g),
				new Ball(345,265,2,g),
				new Ball(345,297,3,g),
				new Ball(369,249,4,g),
				new Ball(369,281,5,g),
				new Ball(369,313,6,g),
		};
		whiteball=balls[0];
		
		holes = new Hole[numHoles];
		holes[0]=new Hole(80,160);
		holes[1]=new Hole(256,144);
		holes[2]=new Hole(432,160);
		holes[3]=new Hole(432,400);
		holes[4]=new Hole(256,416);
		holes[5]=new Hole(80,400);
		
		walls = new Wall[numWalls];
		walls[0]=new Wall(110,128,130,32);
		walls[1]=new Wall(110,400,130,32);
		walls[2]=new Wall(272,128,130,32);
		walls[3]=new Wall(272,400,130,32);
		walls[4]=new Wall(48,190,32,180);
		walls[5]=new Wall(432,190,32,180);
		
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
		
		//iterate collisions
		for (int i=0; i<numBalls; i++) {
			if (balls[i].isAlive) { 
				balls[i].step();
				//ball>ball
				for (int i2=0; i2<numBalls; i2++) {
					if (i!=i2) balls[i].checkCollision(balls[i2]);
				}
				//hole>ball
				for (int i2=0; i2<numHoles; i2++) {
					holes[i2].checkCollision(balls[i]);
				}
				//wall>ball
				for (int i2=0; i2<numWalls; i2++) {
					walls[i2].checkCollision(balls[i]);
				}
				//prevent ball smashing
				for (int i2=0; i2<numBalls; i2++) {
					if (i!=i2) balls[i].expulse(balls[i2], 0);
				}
			}
		}
	}
	
	private void drawFundo(Graphics g) {
		g.drawImage((isRunning ? mesaimg : menuimg), 0, 0, null);
	}
	
	private void drawThings(Graphics g) {
		c++;
		if (c==4) {Ball.k = (Ball.k+1) % 8; c=0;}
		for (int i=0; i<numBalls; i++) balls[i].draw(g);
	}
	
	private void drawTaco(Graphics g) {
		for (int i=0; i<numBalls; i++) {if (balls[i].isMoving()) return;}
		g.drawImage(cross[Ball.k],(int)(whiteball.x+M.lengthdirx(dist,angle)-7),(int)(whiteball.y+M.lengthdiry(dist,angle)-7),14,14,null);
		
	}
	
	public void turn(int ang) {
		angle=M.correctAngle(angle+ang*1.40625);
	}
	
	public void space(int num) {
		dist=Math.min(250,Math.max(18,dist+num));
	}
	
	public void shoot() {
		for (int i=0; i<numBalls; i++) {if (balls[i].isMoving()) return;}
		whiteball.shoot(angle);
	}
	
	private static Image img(String name) {
		return Toolkit.getDefaultToolkit().getImage(Ball.class.getResource(name));
	}
	
}
