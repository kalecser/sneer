package sneer.bricks.network.computers.udp.packet.tests;

import static basis.environments.Environments.my;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sneer.bricks.network.computers.udp.packet.PacketSplitter;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class PacketSplitterTest extends BrickTestBase {

	
	private PacketSplitter subject = my(PacketSplitter.class);

	
	@Test
	public void split() {
		assertSplitPackets(splitBy(3, ""), "");
		assertSplitPackets(splitBy(3, "Hey Neide"), "Hey", " Ne", "ide");
		assertSplitPackets(splitBy(4, "Hey Neide"), "Hey", " Ne", "ide");
		assertSplitPackets(splitBy(5, "Hey Neide"), "Hey N", "eide");
		assertSplitPackets(splitBy(6, "Hey Neide"), "Hey N", "eide");
		assertSplitPackets(splitBy(8, "Hey Neide"), "Hey N", "eide");
		assertSplitPackets(splitBy(9, "Hey Neide"), "Hey Neide");
		assertSplitPackets(splitBy(10, "Hey Neide"), "Hey Neide");
	}
	
	
	@Test
	public void join() {
		assertJoinedPackets(joined("Hey Neide"), "Hey Neide");
		assertJoinedPackets(joined("Hey Ne", "ide"), "Hey Neide");
		assertJoinedPackets(joined("Hey Ne", "ide! ", "How are you?"), "Hey Neide! How are you?");
	}
	

	private ByteBuffer[] splitBy(int payloadSize, String packet) {
		ByteBuffer buff = ByteBuffer.allocate(100);
		buff.put(packet.getBytes());
		buff.flip();
		
		return subject.splitBy(buff, payloadSize);
	}
	
	
	private void assertSplitPackets(ByteBuffer[] pieces, String... expected) {
		List<String> actual = new ArrayList<String>();
		
		for (ByteBuffer piece : pieces) {
			byte[] bytes = new byte[piece.remaining()];
			piece.get(bytes);
			actual.add(new String(bytes));
		}

		assertArrayEquals(expected, actual.toArray());
	}


	private ByteBuffer[] joined(String... packets) {
		ByteBuffer[] ret = new ByteBuffer[packets.length];
		
		for(int i = 0; i < packets.length; i++)
			ret[i] = ByteBuffer.wrap(packets[i].getBytes());
		
		return ret;
	}


	private void assertJoinedPackets(ByteBuffer[] buffersToJoin, String expected) {
		ByteBuffer joined = subject.join(buffersToJoin);
		byte[] bytes = new byte[joined.remaining()];
		joined.get(bytes);
		
		assertEquals(expected, new String(bytes));
	}


}
