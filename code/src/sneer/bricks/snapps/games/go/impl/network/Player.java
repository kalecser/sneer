package sneer.bricks.snapps.games.go.impl.network;

import sneer.bricks.snapps.games.go.impl.logic.Move;


public interface Player {

	void play(Move move);
	void setAdversary(Player playListener);
 
}
