package spikes.klaus.go;

import static sneer.foundation.environments.Environments.my;
import static spikes.klaus.go.GoBoard.StoneColor.BLACK;
import static spikes.klaus.go.GoBoard.StoneColor.WHITE;

import java.util.Arrays;
import java.util.HashSet;

import sneer.bricks.hardware.ram.deepcopy.DeepCopier;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;

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

	private Intersection[][] _intersections;
	private Register<StoneColor> _nextToPlay = my(Signals.class).newRegister(BLACK);
	private Intersection[][] _previousSituation;
	private boolean _previousWasPass = false;
	private final Register<Integer> _blackScore = my(Signals.class).newRegister(0);
	private final Register<Integer> _whiteScore = my(Signals.class).newRegister(0);
	private Register<StoneColor> _winner = my(Signals.class).newRegister(null);
	
	
	
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
		countCapturedStones();
		next();
	}
	public void toggleDeadStone(int x, int y) {
		if (_intersections[x][y]._stone==null & _previousSituation[x][y]._stone!=null) {
			Intersection[][] temp;
			temp=my(DeepCopier.class).deepCopy(_previousSituation);
			_intersections=my(DeepCopier.class).deepCopy(_previousSituation);
			_previousSituation=my(DeepCopier.class).deepCopy(temp);
		}
		else {
			if (_intersections[x][y]._stone!=null) _intersections[x][y].toggleDeadStone();
		}
	}

	public void passTurn() {
		next();
		
		if (_previousWasPass)
			stopAcceptingMoves();
		
		_previousWasPass = true;
	}
	
	public void resign() {
		StoneColor loser = nextToPlay();
		_winner.setter().consume(other(loser));
		stopAcceptingMoves();
	}
	
	public void finish() {
		countDeadStones();
		countTerritories();
	}

	public Signal<Integer> blackScore() {
		return _blackScore.output();
	}
	public Signal<Integer> whiteScore() {
		return _whiteScore.output();
	}
	public Signal<StoneColor> nextToPlaySignal() {
		return _nextToPlay.output();
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
		return _nextToPlay.output().currentValue();
	}
	public StoneColor getPrevColor(int x, int y) {
		return _previousSituation[x][y]._stone;
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
		return my(DeepCopier.class).deepCopy(_intersections);
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
		_nextToPlay.setter().consume(null);
	}
	
	private void countDeadStones() {
		int size = _intersections.length;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (!_intersections[x][y].isLiberty())
					continue;
				StoneColor previousStone = _previousSituation[x][y]._stone;
				if (previousStone == BLACK) add(_whiteScore, 1);
				if (previousStone == WHITE) add(_blackScore, 1);
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
			if (belongsToB & !belongsToW) add(_blackScore, numEmpty);
			if (!belongsToB & belongsToW) add(_whiteScore, numEmpty);
		}
	}
	private void add(Register<Integer> register, int ammount) {
		register.setter().consume(register.output().currentValue() + ammount);
	}

	private void next() {
		_nextToPlay.setter().consume(other(nextToPlay()));
	}
	private void restoreSituation(Intersection[][] situation) {
		_intersections = situation;
	}

	private void countCapturedStones() {
		countCapturedStones(_blackScore, WHITE);
		countCapturedStones(_whiteScore, BLACK);
	}
	private void countCapturedStones(Register<Integer> counter,	StoneColor color) {
		Integer captured = countCapturedStones(color);
		counter.setter().consume(counter.output().currentValue() + captured);
	}
	private int countCapturedStones(StoneColor color) {
		int result = 0;
		
		for (int line = 0; line < size(); line++)
			for (int column = 0; column < size(); column++) {
				StoneColor previous = _previousSituation[line][column]._stone;
				StoneColor current = _intersections[line][column]._stone;
				if (previous == color && current == null) result++;
			};
			
			return result;
	}
	
	public Signal<StoneColor> winner() {
		return _winner.output(); 
	}

	
}
