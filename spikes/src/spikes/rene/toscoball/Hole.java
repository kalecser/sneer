package spikes.rene.toscoball;
//a billiards hole
//Hole(X ,Y)


class Hole {

	int x,y;
	
	Hole(int i, int j) {x=i; y=j;}
	
	void checkCollision(Ball[] balls) {
		for (int i=0; i<balls.length; i++) {collide(balls[i]);}
	}
	private void collide(Ball other) {
		if (!other.isAlive) return;
		if (M.pointDistance(x,y,other.x,other.y)<19) other.die();
	}
}
