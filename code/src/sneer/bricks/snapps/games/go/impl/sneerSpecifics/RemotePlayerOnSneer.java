package sneer.bricks.snapps.games.go.impl.sneerSpecifics;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.snapps.games.go.impl.Player;
import sneer.bricks.snapps.games.go.impl.logic.Move;
import basis.lang.Consumer;

public class RemotePlayerOnSneer implements Player {

	@SuppressWarnings("unused")
	private Object _refToAvoidGc;
	
	private final Register<Move> _move;

	public RemotePlayerOnSneer(Register<Move> move) {
		this._move = move;
	}

	@Override
	public void play(Move move) {
		_move.setter().consume(move);
	}

	@Override
	public void setAdversary(final Player playListener) {
		_refToAvoidGc = _move.output().addReceiver(new Consumer<Move>() { @Override public void consume(Move move) { 
			if (move == null) return; 
			playListener.play(move); 
		}});
	}

}
