package sneer.bricks.network.computers.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

import basis.brickness.Brick;



@Brick
public interface UdpNetwork {

	static final int MAX_PACKET_PAYLOAD_SIZE = 1024 * 20; //2k max. 1k is ideal for UDP Packets

	UdpSocket openSocket(int portNumber) throws SocketException;

	interface UdpSocket {
		void receive(DatagramPacket packet) throws IOException;
		void send(DatagramPacket packet) throws IOException;
		void crash();
	}
	
}
