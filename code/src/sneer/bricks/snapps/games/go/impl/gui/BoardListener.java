package sneer.bricks.snapps.games.go.impl.gui;

import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;


public interface BoardListener {

	void updateScore(int _blackScore, int _whiteScore);
	void nextToPlay(StoneColor _nextToPlay);

}
