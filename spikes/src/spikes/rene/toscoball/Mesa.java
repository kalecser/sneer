package spikes.rene.toscoball;
//This class controls all the game events
//Mesa(numBalls, Game)

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import javax.swing.JPanel;


class Mesa extends JPanel {
	private final Image mesaImg;
	private final Image[]
	    crossImg,
	    menuImg,
	    barImg;
	private int
		numBalls,
		dist=95,
		subImg=0,
		power=7,
		subImgCounter=0,
		powerCounter=0;
	private boolean
		isPowerRaising=false,
		isRunning=false;
	private double angle=0;
	
	private Star star;
	private Ball whiteBall;
	private Ball[] balls;
	private Hole[] holes;
	private Wall[] walls;
	
	Mesa(int n, Game g) {
		setPreferredSize(new Dimension(512,480));
		setLayout(null);
		numBalls=n+1; //white ball into account
		
		//create all game objects
		
		whiteBall=new Ball(161,281,0,g);
		balls=new Ball[] {
			whiteBall,
			new Ball(321,281,1,g),
			new Ball(345,265,2,g),
			new Ball(345,297,3,g),
			new Ball(369,249,4,g),
			new Ball(369,281,5,g),
			new Ball(369,313,6,g)
		};
		
		holes = new Hole[] {
			new Hole(80,160),
			new Hole(256,144),
			new Hole(432,160),
			new Hole(432,400),
			new Hole(256,416),
			new Hole(80,400)
		};
		
		walls = new Wall[] {
			new Wall(110,128,130,32),
			new Wall(110,400,130,32),
			new Wall(272,128,130,32),
			new Wall(272,400,130,32),
			new Wall(48,190,32,180),
			new Wall(432,190,32,180)
		};
		
		//load images
		
		mesaImg=M.img("mesa.png");
		crossImg =new Image[] {
				M.img("cross0a.png"), M.img("cross0a.png"), M.img("cross1a.png"), M.img("cross2a.png"),
				M.img("cross3a.png"), M.img("cross3a.png"), M.img("cross2a.png"), M.img("cross1a.png"),
				M.img("cross0b.png"), M.img("cross0b.png"), M.img("cross1b.png"), M.img("cross2b.png"),
				M.img("cross3b.png"), M.img("cross3b.png"), M.img("cross2b.png"), M.img("cross1b.png"),
				M.img("cross0c.png"), M.img("cross0c.png"), M.img("cross1c.png"), M.img("cross2c.png"),
				M.img("cross3c.png"), M.img("cross3c.png"), M.img("cross2c.png"), M.img("cross1c.png"),
				M.img("cross0d.png"), M.img("cross0d.png"), M.img("cross1d.png"), M.img("cross2d.png"),
				M.img("cross3d.png"), M.img("cross3d.png"), M.img("cross2d.png"), M.img("cross1d.png")};
			menuImg=new Image[] {
				M.img("menu0.png"), M.img("menu1.png"), M.img("menu2.png"),
				M.img("menu3.png"), M.img("menu4.png"), M.img("menu5.png")};
			barImg=new Image[] {
				M.img("bar0.png"), M.img("bar1.png"), M.img("bar2.png"),
				M.img("bar3.png"), M.img("bar4.png"), M.img("bar5.png"),
				M.img("bar6.png"), M.img("bar7.png"), M.img("bar8.png")};
		
		//track all animated images
		MediaTracker tracker=new MediaTracker(this);
		tracker.addImage(menuImg[0], 0); tracker.addImage(menuImg[1], 1);
		tracker.addImage(menuImg[2], 2); tracker.addImage(menuImg[3], 3);
		tracker.addImage(menuImg[4], 4); tracker.addImage(menuImg[5], 5);
		tracker.addImage(crossImg[0], 6); tracker.addImage(crossImg[1], 7);
		tracker.addImage(crossImg[2], 8); tracker.addImage(crossImg[3], 9);
		tracker.addImage(crossImg[4], 10); tracker.addImage(crossImg[5], 11);
		tracker.addImage(crossImg[6], 12); tracker.addImage(barImg[0], 13);
		tracker.addImage(barImg[1], 14); tracker.addImage(barImg[2], 15);
		tracker.addImage(barImg[3], 16); tracker.addImage(barImg[4], 17);
		tracker.addImage(barImg[5], 18); tracker.addImage(barImg[6], 19);
		tracker.addImage(barImg[7], 20); tracker.addImage(barImg[8], 21);
		//add star's images
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
		
		if (!isRunning) {//intro images
			subImgCounter++; if (subImgCounter>7) {subImgCounter=0; subImg=(subImg+1) % 6;}
			g.drawImage(menuImg[subImg],0,0,null);
			return;
		}

		subImgCounter++; if (subImgCounter>3) {subImgCounter=0;
			Ball.subImg = (Ball.subImg+1) % 8;
		}
		
		g.drawImage(mesaImg,0,0,null);
		for (int i=0; i<numBalls; i++) balls[i].draw(g);
		g.drawImage(barImg[power],320,80,null);
		star.draw(g);
		
		//if all balls are stationary, continue
		for (int i=0; i<numBalls; i++) {if (balls[i].isMoving()) return;}
		
		//draw crosshair
		g.drawImage(crossImg[Ball.subImg+((int)((360-angle)/1.40625) % 4)*8],(int)(whiteBall.x+M.lengthdirx(dist,angle)-7),(int)(whiteBall.y+M.lengthdiry(dist,angle)-7),null);
	
		//update power meter with double delay
		if (subImgCounter==0) {powerCounter++; if (powerCounter==3) {powerCounter=0;
			if (isPowerRaising) {power++; if (power==8) isPowerRaising=false;}
			else {power--; if (power==0) isPowerRaising=true;}
		}}
	}
	
	void step(int amount, int ang) {
		moveCursor(amount, ang);
		if (isRunning) {
			//iterate collisions
			
			for (int i=0; i<numBalls; i++) {
					balls[i].step();
					//ball>ball
					balls[i].checkCollision(balls);
				}
			
				//hole>ball
				for (int i=0; i<6; i++) {holes[i].checkCollision(balls);	}
				
				//wall>ball
				for (int i=0; i<6; i++) {walls[i].checkCollision(balls);	}
			
				//prevent ball smashing
				for (int i=0; i<numBalls; i++) {balls[i].checkExpulse(balls);}
		}
		repaint();
	}
	
	void moveCursor(int amount, int ang) {
		dist=Math.min(250,Math.max(18,dist+amount));
		if (ang==2) ang=1;
		else if (ang==-2) ang=-1;
		else return;
		angle=M.correctAngle(angle+ang*1.40625);
	}
	
	void shoot() {
		for (int i=0; i<numBalls; i++) {if (balls[i].isMoving()) return;}
		if (power==0) power=1;
		whiteBall.shoot(angle, power);
	}
	
	void beginGame() {
		isRunning=true;
	}
	
	void restartGame() {
		for (int i=0; i<numBalls; i++) {balls[i].reset();}
		angle=0;
		dist=95;
		power=7;
	}

	void shineAt(int x, int y) {star.shine(x,y);}
}
