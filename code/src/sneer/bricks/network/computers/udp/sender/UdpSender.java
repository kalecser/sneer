package sneer.bricks.network.computers.udp.sender;

import java.net.DatagramPacket;

import basis.brickness.Brick;
import basis.lang.Consumer;

@Brick
public interface UdpSender {
	void send(DatagramPacket packet);
	void init(Consumer<DatagramPacket> sender);
}
