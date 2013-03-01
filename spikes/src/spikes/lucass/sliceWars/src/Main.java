package spikes.lucass.sliceWars.src;

import spikes.lucass.sliceWars.src.gui.GuiPlayer;
import spikes.lucass.sliceWars.src.logic.Player;

public class Main implements RemotePlayListener{

	private GuiPlayer _player1;
	private GuiPlayer _player2;

	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		_player1 = new GuiPlayer(Player.PLAYER1, this, 42);
		_player2 = new GuiPlayer(Player.PLAYER2, this, 42);
	}

	@Override
	public void play(RemotePlay play) {
		_player1.play(play);
		_player2.play(play);
	}
}
