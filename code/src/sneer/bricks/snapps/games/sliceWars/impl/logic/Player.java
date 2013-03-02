package sneer.bricks.snapps.games.sliceWars.impl.logic;

public class Player{
	public static final Player EMPTY = new Player(0);
	public static final Player PLAYER1  =  new Player(1);
	public static final Player PLAYER2  =  new Player(2);
	public static final Player PLAYER3  =  new Player(3);
	public static final Player PLAYER4  =  new Player(4);
	public static final Player PLAYER5  =  new Player(5);
	private final int _playerCount;
	private final int currentPlayer;
	
	private Player(int player) {
		this(player,0);
	}
	
	public Player(int player, int playerCount) {
		_playerCount = playerCount;
		currentPlayer = player;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Player)) return false;
		Player other = (Player)obj;
		return currentPlayer == other.currentPlayer;
	}
	
	
	public boolean isLastPlayer(){
		return currentPlayer == _playerCount;
	}
	
	public Player next(){
		if(currentPlayer + 1 > _playerCount)
			return new Player(1, _playerCount);
		return new Player(currentPlayer + 1, _playerCount);
	}

	public int getPlayerNumber() {
		return currentPlayer;
	}

	public int getPlayersCount() {
		return _playerCount;
	}
}