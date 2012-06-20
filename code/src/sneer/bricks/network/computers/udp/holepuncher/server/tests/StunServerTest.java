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
		byte[] seal2 = seal(2);
		byte[] seal3 = seal(3);

		byte[][] peersToFind = new byte[][]{seal1, seal2};

		DatagramPacket[] replies;
		
		replies = subjectsRepliesFor(seal1, ip("200.243.227.1"), 4111, "local data 1".getBytes());
		assertEmpty(replies);
		replies = subjectsRepliesFor(seal2, ip("200.243.227.2"), 4222, "local data 2".getBytes());
		assertEmpty(replies);
		replies = subjectsRepliesFor(seal3, ip("200.243.227.3"), 4333, "local data 3".getBytes(),
			peersToFind
		);
		
		assertReply(replies[0], "200.243.227.3", 4333, seal1, "200.243.227.1", 4111, "local data 1");
		assertReply(replies[1], "200.243.227.1", 4111, seal3, "200.243.227.3", 4333, "local data 3");
//		assertReply(replies[2], "200.243.227.2", 4222, seal3, "200.243.227.3", 4333, "local data 3");
	}


	private void assertEmpty(DatagramPacket[] replies) {
		assertEquals(0, replies.length);
	}


	private void assertReply(DatagramPacket replyPacket, String ip, int port, byte[] seal, String peerIp, int peerPort, String peerAddressData) throws UnknownHostException {
		assertEquals(ip, replyPacket.getAddress().getHostAddress());
		assertEquals(port, replyPacket.getPort());
		
		StunReply reply = unmarshalReply(replyPacket);
		assertArrayEquals(seal, reply.peerSeal);
		assertEquals(ip(peerIp), reply.peerIp);
		assertEquals(peerPort, reply.peerPort);
		assertEquals(peerAddressData, new String(reply.peerLocalAddressData));
	}


	private StunReply unmarshalReply(DatagramPacket packet) {
		ByteBuffer buf = asBuffer(packet);
		assertSame(UdpPacketType.Stun, UdpPacketType.search(buf.get()));
		return my(StunProtocol.class).unmarshalReply(buf);
	}


	private DatagramPacket[] subjectsRepliesFor(byte[] ownSeal, InetAddress ip, int port, byte[] localAddressData, byte[]... peersToFind) {
		StunRequest request = new StunRequest(ownSeal, peersToFind, localAddressData);
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
