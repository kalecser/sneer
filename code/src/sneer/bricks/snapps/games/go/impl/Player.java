package sneer.bricks.snapps.games.go.impl;

import sneer.bricks.snapps.games.go.impl.logic.Move;


public interface Player {

	void play(Move move);
	void setAdversary(Player player);
 
}
