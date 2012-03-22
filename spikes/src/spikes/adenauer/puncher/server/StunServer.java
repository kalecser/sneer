package spikes.adenauer.puncher.server;

import java.net.DatagramPacket;

import basis.brickness.Brick;


@Brick
public interface StunServer {

	DatagramPacket replyFor(DatagramPacket received);

}
