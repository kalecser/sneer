package spikes.rene.toscoball;
//a billiards ball
//Ball(X pos ,Y pos)

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;


public class Ball {

	private int id;
	double x,y,xprev,yprev;
	private double
		speed=0,
		direction=0;
	static final int
		radius=11;
	static int k;
	private static final int
	minx=radius+48,
	miny=radius+128,
	maxx=464-radius,
	maxy=432-radius;
	private static final double friction=0.01;
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
		setPos(i,j);
		id=n;
		isWhite=(id==0);
		game=g;
	}
	
	public void draw(Graphics g) {
		if (!isAlive) return;
		int sub=id-1;
		if (isWhite) {sub=6+k;}
		g.drawImage(ballspr[sub],(int)(x-radius),(int)(y-radius), radius*2, radius*2,null);
	}
	
	public void setPos(double i, double j) {x=i; y=j;}
	
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
			if (M.pointDistance(x,y,other.x,other.y)<radius*2) { 
				
				double xf=0,yf=0,oxf=0,oyf=0;
				if (speed>0) {
				xf=M.lengthdirx(1,direction);
				yf=M.lengthdiry(1,direction);}
				if (other.speed>0) {
				oxf=M.lengthdirx(1,other.direction);
				oyf=M.lengthdiry(1,other.direction);}
				
				int lol=(int)Math.ceil(Math.max(speed,other.speed));
				
				for (int i=0; i<lol; i++) {
					if (i>lol | M.sqr(x-other.x)+M.sqr(y-other.y)>M.sqr(radius*2)) break;
					x-=xf; y-=yf;
					other.x-=oxf; other.y-=oyf;
				}
	
				
				
				//next find out how much force each ball applies to the other
				double mydir=M.pointDirection(x,y,other.x,other.y);
				double hisdir=M.correctAngle(mydir+180);
				double mytowmod=Math.max(0,Math.cos(M.degtorad*(direction-mydir))*speed);
				double histowmod=Math.max(0,Math.cos(M.degtorad*(other.direction-hisdir))*other.speed);
	
				//store the amount of initial momentum for later corrections
				double total=(speed+other.speed);
	
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
				
				//then cap resulting energy to avoid 'explode'
				if (speed==0) other.speed=total;
				else if (other.speed==0) speed=total;
				else {
					double factor=speed/total;
					speed=total*(factor);
					other.speed=total*(1-factor);
				}
			}
		}
	}
	
	public void expulse(Ball other, int fac) {
		double dist=M.pointDistance(x,y,other.x,other.y)/2;
		if (dist>radius+fac) return;
		else if (dist<radius*0.8) {
			x+=Math.random()-0.5;
			y+=Math.random()-0.5;
		}
		double mydir=M.pointDirection(x,y,other.x,other.y);
		double hisdir=M.correctAngle(mydir+180);
		double midx=x+M.lengthdirx(dist,mydir);
		double midy=y+M.lengthdiry(dist,mydir);
		x=midx+M.lengthdirx(radius+1,hisdir);
		y=midy+M.lengthdiry(radius+1,hisdir);
		other.x=midx+M.lengthdirx(radius+1,mydir);
		other.y=midy+M.lengthdiry(radius+1,mydir);
		xprev=x;
		yprev=y;
		other.xprev=other.x;
		other.yprev=other.y;
	}
	
	public void bounce(double angle) {
		direction=M.correctAngle(-(direction-angle)+angle);
	}
	
	public void shoot(double a) {
		speed=10;
		direction=a;
	}
	
	public void die() {
		if (isWhite) game.loseGame();
		else isAlive=false;
	}

	private static Image img(String name) {
		return Toolkit.getDefaultToolkit().getImage(Ball.class.getResource(name));
	}
}
