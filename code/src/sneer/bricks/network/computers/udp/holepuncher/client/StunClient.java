package sneer.bricks.network.computers.udp.holepuncher.client;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import org.hamcrest.Matcher;

import basis.brickness.Brick;
import basis.lang.Consumer;

@Brick
public interface StunClient {

	void init(Consumer<DatagramPacket> sender);

	void handle(ByteBuffer stunPacket);

	
	
}
