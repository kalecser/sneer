package spikes.rene.toscoball;
//a billiards hole. 
//Hole(X pos ,Y pos)

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;


public class Hole {

	int x,y;
	static final int
		radius=16;
	private static final Image
		buracospr=Toolkit.getDefaultToolkit().getImage(Hole.class.getResource("buraco.png"));
	
	public Hole(int i, int j) {x=i; y=j;}
	
	public void draw(Graphics g) {
		g.drawImage(buracospr,x-radius,y-radius,radius*2,radius*2,null);
	}
	
	public void checkCollision(Ball other) {
		if (!other.isAlive) return; 
		if (M.pointDistance(x,y,other.x,other.y)<radius) other.die();
	}	
}
