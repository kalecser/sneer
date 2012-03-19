package spikes.adenauer.puncher.server.tests;

import static sneer.bricks.pulp.network.udp.UdpNetwork.MAX_PACKET_PAYLOAD_SIZE;
import static sneer.foundation.environments.Environments.my;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.junit.Test;

import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import spikes.adenauer.puncher.server.StunServer;


public class StunServerTest extends BrickTestBase {

	private final StunServer subject = my(StunServer.class);
	
	@Test
	public void ownIp() throws UnknownHostException {
		DatagramPacket packet = new DatagramPacket(newBuf(), 0, ip("10.42.10.42"), 4242); 
		handleAndUseForReply(packet);
		byte[] reply = payloadIn(packet);
		assertArrayEquals(asBytes("10.42.10.42"), reply);
	}


	private void handleAndUseForReply(DatagramPacket packet) {
		InetAddress ip = packet.getAddress();
		int port = packet.getPort();
		
		subject.handleAndUseForReply(packet);

		assertEquals(0, packet.getOffset());
		assertEquals(ip, packet.getAddress());
		assertEquals(port, packet.getPort());
	}

	
	private static byte[] payloadIn(DatagramPacket packet) {
		return Arrays.copyOf(packet.getData(), packet.getLength());
	}
	
	
	private byte[] newBuf() {
		return new byte[MAX_PACKET_PAYLOAD_SIZE];
	}

	
	private InetAddress ip(String address) throws UnknownHostException {
		return InetAddress.getByName(address);
	}

	
	private byte[] asBytes(String address) throws UnknownHostException {
		return InetAddress.getByName(address).getAddress();
	}
	
}
