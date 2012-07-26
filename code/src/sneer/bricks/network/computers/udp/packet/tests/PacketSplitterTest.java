package sneer.bricks.network.computers.udp.packet.tests;

import static basis.environments.Environments.my;
import static java.lang.String.format;
import static sneer.bricks.network.computers.udp.packet.PacketSplitter.OpCode.First;
import static sneer.bricks.network.computers.udp.packet.PacketSplitter.OpCode.Unique;

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
		assertJoinedMessage("First(SEQ: 1 REMAINING: 1): Hey Ne", "Piece(SEQ: 1 ORDER: 1): ide");
	}
	

	@Test(timeout = 1000)
	public void splitSeveralPacketsForEachMessage() {
		sendSplittedBy(8, "Hey Neide", "How are you?");
		assertJoinedMessage("First(SEQ: 1 REMAINING: 1): Hey N", "Piece(SEQ: 1 ORDER: 1): eide",
							"First(SEQ: 2 REMAINING: 2): How a", "Piece(SEQ: 2 ORDER: 1): re yo", "Piece(SEQ: 2 ORDER: 2): u?");
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
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		OpCode type = OpCode.search(buffer.get());

		if (type == Unique)
			return format("Unique: %s", payload(buffer));
		
		return format("%s(SEQ: %s %s: %s): %s", type, buffer.get(), (type == First ? "REMAINING" : "ORDER"), buffer.get(), payload(buffer));
	}


	private String payload(ByteBuffer buffer) {
		byte[] payload = new byte[buffer.remaining()];
		buffer.get(payload);
		return new String(payload);
	}


}
