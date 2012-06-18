package sneer.main.stun;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import sneer.bricks.hardware.io.log.exceptions.ExceptionLogger;
import sneer.bricks.network.computers.udp.UdpNetwork;
import sneer.bricks.network.computers.udp.UdpNetwork.UdpSocket;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.server.StunServer;
import sneer.main.SneerSessionBase;
import basis.lang.Consumer;

public class StunServerSession extends SneerSessionBase implements Consumer<DatagramPacket> {

	private UdpSocket socket;

	public static void main(String[] args) {
		new StunServerSession();
	}

	@Override
	protected void start() {
		InetSocketAddress serverAddress = my(StunProtocol.class).serverAddress();
		try {
			socket = my(UdpNetwork.class).openSocket(serverAddress.getPort());
		} catch (SocketException e) {
			throw new IllegalStateException(e);
		}
		socket.initReceiver(this);
	}

	@Override
	public void consume(DatagramPacket packet) {
		DatagramPacket[] replies = my(StunServer.class).repliesFor(packet);
		for (DatagramPacket reply : replies)
			tryToSend(reply);
	}

	private void tryToSend(DatagramPacket reply) {
		try {
			socket.send(reply);
		} catch (IOException e) {
			my(ExceptionLogger.class).log(e);
		}
	}

}
