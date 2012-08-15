package sneer.bricks.network.computers.channels.guaranteed;

import sneer.bricks.network.computers.channels.Channel;

public interface GuaranteedChannels {

	GuaranteedChannel guarantee(Channel channel);
}
