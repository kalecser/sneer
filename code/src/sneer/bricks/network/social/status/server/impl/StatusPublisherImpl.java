package sneer.bricks.network.social.status.server.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.network.social.status.protocol.StatusFactory;
import sneer.bricks.network.social.status.server.StatusPublisher;
import sneer.bricks.pulp.tuples.TupleSpace;

class StatusPublisherImpl implements StatusPublisher {

	@Override
	public void publish(String status) {
		my(TupleSpace.class).acquire(my(StatusFactory.class).tupleFor(status));
	}

}
