package kalecser.sneer.bricks.network.social.navigation;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class ContactsRequest extends Tuple {

	public ContactsRequest(Seal adressee) {
		super(adressee);
	}

}
