package sneer.bricks.network.computers.udp.connections;

import java.net.DatagramPacket;

import sneer.bricks.network.computers.connections.ConnectionManager;
import basis.brickness.Brick;
import basis.lang.Consumer;


@Brick
public interface UdpConnectionManager extends ConnectionManager.Delegate {

	void handle(DatagramPacket packet);
	
	void initSender(Consumer<DatagramPacket> sender);

}
