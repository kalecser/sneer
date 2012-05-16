package sneer.bricks.network.computers.udp.holepuncher;

import java.net.DatagramPacket;

import basis.brickness.Brick;


@Brick
public interface StunServer {

	DatagramPacket replyFor(DatagramPacket received);

}
