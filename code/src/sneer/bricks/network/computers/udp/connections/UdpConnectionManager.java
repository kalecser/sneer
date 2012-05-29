package sneer.bricks.network.computers.udp.connections;

import java.net.DatagramPacket;

import sneer.bricks.network.computers.connections.ConnectionManager;
import basis.brickness.Brick;


@Brick
public interface UdpConnectionManager extends ConnectionManager.Delegate {
	
	enum PacketType {Hail, Data}
	
	static final int KEEP_ALIVE_PERIOD = 10000;
	static final int IDLE_PERIOD = 3 * KEEP_ALIVE_PERIOD;

	void handle(DatagramPacket packet);
	

}
