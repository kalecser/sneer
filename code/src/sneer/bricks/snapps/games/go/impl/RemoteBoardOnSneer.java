package sneer.bricks.snapps.games.go.impl;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.snapps.games.go.impl.logic.Move;

public class RemoteBoardOnSneer implements RemoteBoard {

	private final Register<Move> _move;

	public RemoteBoardOnSneer(Register<Move> move) {
		_move = move;
	}

	@Override
	public void play(Move move) {
		_move.setter().consume(move);
	}

}
