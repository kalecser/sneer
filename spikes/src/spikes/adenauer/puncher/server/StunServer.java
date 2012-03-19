package spikes.adenauer.puncher.server;

import java.net.DatagramPacket;

import sneer.foundation.brickness.Brick;

@Brick
public interface StunServer {

	void handleAndUseForReply(DatagramPacket received);

}
