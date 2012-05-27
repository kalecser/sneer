package sneer.bricks.snapps.games.go.impl.network;

import sneer.bricks.identity.seals.Seal;
import sneer.bricks.snapps.games.go.impl.logic.Move;

public class GoMove extends GoMessage {

	public final Move move; // Fix: Use String instead of Move, and create a parse method in GoMain
	
	public GoMove(Seal addressee_, Move move_) {
		super(addressee_);
		move = move_;
	}

}
