package sneer.bricks.snapps.games.go.impl.logic;

import static sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor.BLACK;
import static sneer.bricks.snapps.games.go.impl.logic.GoBoard.StoneColor.WHITE;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class GoBoard {

	public static enum StoneColor { BLACK, WHITE;}
	
	public GoBoard(int size) {
		_intersections = new Intersection[size][size];
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				Intersection newOne = new Intersection();
				_intersections[x][y] = newOne;
				if (x != 0) newOne.connectToYourLeft(_intersections[x - 1][y]);
				if (y != 0) newOne.connectUp(_intersections[x][y - 1]);
			}
		}
	}
	
	
	public GoBoard(String[] setup) {
		this(setup.length);
		setup(setup);
	}

	
	private StoneColor _nextToPlay = BLACK;
	private int _blackScore = 0;
	private int _whiteScore = 0;
	private StoneColor _winner = null;
	
	
	private Intersection[][] _intersections;
	private Intersection[][] _previousSituation;
	private boolean _previousWasPass = false;
	private int _capturedStonesBlack;
	private int _capturedStonesWhite;
	private BoardListener _boardListener;
	
		
	protected Intersection intersection(int x, int y) {
		return _intersections[x][y];
	}
	
	
	public String printOut(){
		StringBuffer result= new StringBuffer();
		
		for (int y = 0; y < size(); y++) {
			for (int x = 0; x < size(); x++) {
				StoneColor stone = stoneAt(x, y);
				if(stone == WHITE)
					result.append(" o");
				else if(stone == BLACK)
					result.append(" x");
				else
					result.append(" +");
			}
			result.append("\n");
		}
		return result.toString();
	}
	
	
	public int size() {
		return _intersections.length;
	}
	
	
	public boolean canPlayStone(int x, int y) {
		if (nextToPlay() == null) return false;
		
		Intersection[][] situation = copySituation();
		try {
			tryToPlayStone(x, y);
		} catch (IllegalMove im) {
			return false;
		} finally {
			restoreSituation(situation);
		}
		
		return true;
	}
	
	
	public void playStone(int x, int y) {
		Intersection[][] situationFound = copySituation();
		
		try {
			tryToPlayStone(x, y);
		} catch (IllegalMove e) {
			throw new IllegalArgumentException(e);
		}
		_previousWasPass = false;
		_previousSituation = situationFound;
		countDeadStones();
		next();
	}
	
	
	public void toggleDeadStone(int x, int y) {
		if (_intersections[x][y]._stone == null)
			unmarkDeadStones(x, y);
		else
			_intersections[x][y].markDeadStones();

		updateScore();
	}


	public void passTurn() {
		next();
		
		if (_previousWasPass)
			stopAcceptingMoves();
		
		_previousWasPass = true;
	}
	
	
	public void resign() {
		StoneColor loser = nextToPlay();
		_winner = other(loser);
		stopAcceptingMoves();
	}
	
	
	public int blackScore() {
		return _blackScore;
	}
	
	
	public int whiteScore() {
		return _whiteScore;
	}
	
	
	public StoneColor other(StoneColor color) {
		return color == BLACK
		? WHITE
				: BLACK;
	}
	
	
	public StoneColor stoneAt(int x, int y) {
		return intersection(x, y)._stone;
	}
	
	public StoneColor nextToPlay() {
		return _nextToPlay;
	}
	
	public StoneColor getPrevColor(int x, int y) {
		return _previousSituation[x][y]._stone;
	}
	
	
	public StoneColor winner() {
		return _winner; 
	}


	public void setBoardListener(BoardListener boardListener) {
		_boardListener = boardListener;
	}


	private void unmarkDeadStones(int x, int y) {
		Set<Intersection> group = _intersections[x][y].getGroupWithNeighbours();
		for (Intersection intersection : group)
			if (intersection._stone == null)
				intersection._stone = previousEquivalent(intersection)._stone;
	}


	private Intersection previousEquivalent(Intersection intersection) {
		int size = _intersections.length;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (_intersections[x][y] == intersection)
					return _previousSituation[x][y];
			}
		}
		throw new IllegalStateException("Intersection " + intersection + " not found.");
	}


	private boolean sameSituationAs(Intersection[][] situation) {
		return Arrays.deepEquals(situation, _intersections);
	}
	
	
	private boolean killSurroundedStones(StoneColor color) {
		boolean wereStonesKilled = false;
		for(Intersection[] column : _intersections)
			for(Intersection intersection : column)
				if (intersection.killGroupIfSurrounded(color))
					wereStonesKilled = true;
		
		return wereStonesKilled;
	}
	
	
	private Intersection[][] copySituation() {
		if(_intersections.length == 0)
			return new Intersection[0][0];
		Intersection[][] copy =  new Intersection[_intersections.length][_intersections[0].length];
		for (int i = 0; i < copy.length; i++) {
			for (int j = 0; j < copy[i].length; j++) {
				copy[i][j] = _intersections[i][j].copy();
			}
		}
		return copy;
	}
	
	
	private HashSet<Intersection> allIntersections() {
		HashSet<Intersection> set = new HashSet<Intersection>();
		
		for (Intersection[] column: _intersections )
			for(Intersection inter : column)
				set.add(inter);
		
		return set;
	}
	
	
	private void setup(String[] setup){
		for (int y = 0; y < setup.length; y++)
			setupLine(y, setup[y]);
	}
	
	
	private void setupLine(int y, String line) {
		int x = 0;
		for(char symbol : line.toCharArray()) {
			if (symbol == ' ') continue;
			
			StoneColor stone = null;
			if(symbol == 'x') stone = BLACK;
			if(symbol == 'o') stone = WHITE;
			
			intersection(x, y)._stone = stone;
			x++;
		}
	}
	
	
	private void tryToPlayStone(int x, int y) throws IllegalMove{
		intersection(x, y).setStone(nextToPlay());
		
		killSurroundedStones(other(nextToPlay()));
		if (killSurroundedStones(nextToPlay()))
			throw new IllegalMove();
		
		if(sameSituationAs(_previousSituation))
			throw new IllegalMove();
	}
	
	
	private void stopAcceptingMoves() {
		_previousSituation = copySituation();
		_nextToPlay = null;
		if(_boardListener != null){
			_boardListener.nextToPlay(_nextToPlay);
		}
		
		_capturedStonesBlack = _blackScore;
		_capturedStonesWhite = _whiteScore;
		
		updateScore();
	}
	
	
	private void updateScore() {
		_blackScore = _capturedStonesBlack;
		_whiteScore = _capturedStonesWhite;
		countDeadStones();
		countTerritories();
		if(_boardListener != null){
			_boardListener.updateScore(_blackScore,_whiteScore);
		}
	}

	
	private void countDeadStones() {
		int size = _intersections.length;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (!_intersections[x][y].isLiberty())
					continue;
				StoneColor previousStone = _previousSituation[x][y]._stone;
				if (previousStone == BLACK){
					_whiteScore++;
				}
				if (previousStone == WHITE){
					_blackScore++;
				}
			}
		}
	}
	
	private void countTerritories() {
	
		HashSet<Intersection> pending = allIntersections();
		
		while (!pending.isEmpty()) {
			Intersection starting = pending.iterator().next();
			
			HashSet<Intersection> group = new HashSet<Intersection>();
			starting.fillGroupWithNeighbours(null, group);
			
			boolean belongsToW=false, belongsToB=false;
			int numEmpty=0;
			for (Intersection groupee : group) {
				pending.remove(groupee);
				if (groupee._stone == BLACK) belongsToB = true;
				if (groupee._stone ==WHITE) belongsToW = true;
				if (groupee.isLiberty()) numEmpty++;
			}
			if (belongsToB & !belongsToW){
				_blackScore += numEmpty;
			}
			if (!belongsToB & belongsToW){
				_whiteScore += numEmpty;
			}
		}
	}

	
	private void next() {
		_nextToPlay = other(nextToPlay());
		if(_boardListener!=null){
			_boardListener.nextToPlay(_nextToPlay);
		}
	}
	
	private void restoreSituation(Intersection[][] situation) {
		_intersections = situation;
	}
	
}
