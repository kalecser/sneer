package spikes.rene.toscoball;
//a physically genuine 2D sphere with collision
//Ball(X pos ,Y pos)

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;


public class Ball {

	private int id;
	double x,y,xprev,yprev,xstart,ystart;
	private double
		speed,
		direction=0;
	static int k;
	private static final int
	minx=11+48,
	miny=11+128,
	maxx=464-11,
	maxy=432-11;
	private static final double friction=0.0152;
	private static final Image[] ballspr=new Image[] {
		img("bola1.png"), img("bola2.png"),
		img("bola3.png"), img("bola4.png"),
		img("bola5.png"), img("bola6.png"),
		img("bolab0.png"), img("bolab0.png"),
		img("bolab1.png"), img("bolab2.png"),
		img("bolab3.png"), img("bolab3.png"),
		img("bolab2.png"), img("bolab1.png")};
	
	private boolean isWhite=false;
	public boolean isAlive=true;
	private Game game;
	
	public Ball(int i, int j, int n, Game g) {
		xstart=i;
		ystart=j;
		resetPos();
		id=n;
		isWhite=(id==0);
		game=g;
	}
	
	public void draw(Graphics g) {
		if (!isAlive) return;
		int sub=id-1;
		if (isWhite) {sub=6+k;}
		g.drawImage(ballspr[sub],(int)x-11,(int)y-11,null);
	}
	
	public boolean isMoving() {
		if (!isAlive) return false;
		return (speed>0 ? true : false);
	}
	
	public void step() {
		direction=M.correctAngle(direction);
		xprev=x; yprev=y;
		speed=Math.max(0,Math.min(speed,1000)-friction);
		x+=M.lengthdirx(speed,direction);
		y+=M.lengthdiry(speed,direction);
		if (x<minx | x>maxx) {
			bounce(90);
			x=Math.max(minx,Math.min(maxx,x));
		}
		if (y<miny | y>maxy) {
			bounce(0);
			y=Math.max(miny,Math.min(maxy,y));
		}
	}

	public void contactWall(Wall w) {
		double
			xf=Math.cos(direction*M.degtorad),
			yf=-Math.sin(direction*M.degtorad);
		
		x=xprev;
		y=yprev;
		int count=(int)Math.ceil(speed);
		while (count>0) {
			count--;
			x+=xf; y+=yf;
			if (w.isIn(this)) break;
		}
	}
	
	public void checkCollision(Ball other) {
		if (isAlive & other.isAlive) {
			
			if (speed==0 & other.speed==0) {
				expulse(other, 1);
				return;
			}
			if (M.pointDistance(x,y,other.x,other.y)<22) { 
				
				double xf=0,yf=0,oxf=0,oyf=0;
				if (speed>0) {
				xf=M.lengthdirx(1,direction);
				yf=M.lengthdiry(1,direction);}
				if (other.speed>0) {
				oxf=M.lengthdirx(1,other.direction);
				oyf=M.lengthdiry(1,other.direction);}
				
				int lol=(int)Math.ceil(Math.max(speed,other.speed));
				
				for (int i=0; i<lol; i++) {
					if (i>lol | M.sqr(x-other.x)+M.sqr(y-other.y)>M.sqr(22)) break;
					x-=xf; y-=yf;
					other.x-=oxf; other.y-=oyf;
				}
	
				
				
				//next find out how much force each ball applies to the other
				double mydir=M.pointDirection(x,y,other.x,other.y);
				double hisdir=M.correctAngle(mydir+180);
				double mytowmod=Math.max(0,Math.cos(M.degtorad*(direction-mydir))*speed);
				double histowmod=Math.max(0,Math.cos(M.degtorad*(other.direction-hisdir))*other.speed);
	
				//store the amount of initial momentum for later corrections
				//double total=(speed+other.speed);
	
				//update the speed and direction of the local ball
				double hcomponent=M.lengthdirx(speed,direction)+M.lengthdirx(histowmod+mytowmod,hisdir);
				double vcomponent=M.lengthdiry(speed,direction)+M.lengthdiry(histowmod+mytowmod,hisdir);
				direction=M.pointDirection(0,0,hcomponent,vcomponent);
				speed=M.pointDistance(0,0,hcomponent,vcomponent);
				
				//and the remote ball, applying the new forces
				hcomponent=M.lengthdirx(other.speed,other.direction)+M.lengthdirx(mytowmod+histowmod,mydir);
				vcomponent=M.lengthdiry(other.speed,other.direction)+M.lengthdiry(mytowmod+histowmod,mydir);
				other.direction=M.pointDirection(0,0,hcomponent,vcomponent);
				other.speed=M.pointDistance(0,0,hcomponent,vcomponent);
				
				/* This code is making balls too slow.
				
				//then cap resulting energy to avoid 'explode'
				if (speed==0) other.speed=total;
				else if (other.speed==0) speed=total;
				else {
					double factor=speed/total;
					speed=total*(factor);
					other.speed=total*(1-factor);
				}*/
			}
		}
	}
	
	public void expulse(Ball other, int fac) {
		double dist=M.pointDistance(x,y,other.x,other.y)/2;
		if (dist>11+fac) return;
		else if (dist<9) {
			x+=Math.random()-0.5;
			y+=Math.random()-0.5;
		}
		double mydir=M.pointDirection(x,y,other.x,other.y);
		double hisdir=M.correctAngle(mydir+180);
		double midx=x+M.lengthdirx(dist,mydir);
		double midy=y+M.lengthdiry(dist,mydir);
		x=midx+M.lengthdirx(12,hisdir);
		y=midy+M.lengthdiry(12,hisdir);
		other.x=midx+M.lengthdirx(12,mydir);
		other.y=midy+M.lengthdiry(12,mydir);
		xprev=x;
		yprev=y;
		other.xprev=other.x;
		other.yprev=other.y;
	}
	
	public void bounce(double angle) {
		direction=M.correctAngle(-(direction-angle)+angle);
	}
	
	public void shoot(double a, int p) {
		speed=p;
		direction=a;
	}
	
	public void die() {
		if (isWhite) game.loseGame();
		else {
			isAlive=false;
			game.tellMesaToShine((int)x,(int)y);
		}
	}
	
	public void resetPos() {
		x=xstart;
		y=ystart;
		speed=0;
	}

	private static Image img(String name) {
		return Toolkit.getDefaultToolkit().getImage(Ball.class.getResource("images/"+name));
	}
}
