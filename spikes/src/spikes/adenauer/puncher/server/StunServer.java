package spikes.adenauer.puncher.server;

import java.net.DatagramPacket;

import sneer.foundation.brickness.Brick;

@Brick
public interface StunServer {

	DatagramPacket replyFor(DatagramPacket received);

}
