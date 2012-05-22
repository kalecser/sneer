package sneer.bricks.snapps.games.go.network;

import sneer.bricks.identity.seals.Seal;

public class GoInvitation extends GoMessage {

	public final String text;

	public GoInvitation(Seal addressee_, String text_) {
		super(addressee_);
		this.text = text_;
	}

}
