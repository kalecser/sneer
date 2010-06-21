package spikes.rene.toscoball;
//This class controls all the game events
//Mesa(number of balls, Game)

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import javax.swing.JPanel;



public class Mesa extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Image mesaimg=img("mesa.png");
	private static final Image[] cross =new Image[] {
		img("cross0a.png"), img("cross0a.png"),
		img("cross1a.png"), img("cross2a.png"),
		img("cross3a.png"), img("cross3a.png"),
		img("cross2a.png"), img("cross1a.png"),
		img("cross0b.png"), img("cross0b.png"),
		img("cross1b.png"), img("cross2b.png"),
		img("cross3b.png"), img("cross3b.png"),
		img("cross2b.png"), img("cross1b.png"),
		img("cross0c.png"), img("cross0c.png"),
		img("cross1c.png"), img("cross2c.png"),
		img("cross3c.png"), img("cross3c.png"),
		img("cross2c.png"), img("cross1c.png"),
		img("cross0d.png"), img("cross0d.png"),
		img("cross1d.png"), img("cross2d.png"),
		img("cross3d.png"), img("cross3d.png"),
		img("cross2d.png"), img("cross1d.png")},
	menuimg=new Image[] {
		img("menu0.png"), img("menu1.png"),
		img("menu2.png"), img("menu3.png"),
		img("menu4.png"), img("menu5.png")},
	barimg=new Image[] {
		img("bar0.png"), img("bar1.png"), img("bar2.png"),
		img("bar3.png"), img("bar4.png"), img("bar5.png"),
		img("bar6.png"), img("bar7.png"), img("bar8.png")};
	
	
	private static double angle=0;
	private static int numBalls, numHoles, numWalls, dist=95, c=0, p=0, ppl=0, power=7;
	private static boolean pl=true;
	private Star star;
	Ball whiteball;
	Ball[] balls;
	Hole[] holes;
	Wall[] walls;
	public boolean isRunning=false;
	
	public Mesa(int n, Game g) {
		setPreferredSize(new Dimension(512,480));
		setLayout(null);
		numBalls=n+1; //white ball into account
		numHoles=6;
		numWalls=6;
		
		//create all game objects
		
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
		
		//track all animated images
		
		MediaTracker tracker=new MediaTracker(this);
		tracker.addImage(menuimg[0], 0); tracker.addImage(menuimg[1], 1);
		tracker.addImage(menuimg[2], 2); tracker.addImage(menuimg[3], 3);
		tracker.addImage(menuimg[4], 4); tracker.addImage(menuimg[5], 5);
		tracker.addImage(cross[0], 6); tracker.addImage(cross[1], 7);
		tracker.addImage(cross[2], 8); tracker.addImage(cross[3], 9);
		tracker.addImage(cross[4], 10); tracker.addImage(cross[5], 11);
		tracker.addImage(cross[6], 12); tracker.addImage(barimg[0], 13);
		tracker.addImage(barimg[1], 14); tracker.addImage(barimg[2], 15);
		tracker.addImage(barimg[3], 16); tracker.addImage(barimg[4], 17);
		tracker.addImage(barimg[5], 18); tracker.addImage(barimg[6], 19);
		tracker.addImage(barimg[7], 20); tracker.addImage(barimg[8], 21);
		//load star's images
		star=new Star(tracker);
		
		try {tracker.waitForAll();}
		catch (InterruptedException e) {}
		
		if (tracker.isErrorAny()) {
			System.err.print("Unable to load some images.");
			System.exit(1);
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		stepMesa(g);
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
	
	public void restartGame() {
		for (int i=0; i<numBalls; i++) {
			balls[i].resetPos();
			balls[i].isAlive=true;
		}
		angle=0;
		dist=95;
		power=7;
	}
	
	private void stepMesa(Graphics g) {
		if (!isRunning) {
			c++;
			if (c>7) {c=0; p=(p+1) % 6;}
			g.drawImage(menuimg[p],0,0,null);
			return;
		}

		c++; if (c>3) {
			Ball.k = (Ball.k+1) % 8;
			c=0;
		}
		p=((int)((360-angle)/1.40625) % 4)*8;

		g.drawImage(mesaimg,0,0,null);
		for (int i=0; i<numBalls; i++) balls[i].draw(g);
		star.draw(g);
		g.drawImage(barimg[power],320,80,null);
		for (int i=0; i<numBalls; i++) {if (balls[i].isMoving()) return;}
		g.drawImage(cross[Ball.k+p],(int)(whiteball.x+M.lengthdirx(dist,angle)-7),(int)(whiteball.y+M.lengthdiry(dist,angle)-7),14,14,null);
		if (c==0) {
			ppl++;
			if (ppl==3) {
				ppl=0;
				if (pl) {power++; if (power==8) pl=false;}
				else {power--; if (power==0) pl=true;}
			}
		}
	}
	
	public void turn(int ang) {
		angle=M.correctAngle(angle+ang*1.40625);
	}
	
	public void space(int num) {
		dist = Math.min(250,Math.max(18,dist+num));
	}
	
	public void shoot() {
		for (int i=0; i<numBalls; i++) {if (balls[i].isMoving()) return;}
		if (power==0) power=1;
		whiteball.shoot(angle, power);
	}
	
	public void shine(int x, int y) {
		star.shine(x,y);
	}
	
	private static Image img(String name) {
		return Toolkit.getDefaultToolkit().getImage(Ball.class.getResource("images/"+name));
	}
	
}
