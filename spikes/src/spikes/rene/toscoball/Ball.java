package spikes.rene.toscoball;
//a billiards ball with physics
//Ball(X ,Y)

import java.awt.Graphics;
import java.awt.Image;


class Ball {

	int id;
	static int subImg;
	double x,y,xprev,yprev,xstart,ystart;
	private double
		speed,
		direction=0;
	private static final double friction=0.0152;
	private static final Image[] ballspr=new Image[] {
		M.img("bola1.png"), M.img("bola2.png"), M.img("bola3.png"),
		M.img("bola4.png"), M.img("bola5.png"), M.img("bola6.png"),
		M.img("bolab0.png"), M.img("bolab0.png"),
		M.img("bolab1.png"), M.img("bolab2.png"),
		M.img("bolab3.png"), M.img("bolab3.png"),
		M.img("bolab2.png"), M.img("bolab1.png")};
	boolean isAlive;
	private boolean isWhite=false;
	private Mesa mesa;
	
	Ball(int i, int j, int n, Mesa m) {
		xstart=i;
		ystart=j;
		reset();
		id=n;
		isWhite=(id==0);
		mesa=m;
	}
	
	void draw(Graphics g) {
		if (!isAlive) return;
		int sub=id-1;
		if (isWhite) {sub=6+subImg;}
		g.drawImage(ballspr[sub],(int)x-11,(int)y-11,null);
	}
	
	boolean isMoving() {
		if (!isAlive) return false;
		return (speed>0 ? true : false);
	}
	
	void step() {
		if (!isAlive) return;
		direction=M.correctAngle(direction);
		xprev=x; yprev=y;
		speed=Math.max(0,Math.min(speed,1000)-friction);
		x+=M.lengthdirx(speed,direction);
		y+=M.lengthdiry(speed,direction);
	}

	void contactWall(Wall w) {
		double
			xf=Math.cos(direction*M.degtorad),
			yf=-Math.sin(direction*M.degtorad);
		
		x=xprev;
		y=yprev;
		int count=(int)Math.ceil(speed);
		while (count>0) {
			count--;
			x+=xf; y+=yf;
			if (w.intersects(this)) break;
		}
	}
	
	void checkCollision(Ball[] balls) {
		for (int i=0; i<balls.length; i++) {collide(balls[i]);	}
	}
	private void collide(Ball other) {
		if (!isAlive | !other.isAlive | id==other.id) return;
			
		if (speed==0 & other.speed==0) {
			expulse(other, 1);
			return;
		}
		if (M.pointDistance(x,y,other.x,other.y)<22) { 
			
			//back-stepping to expulse balls instantly
			double xfactor=0,yfactor=0,oxf=0,oyf=0;
			if (speed>0) {
			xfactor=M.lengthdirx(1,direction);
			yfactor=M.lengthdiry(1,direction);}
			if (other.speed>0) {
			oxf=M.lengthdirx(1,other.direction);
			oyf=M.lengthdiry(1,other.direction);}
			int maxAllowedDist=(int)Math.ceil(Math.max(speed,other.speed));
			for (int i=0; i<maxAllowedDist; i++) {
				if (i>maxAllowedDist | M.sqr(x-other.x)+M.sqr(y-other.y)>M.sqr(22)) break;
				x-=xfactor; y-=yfactor;
				other.x-=oxf; other.y-=oyf;
			}
			
			//next find out how much force each ball applies to the other
			double mydir=M.pointDirection(x,y,other.x,other.y);
			double hisdir=M.correctAngle(mydir+180);
			double myForce=Math.max(0,Math.cos(M.degtorad*(direction-mydir))*speed);
			double hisForce=Math.max(0,Math.cos(M.degtorad*(other.direction-hisdir))*other.speed);

			//update the speed and direction of the local ball
			double horFactor=M.lengthdirx(speed,direction)+M.lengthdirx(hisForce+myForce,hisdir);
			double verFactor=M.lengthdiry(speed,direction)+M.lengthdiry(hisForce+myForce,hisdir);
			direction=M.pointDirection(0,0,horFactor,verFactor);
			speed=M.pointDistance(0,0,horFactor,verFactor);
			
			//and the remote ball, applying the new forces
			horFactor=M.lengthdirx(other.speed,other.direction)+M.lengthdirx(myForce+hisForce,mydir);
			verFactor=M.lengthdiry(other.speed,other.direction)+M.lengthdiry(myForce+hisForce,mydir);
			other.direction=M.pointDirection(0,0,horFactor,verFactor);
			other.speed=M.pointDistance(0,0,horFactor,verFactor);
		}
		
	}
	
	void checkExpulse(Ball[] balls) {
		for (int i=0; i<balls.length; i++) {expulse(balls[i],0);}
	}
	private void expulse(Ball other, int factor) {
		if (!isAlive | !other.isAlive | id==other.id) return;
		double distance=M.pointDistance(x,y,other.x,other.y)/2;
		if (distance>11+factor) return;
		else if (distance<9) {
			x+=Math.random()-0.5;
			y+=Math.random()-0.5;
		}
		double mydir=M.pointDirection(x,y,other.x,other.y);
		double hisdir=M.correctAngle(mydir+180);
		double middleX=x+M.lengthdirx(distance,mydir);
		double middleY=y+M.lengthdiry(distance,mydir);
		x=middleX+M.lengthdirx(12,hisdir);
		y=middleY+M.lengthdiry(12,hisdir);
		other.x=middleX+M.lengthdirx(12,mydir);
		other.y=middleY+M.lengthdiry(12,mydir);
		xprev=x;
		yprev=y;
		other.xprev=other.x;
		other.yprev=other.y;
	}
	
	void bounce(double angle) {
		direction=M.correctAngle(-(direction-angle)+angle);
	}
	
	void shoot(double a, int p) {
		speed=p;
		direction=a;
	}
	
	void die() {
		if (isWhite) mesa.loseGame();
		else {//fell in a hole
			isAlive=false;
			mesa.shineAt((int)x,(int)y);
		}
	}
	
	void reset() {
		x=xstart;
		y=ystart;
		speed=0;
		isAlive=true;
	}
}
