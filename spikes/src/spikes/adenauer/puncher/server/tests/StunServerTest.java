package spikes.adenauer.puncher.server.tests;

import static sneer.bricks.pulp.network.udp.UdpNetwork.MAX_PACKET_PAYLOAD_SIZE;
import static sneer.foundation.environments.Environments.my;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.junit.Test;

import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import spikes.adenauer.puncher.server.StunServer;
import spikes.adenauer.puncher.server.impl.StunReply;
import spikes.adenauer.puncher.server.impl.StunRequest;


public class StunServerTest extends BrickTestBase {

	//TODO Spread outgoing port.
	
	private final StunServer subject = my(StunServer.class);
	
	
	@Test
	public void stun() throws Exception {
		byte[] seal1 = seal(1);
		assertNull(subjectsReplyFor(seal1, ip("200.243.227.1"), 4111, ip("10.42.10.1"), 1001, null));
		
		byte[] seal2 = seal(2);
		byte[] peerToFind = seal1;
		DatagramPacket packet2 = subjectsReplyFor(seal2, ip("205.65.114.2"), 4222, ip("10.42.10.2"), 1002, peerToFind);
		
		StunReply reply = unmarshalReply(packet2);
		assertArrayEquals(seal1, reply.peerSeal);
		assertEquals(ip("200.243.227.1"), reply.peerIp);
		assertEquals(4111, reply.peerPort);
		assertEquals(ip("10.42.10.1"), reply.peerLocalIp);
		assertEquals(1001, reply.peerLocalPort);
	}


	private StunReply unmarshalReply(DatagramPacket packet) throws IOException {
		return StunReply.unmarshalFrom(packet.getData(), packet.getLength());
	}


	private DatagramPacket subjectsReplyFor(byte[] ownSeal, InetAddress ip, int port, InetAddress localIp, int localPort, byte[] peerToFind) {
		byte[] buf = newBuf();
		int length = new StunRequest(ownSeal, localIp, localPort, peerToFind).marshalTo(buf);
		return subjectsReplyFor(new DatagramPacket(buf, length, ip, port));
	}

	
	private byte[] seal(int number) {
		byte[] ret = new byte[64];
		Arrays.fill(ret, (byte)number);
		return ret;
	}


	private DatagramPacket subjectsReplyFor(DatagramPacket packet) {
		InetAddress ip = packet.getAddress();
		int port = packet.getPort();
		
		DatagramPacket reply = subject.replyFor(packet);
		if (reply == null) return null;

		assertEquals(0, reply.getOffset());
		assertEquals(ip, reply.getAddress());
		assertEquals(port, reply.getPort());
		return reply;
	}

	
	private byte[] newBuf() {
		return new byte[MAX_PACKET_PAYLOAD_SIZE];
	}

	
	private InetAddress ip(String address) throws UnknownHostException {
		return InetAddress.getByName(address);
	}
	
}
