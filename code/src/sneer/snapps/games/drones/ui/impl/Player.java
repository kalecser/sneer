package sneer.snapps.games.drones.ui.impl;

public class Player {

	private int x;
	private Direction direction;
	
	enum Direction {LEFT, RIGHT};

	public Player(int i, Direction direction) {
		x = i;
		this.direction = direction;
	}

	public int x() {
		return x += direction() * 10;
	}

	private int direction() {
		return direction == Direction.RIGHT ? 1 : -1;
	
	}
	
	
	
}
