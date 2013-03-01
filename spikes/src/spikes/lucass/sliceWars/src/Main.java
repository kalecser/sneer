package spikes.lucass.sliceWars.src;

import java.util.Calendar;
import java.util.Random;

import spikes.lucass.sliceWars.src.gui.GuiPlayer;
import spikes.lucass.sliceWars.src.logic.Player;

public class Main implements RemotePlayListener{

	private GuiPlayer _player1;
	private GuiPlayer _player2;

	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		Random random = new Random(Calendar.getInstance().getTimeInMillis());
		int nextInt = random.nextInt();
		_player1 = new GuiPlayer(Player.PLAYER1, this, nextInt);
		_player2 = new GuiPlayer(Player.PLAYER2, this, nextInt);
	}

	@Override
	public void play(RemotePlay play) {
		_player1.play(play);
		_player2.play(play);
	}
}
