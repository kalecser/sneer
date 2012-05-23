package sneer.bricks.snapps.games.go.impl;

import basis.lang.Consumer;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.snapps.games.go.impl.gui.GoBoardPanel;
import sneer.bricks.snapps.games.go.impl.logic.Move;

public class RemotePlayerOnSneer implements RemotePlayer {

	@SuppressWarnings("unused")
	private Object _refToAvoidGc;
	
	private final Register<Move> _move;

	public RemotePlayerOnSneer(Register<Move> move) {
		this._move = move;
	}

	@Override
	public void setBoard(final GoBoardPanel goBoardPanel) {
		_refToAvoidGc = _move.output().addReceiver(new Consumer<Move>() { @Override public void consume(Move move) { 
			if (move == null) return; 
			goBoardPanel.play(move); 
		}});
	}

}
