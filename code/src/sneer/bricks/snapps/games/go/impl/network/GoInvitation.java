package sneer.bricks.snapps.games.go.impl.network;

import sneer.bricks.identity.seals.Seal;

public class GoInvitation extends GoMessage {

	public final String text;
	public final int _size;
	public final int _gameId;

	public GoInvitation(Seal addressee_, String text_, int size, int gameId) {
		super(addressee_);
		this.text = text_;
		this._size = size;
		this._gameId = gameId;
	}

}
