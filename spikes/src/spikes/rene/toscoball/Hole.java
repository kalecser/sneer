package spikes.rene.toscoball;
//a billiards hole. 
//Hole(X pos ,Y pos)


public class Hole {

	int x,y;
	static final int radius=18;
	
	public Hole(int i, int j) {x=i; y=j;}
	
	public void checkCollision(Ball other) {
		if (!other.isAlive) return; 
		if (M.pointDistance(x,y,other.x,other.y)<radius) other.die();
	}	
}
