package spikes.klaus.go;


import java.util.HashSet;
import java.util.Set;

import spikes.klaus.go.GoBoard.StoneColor;

class Intersection {

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
	
	void toggleDeadStone() {
		StoneColor colorToKill = _stone;
		boolean killed;
		StoneColor turn=null;
		if (isLiberty()) {
			 turn=colorToKill; colorToKill=null;
		}
		
		do {
			killed = false;
			Set<Intersection> group = getGroupWithNeighbours();
			for (Intersection intersection : group)
				if (intersection._stone == colorToKill) {
					intersection._stone = turn;
					killed = true;
				}
		} while (killed);
	}

	Set<Intersection> getGroupWithNeighbours() {
		Set<Intersection> result = new HashSet<Intersection>();
		fillGroupWithNeighbours(_stone, result);
		return result;
	}

	boolean killGroupIfSurrounded(StoneColor color) {
		if (_stone != color) return false;
		
		Set<Intersection> groupWithNeighbours = getGroupWithNeighbours();
		
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

}