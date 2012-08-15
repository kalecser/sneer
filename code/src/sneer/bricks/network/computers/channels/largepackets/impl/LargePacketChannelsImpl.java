package sneer.bricks.network.computers.channels.largepackets.impl;

import sneer.bricks.network.computers.channels.Channel;
import sneer.bricks.network.computers.channels.largepackets.LargePacketChannel;
import sneer.bricks.network.computers.channels.largepackets.LargePacketChannels;

class LargePacketChannelsImpl implements LargePacketChannels {

	@Override
	public LargePacketChannel enhance(Channel delegate) {
		return new LargePacketChannelImpl(delegate); 
	}

}
