package sneer.bricks.network.computers.udp.holepuncher.server.tests;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.UdpNetwork.MAX_PACKET_PAYLOAD_SIZE;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.junit.Test;

import sneer.bricks.network.computers.udp.holepuncher.protocol.StunProtocol;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunReply;
import sneer.bricks.network.computers.udp.holepuncher.protocol.StunRequest;
import sneer.bricks.network.computers.udp.holepuncher.server.StunServer;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;


public class StunServerTest extends BrickTestBase {

	//TODO Spread outgoing port.
	
	private final StunServer subject = my(StunServer.class);
	
	
	@Test
	public void stun() throws Exception {
		byte[] seal1 = seal(1);
		assertEquals(0, subjectsRepliesFor(seal1, ip("200.243.227.1"), 4111, ip("10.42.10.1"), 1001, null).length);
		
		byte[] seal2 = seal(2);
		byte[] peerToFind = seal1;
		DatagramPacket[] replies = subjectsRepliesFor(seal2, ip("205.65.114.2"), 4222, ip("10.42.10.2"), 1002, peerToFind);
		
		StunReply replyToMe = unmarshalReply(replies[0]);
		StunReply replyToPeer = unmarshalReply(replies[1]);
		
		assertArrayEquals(seal1, replyToMe.peerSeal);
		assertEquals(ip("200.243.227.1"), replyToMe.peerIp);
		assertEquals(4111, replyToMe.peerPort);
		assertEquals(ip("10.42.10.1"), replyToMe.peerLocalIp);
		assertEquals(1001, replyToMe.peerLocalPort);
		
		assertArrayEquals(seal2, replyToPeer.peerSeal);
		assertEquals(ip("205.65.114.2"), replyToPeer.peerIp);
		assertEquals(4222, replyToPeer.peerPort);
		assertEquals(ip("10.42.10.2"), replyToPeer.peerLocalIp);
		assertEquals(1002, replyToPeer.peerLocalPort);		
	}


	private StunReply unmarshalReply(DatagramPacket packet) {
		return my(StunProtocol.class).unmarshalReply(packet.getData(), packet.getLength());
	}


	private DatagramPacket[] subjectsRepliesFor(byte[] ownSeal, InetAddress ip, int port, InetAddress localIp, int localPort, byte[] peerToFind) {
		StunRequest request = new StunRequest(ownSeal, localIp, localPort, peerToFind);
		byte[] buf = newBuf();
		int length = my(StunProtocol.class).marshalRequestTo(request, buf);
		return subjectsReplyFor(new DatagramPacket(buf, length, ip, port));
	}

	
	private byte[] seal(int number) {
		byte[] ret = new byte[64];
		Arrays.fill(ret, (byte)number);
		return ret;
	}


	private DatagramPacket[] subjectsReplyFor(DatagramPacket packet) {
		InetAddress ip = packet.getAddress();
		int port = packet.getPort();
		
		DatagramPacket[] replies = subject.repliesFor(packet);
		if (replies.length == 0) return replies;

		assertEquals(0, replies[0].getOffset());
		assertEquals(ip, replies[0].getAddress());
		assertEquals(port, replies[0].getPort());
		return replies;
	}

	
	private byte[] newBuf() {
		return new byte[MAX_PACKET_PAYLOAD_SIZE];
	}

	
	private InetAddress ip(String address) throws UnknownHostException {
		return InetAddress.getByName(address);
	}
	
}
