package spikes.klaus.go;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import spikes.klaus.go.GoBoard.StoneColor;

class Intersection implements Serializable {

	private static final long serialVersionUID = 1L;
	private Intersection _left;
	private Intersection _right;
	private Intersection _up;
	private Intersection _down;
	
	StoneColor _stone = null;

	protected void connectToYourLeft(Intersection other) {
		_left = other;
		other._right = this;
	}

	protected void connectUp(Intersection other) {
		_up = other;
		other._down = this;
	}

	void setStone(StoneColor stoneColor) throws IllegalMove {
		if (!isLiberty()) throw new IllegalMove();
		_stone = stoneColor;
	}
	
	void fillGroupWithNeighbours(StoneColor stoneColor, Set<Intersection> group) {
		if (group.contains(this)) return;
		group.add(this);
		
		if(_stone != stoneColor) return;
		
		if (_up != null) _up.fillGroupWithNeighbours(stoneColor, group);
		if (_down != null) _down.fillGroupWithNeighbours(stoneColor, group);
		if (_left != null) _left.fillGroupWithNeighbours(stoneColor, group);
		if (_right != null) _right.fillGroupWithNeighbours(stoneColor, group);
	}

	boolean killGroupIfSurrounded(StoneColor color) {
		if (_stone != color) return false;
		
		Set<Intersection> groupWithNeighbours = new HashSet<Intersection>();
		fillGroupWithNeighbours(_stone, groupWithNeighbours);
		
		for (Intersection intersection : groupWithNeighbours)
			if (intersection.isLiberty()) return false;

		for (Intersection intersection : groupWithNeighbours)
			if (intersection._stone == color) intersection._stone = null;
		
		return true;
	}

	boolean isLiberty() {
		return _stone == null;
	}

	@Override
	public boolean equals(Object obj) {
		final Intersection other = (Intersection) obj;
		if (_stone == null) 
			return (other._stone == null);
		return _stone.equals(other._stone);
	}

	void toggleDeadStone() {
		if (isLiberty()) return;
		StoneColor colorToKill = _stone;
		boolean killed;
		do {
			killed = false;
			Set<Intersection> group = new HashSet<Intersection>();
			fillGroupWithNeighbours(_stone, group);
			for (Intersection intersection : group)
				if (intersection._stone == colorToKill) {
					intersection._stone = null;
					killed = true;
				}
		} while (killed);
	}

}