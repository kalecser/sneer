package sneer.bricks.network.social.status.client.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.status.client.StatusClient;
import sneer.bricks.network.social.status.keeper.StatusKeeper;
import sneer.bricks.network.social.status.protocol.Status;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.foundation.lang.Consumer;

class StatusClientImpl implements StatusClient, Consumer<Status> {

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	{
		_toAvoidGC = my(TupleSpace.class).addSubscription(Status.class, this);
	}

	@Override
	public void consume(Status status) {
		if (my(OwnSeal.class).get().equals(status.publisher)) return;

		Contact contact = my(ContactSeals.class).contactGiven(status.publisher);
		if (contact == null) {
			my(Logger.class).log("Status received from unkown contact: ", status.publisher);
			return;
		}

		my(StatusKeeper.class).setStatus(contact, status.value);
	}

}
