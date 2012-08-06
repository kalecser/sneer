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
		assertSplittedPackets(splittedBy(6, "Hey Neide"), "Hey Ne", "ide");
		assertSplittedPackets(splittedBy(9, "Hey Neide"), "Hey Neide");
		assertSplittedPackets(splittedBy(10, "Hey Neide"), "Hey Neide");
	}
	

	private ByteBuffer[] splittedBy(int payloadSize, String packet) {
		return subject.splitBy(payloadSize, ByteBuffer.wrap(packet.getBytes()));
	}
	
	
	private void assertSplittedPackets(ByteBuffer[] splitted, String... expected) {
		List<String> result = new ArrayList<String>();
		
		for (ByteBuffer packet : splitted)
			result.add(new String(packet.array()));

		assertArrayEquals(expected, result.toArray());
	}


}
