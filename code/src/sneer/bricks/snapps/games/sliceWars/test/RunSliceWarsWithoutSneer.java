package sneer.bricks.snapps.games.sliceWars.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import sneer.bricks.snapps.games.sliceWars.impl.RemotePlay;
import sneer.bricks.snapps.games.sliceWars.impl.RemotePlayListener;
import sneer.bricks.snapps.games.sliceWars.impl.gui.GuiPlayer;
import sneer.bricks.snapps.games.sliceWars.impl.logic.Player;

public class RunSliceWarsWithoutSneer implements RemotePlayListener{

	private List<GuiPlayer> _players;

	public static void main(String[] args) {
		new RunSliceWarsWithoutSneer();
	}
	
	public RunSliceWarsWithoutSneer() {
		Random random = new Random(Calendar.getInstance().getTimeInMillis());
		int nextInt = random.nextInt();
		int numberOfPlayers = 2;
		int lines = 6;
		int columns = 6;
		int randomlyRemoveCells = 12;
				
		_players = new ArrayList<GuiPlayer>();
		
		Player player = new Player(1, numberOfPlayers);
		_players.add(new GuiPlayer(player, this, nextInt,numberOfPlayers,lines,columns,randomlyRemoveCells));
		
		for (int i = 1; i < numberOfPlayers; i++) {
			player = player.next();
			_players.add(new GuiPlayer(player, this, nextInt,numberOfPlayers,lines,columns,randomlyRemoveCells));
		}
	}

	@Override
	public void play(RemotePlay play) {
		for (GuiPlayer guiPlayer : _players) {
			guiPlayer.play(play);
		}
	}
}
