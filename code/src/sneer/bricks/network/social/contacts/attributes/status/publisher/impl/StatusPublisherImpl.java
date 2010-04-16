package sneer.bricks.network.social.contacts.attributes.status.publisher.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.network.social.contacts.attributes.AttributeValue;
import sneer.bricks.network.social.contacts.attributes.status.publisher.StatusPublisher;
import sneer.bricks.pulp.serialization.Serializer;

class StatusPublisherImpl implements StatusPublisher {

	@Override
	public void publish(String status) {
		my(TupleSpace.class).acquire(
			new AttributeValue("Status", my(Serializer.class).serialize(status))
		);
	}

}
