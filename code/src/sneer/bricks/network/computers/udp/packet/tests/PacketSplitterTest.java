package sneer.bricks.network.computers.udp.packet.tests;

import static basis.environments.Environments.my;
import static java.lang.String.format;
import static java.util.Arrays.copyOfRange;
import static sneer.bricks.network.computers.udp.packet.PacketSplitter.OpCode.Checksum;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import sneer.bricks.network.computers.udp.packet.PacketSplitter;
import sneer.bricks.network.computers.udp.packet.PacketSplitter.OpCode;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class PacketSplitterTest extends BrickTestBase {

	private PacketSplitter subject = my(PacketSplitter.class);

	private PacketSchedulerMock schedulerMock;
	private PacketScheduler splitted;
	

	@Test(timeout = 1000)
	public void dontSplitPackets() {
		sendSplittedBy(10, "Hey Neide");
		assertJoinedMessage("Unique: Hey Neide");
	}
	

	@Test(timeout = 1000)
	public void dontSplitPacketsForEachMessage() {
		sendSplittedBy(13, "Hey Neide", "How are you?");
		assertJoinedMessage("Unique: Hey Neide", "Unique: How are you?");
	}
	

	@Test(timeout = 1000)
	public void splitSeveralPackets() {
		sendSplittedBy(9, "Hey Neide");
		assertJoinedMessage("Piece: Hey Neid", "Piece: e", "Checksum: 3394");
	}
	

	@Test(timeout = 1000)
	public void splitSeveralPacketsForEachMessage() {
		sendSplittedBy(9, "Hey Neide", "How are you?");
		assertJoinedMessage("Piece: Hey Neid", "Piece: e", "Checksum: 3394",
							"Piece: How are ", "Piece: you?", "Checksum: 6063");
	}
	

	private void sendSplittedBy(int payloadSize, String... message) {
		schedulerMock = new PacketSchedulerMock(message);
		splitted = subject.splitScheduler(schedulerMock, payloadSize);
	}
	
	
	private void assertJoinedMessage(String... expected) {
		List<String> result = new ArrayList<String>();

		while (schedulerMock.hasMorePackets()) {
			String message = formatData(splitted.highestPriorityPacketToSend());
			result.add(message);			
			splitted.previousPacketWasSent();
		}

		assertArrayEquals(expected, result.toArray());
	}


	private String formatData(byte[] bytes) {
		OpCode type = OpCode.search(bytes[0]);
		
		Object data;
		if (type == Checksum) {
			ByteBuffer buffer = ByteBuffer.wrap(copyOfRange(bytes, 1, bytes.length));
			data = buffer.getLong();
		} else {
			data = new String(copyOfRange(bytes, 1, bytes.length));
		}
		
		return format("%s: %s", type, data);
	}


}
