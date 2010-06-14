package spikes.rene.toscoball;
//a physically genuine 2D sphere with collision
//Ball(X pos ,Y pos)

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;



public class Ball {

	double x,y,xprev,yprev;
	private double
		speed=0,
		direction=0;
	private int
		minx,
		miny,
		maxx,
		maxy;
	static final int
		radius=16,
		border=32;
	private static final float
		conservation=0.99f,
		friction=0.05f;
	private static final double
		degtorad=Math.PI/180;
	private static final Image
		ballspr=Toolkit.getDefaultToolkit().getImage(Ball.class.getResource("ball.png")),
		whitespr=Toolkit.getDefaultToolkit().getImage(Ball.class.getResource("wball.png"));
	private boolean isWhite=false;
	public boolean isAlive=true;
	private Game game;
	
	public Ball(int i, int j, boolean b, JFrame w, Game g) {
		setPos(i,j);
		if (b) isWhite=true;
		game=g;
		minx=radius+border;
		miny=radius+border;
		maxx=w.getWidth()-radius-border;
		maxy=w.getHeight()-radius-border;
	}
	
	public void setPos(double i, double j) {x=i; y=j;}
	
	public boolean isMoving() {
		if (!isAlive) return false;
		return (speed>0 ? true : false);
	}
	
	public void step() {
		if (direction<0) direction+=360;
		if (direction>360) direction-=360;
		xprev=x; yprev=y;
		speed=Math.max(0,Math.min(speed,10000)-friction);
		x+=speed*Math.cos(direction*degtorad);
		y+=speed*-Math.sin(direction*degtorad);
		if (x<minx | x>maxx) {
			bounce(90);
			x=Math.max(minx,Math.min(maxx,x));
		}
		if (y<miny | y>maxy) {
			bounce(0);
			y=Math.max(miny,Math.min(maxy,y));
		}
	}

	public void contact(Wall w) {
		double
			xf=Math.cos(direction*degtorad),
			yf=-Math.sin(direction*degtorad);
		x=xprev;
		y=yprev;

		int count=(int)Math.ceil(speed);
		while (count>0) {
			count--;
			x+=xf; y+=yf;
			if (w.isIn(this)) break;
		}
	}
	
	public void draw(Graphics g) {
		if (!isAlive) return;
		g.drawImage((isWhite ? whitespr : ballspr),(int)(x-radius),(int)(y-radius), radius*2, radius*2,null);
	}
	
	public void checkCollision(Ball other) {
		if (M.pointDistance(x,y,other.x,other.y)<radius*2) {
			
			//get the normals and expulse
			double mydir=M.pointDirection(x,y,other.x,other.y);
			double hisdir=M.pointDirection(other.x,other.y,x,y);
			expulse(other,mydir,hisdir, M.pointDistance(x,y,other.x,other.y)/2);

			//store the amount of initial energy to prevent the 'explode' effect
			double total=(speed+other.speed)*conservation;
			
			//next find out how much force each ball applies to the other
			double mytowmod=Math.max(0,Math.cos(degtorad*(direction-mydir))*speed);
			double histowmod=Math.max(0,Math.cos(degtorad*(other.direction-hisdir))*other.speed);
			
			//and finally update the speed and direction of the local ball
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
			if (speed==0) {other.speed=total;}
			else if (other.speed==0) {speed=total;}
			else {
				double factor=speed/total;
				speed=total*(factor);
				other.speed=total*(1-factor);
			}
		}
	}
	
	public void checkExpulse(Ball other) {
		double dist=M.pointDistance(x,y,other.x,other.y)/2;
		if (dist>radius) return;
		if (dist<radius*0.95) {
			x+=Math.random()-0.5;
			y+=Math.random()-0.5;
		}
		expulse(other,M.pointDirection(x,y,other.x,other.y),M.pointDirection(other.x,other.y,x,y),dist);
	}
	
	private void expulse(Ball other, double mydir, double hisdir, double dist) {
		double midx=x+M.lengthdirx(dist,mydir);
		double midy=y+M.lengthdiry(dist,mydir);
		x=midx+M.lengthdirx(radius,hisdir);
		y=midy+M.lengthdiry(radius,hisdir);
		other.x=midx+M.lengthdirx(radius,mydir);
		other.y=midy+M.lengthdiry(radius,mydir);
	}
	
	public void bounce(double angle) {
		direction=M.correctAngle(-(direction-angle)+angle);
	}
	
	public void shoot(MouseEvent e) {
		speed=M.pointDistance(x,y,e.getX(),e.getY())/5;
		direction=M.pointDirection(e.getX(),e.getY(),x,y);
	}
	
	public void die() {
		if (isWhite) game.loseGame();
		else isAlive=false;
	}

}
