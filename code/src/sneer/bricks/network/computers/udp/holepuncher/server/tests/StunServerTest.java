package sneer.bricks.network.computers.udp.holepuncher.server.tests;

import static basis.environments.Environments.my;
import static sneer.bricks.network.computers.udp.UdpNetwork.MAX_PACKET_PAYLOAD_SIZE;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import sneer.bricks.network.computers.udp.connections.UdpPacketType;
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
		assertEquals(0, subjectsRepliesFor(seal1, ip("200.243.227.1"), 4111, null, "local data 1".getBytes()).length);
		
		byte[] seal2 = seal(2);
		byte[] peerToFind = seal1;
		DatagramPacket[] replies = subjectsRepliesFor(seal2, ip("200.243.227.2"), 4222, peerToFind, "local data 2".getBytes());
		
		StunReply replyTo2 = unmarshalReply(replies[0]);
		StunReply replyTo1 = unmarshalReply(replies[1]);
		
		assertArrayEquals(seal1, replyTo2.peerSeal);
		assertEquals(ip("200.243.227.1"), replyTo2.peerIp);
		assertEquals(4111, replyTo2.peerPort);
		assertEquals("local data 1", new String(replyTo2.peerLocalAddressData));
		
		assertArrayEquals(seal2, replyTo1.peerSeal);
		assertEquals(ip("200.243.227.2"), replyTo1.peerIp);
		assertEquals(4222, replyTo1.peerPort);
		assertEquals("local data 2", new String(replyTo1.peerLocalAddressData));				
	}


	private StunReply unmarshalReply(DatagramPacket packet) {
		ByteBuffer buf = asBuffer(packet);
		assertSame(UdpPacketType.Stun, UdpPacketType.search(buf.get()));
		return my(StunProtocol.class).unmarshalReply(buf);
	}


	private DatagramPacket[] subjectsRepliesFor(byte[] ownSeal, InetAddress ip, int port, byte[] peerToFind, byte[] localAddressData) {
		byte[][] peerSealsToFind = peerToFind == null 
			? new byte[][]{} 
			: new byte[][]{peerToFind};
		StunRequest request = new StunRequest(ownSeal, peerSealsToFind, localAddressData);
		ByteBuffer buf = newBuf();
		my(StunProtocol.class).marshalRequestTo(request, buf);
		return subjectsReplyFor(asPacket(buf, ip, port));		
	}


	static private DatagramPacket asPacket(ByteBuffer buf, InetAddress ip, int port) {
		return new DatagramPacket(buf.array(), buf.position(), ip, port);
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

	
	private ByteBuffer newBuf() {
		return ByteBuffer.wrap(new byte[MAX_PACKET_PAYLOAD_SIZE]);
	}

	
	private InetAddress ip(String address) throws UnknownHostException {
		return InetAddress.getByName(address);
	}
	
	
	static private ByteBuffer asBuffer(DatagramPacket packet) {
		return ByteBuffer.wrap(packet.getData(), 0, packet.getLength());
	}
	
}
