package sneer.bricks.network.computers.addresses.own.port.impl;

import static basis.environments.Environments.my;

import java.util.Random;

import sneer.bricks.network.computers.addresses.own.port.OwnPort;
import sneer.bricks.network.social.attributes.Attributes;

class OwnPortImpl implements OwnPort {

	{
		my(Attributes.class).registerAttribute(OwnPort.class);
		
		Integer currentValue = my(Attributes.class).myAttributeValue(OwnPort.class).currentValue();
		if (currentValue == NOT_YET_SET) {
			int port = randomPort();
			my(Attributes.class).myAttributeSetter(OwnPort.class).consume(port);
		}

	}

	private int randomPort() {
		int lower = 10000;
		int higher = 40000;
		return lower + new Random().nextInt(higher - lower + 1);
	}

}
