package sneer.bricks.snapps.games.go;


public class Move {
	
	public final boolean isResign;
	public final boolean isPass;
	public final boolean isMark;
	public final int xCoordinate;
	public final int yCoordinate;
	
	public Move(boolean resign_, boolean pass_, int x_, int y_, boolean mark_) {
		isResign = resign_;
		isPass = pass_;
		isMark=mark_;
		xCoordinate = x_;
		yCoordinate = y_;
	}

}
