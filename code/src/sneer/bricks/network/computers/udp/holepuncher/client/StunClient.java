package sneer.bricks.network.computers.udp.holepuncher.client;

import java.net.DatagramPacket;

import basis.brickness.Brick;
import basis.lang.Consumer;

@Brick
public interface StunClient {

	void initSender(Consumer<DatagramPacket> sender);

	
	
}
