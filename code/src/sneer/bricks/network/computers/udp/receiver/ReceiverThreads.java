package sneer.bricks.network.computers.udp.receiver;

import java.net.DatagramPacket;

import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import basis.brickness.Brick;
import basis.lang.Consumer;

@Brick
public interface ReceiverThreads {

	void start(UdpSocket socket, Consumer<DatagramPacket> receiver);

}
