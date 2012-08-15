package sneer.bricks.network.computers.channels.impl;

import static basis.environments.Environments.my;
import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.channels.Channels;
import sneer.bricks.network.computers.connections.ConnectionManager;
import sneer.bricks.network.social.Contact;

class ChannelsImpl implements Channels {

	@Override
	public Channel accept(long id) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public Channel create(Contact contact, Priority priority) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public Channel createControl(Contact contact) {
		return new ChannelImpl(my(ConnectionManager.class).connectionFor(contact));
	}

}
