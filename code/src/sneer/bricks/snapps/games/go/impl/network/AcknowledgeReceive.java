package sneer.bricks.snapps.games.go.impl.network;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor;
import sneer.bricks.snapps.games.go.impl.logic.Move;

public class AcknowledgeReceive  extends Tuple { 
	
	public final StoneColor color;
	public final Move move; 

	public AcknowledgeReceive(Move move, StoneColor color) {
		this.move = move;
		this.color = color;
	}
	 

}
