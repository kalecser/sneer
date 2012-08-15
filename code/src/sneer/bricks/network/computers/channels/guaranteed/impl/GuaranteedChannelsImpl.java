package sneer.bricks.network.computers.channels.guaranteed.impl;

import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.channels.guaranteed.GuaranteedChannel;
import sneer.bricks.network.computers.channels.guaranteed.GuaranteedChannels;

class GuaranteedChannelsImpl implements GuaranteedChannels {

	@Override
	public GuaranteedChannel guarantee(Channel channel) {
		return new GuaranteedChannelImpl(channel); 
	}

}
