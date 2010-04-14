package sneer.bricks.network.social.status.protocol.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.Collection;

import scala.actors.threadpool.Arrays;
import sneer.bricks.hardware.ram.collections.CollectionUtils;
import sneer.bricks.network.social.status.protocol.StatusFactory;
import sneer.foundation.lang.Functor;

class StatusFactoryImpl implements StatusFactory {

	private enum Status { ONLINE, OFFLINE, AWAY, BUSY };

	private Status _default = Status.OFFLINE; 

	@Override
	public String defaultValue() {
		return _default.toString();
	}

	public Collection<String> values() {
		return my(CollectionUtils.class).map(Arrays.asList(Status.values()), new Functor<Status, String>() { @Override public String evaluate(Status status) throws RuntimeException {
			return status.toString();
		}});
	}

	public sneer.bricks.network.social.status.protocol.Status tupleFor(String statusName) {
		Status status = Status.valueOf(statusName); // Validates argument
		return new sneer.bricks.network.social.status.protocol.Status(status.name());
	}

}
