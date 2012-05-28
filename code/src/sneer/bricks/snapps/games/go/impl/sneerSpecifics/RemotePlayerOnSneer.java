package sneer.bricks.snapps.games.go.impl.sneerSpecifics;

import java.util.LinkedHashSet;
import java.util.Set;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.snapps.games.go.impl.Player;
import sneer.bricks.snapps.games.go.impl.logging.GoLogger;
import sneer.bricks.snapps.games.go.impl.logic.Move;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.impl.network.AcknowledgeReceive;
import basis.lang.Consumer;

public class RemotePlayerOnSneer implements Player {

	@SuppressWarnings("unused")
	private WeakContract _refToAvoidGc;
	
	private final Register<Move> _move;

	@SuppressWarnings("unused") 
	private WeakContract _refToAvoidGc2;
	
	private Set<Move> movesAcknowleged = new LinkedHashSet<Move>();

	private final int _gameId;
	
	public RemotePlayerOnSneer(final int gameId,final StoneColor remoteSide,Register<Move> move,Register<AcknowledgeReceive> ackRegister) {
		this._gameId = gameId;
		this._move = move;
		_refToAvoidGc2 = ackRegister.output().addReceiver(new Consumer<AcknowledgeReceive>() { @Override public void consume(AcknowledgeReceive ack) {
			if(ack == null)
				return;
			if(ack.color.equals(remoteSide))
				return;
			
			movesAcknowleged.add(ack.move);
		}}); 
	}

	@Override
	public void play(Move move) {
		do{
			GoLogger.log("sending play "+move);
			_move.setter().consume(move);
			GoLogger.log("WaitingAcknowledge "+move);
			try {
				Thread.sleep(100);//TODO: timeout here
			} catch (InterruptedException e) {
				throw new basis.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
			}
		}while(!movesAcknowleged.contains(move));
	}

	@Override
	public void setAdversary(final Player playListener) {
		_refToAvoidGc = _move.output().addReceiver(new Consumer<Move>() { @Override public void consume(Move move) { 
			if (move == null) return;
			if(move.gameId != _gameId) return;
			playListener.play(move);
		}});
	}

}
