package sneer.bricks.network.computers.channels.impl;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.channels.Channels;
import sneer.bricks.network.social.Contact;

class ChannelsImpl implements Channels {

	@Override
	public Channel accept(Hash id) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public Channel create(Contact contact, Priority priority) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

	@Override
	public Channel createControl(Contact contact) {
		throw new basis.lang.exceptions.NotImplementedYet(); // Implement
	}

}
