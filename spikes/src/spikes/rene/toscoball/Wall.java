package spikes.rene.toscoball;
//a rectangular wall with collision.
//Wall(X pos ,Y pos, Width, Height)


public class Wall {

	int x,y,w,h;
	
	public Wall(int i, int j, int k, int l) {
		x=i;
		y=j;
		w=k;
		h=l;
	}
	
	public void checkCollision(Ball other) {
		if (!other.isAlive | !isIn(other)) return; 

		//Contact before collision to increase precision.
		other.contactWall(this);
		
		if ((other.x>x & other.x<x+w) & (other.y>y-11 & other.y<y+h+11)) {
			//vertical collision
			other.bounce(0);
			if (other.y<y+h/2) other.y=y-11;
			else other.y=y+h+11;
			return;
		}
		if ((other.y>y & other.y<y+h) & (other.x>x-11 & other.x<x+w+11)) {
			//horizontal collision
			other.bounce(90);
			if (other.x<x+w/2) other.x=x-11;
			else other.x=x+w+11;
			return;
		}
		if (M.pointDistance(x,y,other.x,other.y)<11) {
			//top-left collision
			other.bounce(M.pointDirection(x,y,other.x,other.y)-90);
			return;
		}
		if (M.pointDistance(x+w,y,other.x,other.y)<11) {
			//top-right collision
			other.bounce(M.pointDirection(x+w,y,other.x,other.y)-90);
			return;
		}
		if (M.pointDistance(x+w,y+h,other.x,other.y)<11) {
			//bottom-right collision
			other.bounce(M.pointDirection(x+w,y+h,other.x,other.y)-90);
			return;
		}
		if (M.pointDistance(x,y+h,other.x,other.y)<11) {
			//bottom-left collision
			other.bounce(M.pointDirection(x,y+h,other.x,other.y)-90);
			return;
		}
	}
	
	public boolean isIn(Ball other) {
		//required for Ball.contact(Wall)
		if ((other.x>x & other.x<x+w) & (other.y>y-11 & other.y<y+h+11)) return true;
		if ((other.y>y & other.y<y+h) & (other.x>x-11 & other.x<x+w+11)) return true;
		if (M.pointDistance(x,y,other.x,other.y)<11) return true;
		if (M.pointDistance(x+w,y,other.x,other.y)<11) return true;
		if (M.pointDistance(x+w,y+h,other.x,other.y)<11) return true;
		if (M.pointDistance(x,y+h,other.x,other.y)<11) return true;
		return false;
	}
}
