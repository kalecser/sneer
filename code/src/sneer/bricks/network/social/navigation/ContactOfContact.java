package sneer.bricks.network.social.navigation;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class ContactOfContact extends Tuple {

	public final String nick;
	public final Seal contactSeal;
	

	public ContactOfContact(String nick_, Seal contactSeal_, Seal adressee) {
		super(adressee);
		this.nick = nick_;
		this.contactSeal = contactSeal_;
	}
}
