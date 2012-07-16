package sneer.bricks.network.computers.udp.packet.tests;

import static basis.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import sneer.bricks.network.computers.udp.packet.PacketSplitter;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

public class PacketSplitterTest extends BrickTestBase {

	private PacketSplitter subject = my(PacketSplitter.class);

	private PacketSchedulerMock schedulerMock;
	private PacketScheduler splitted;

	@Test(timeout = 1000)
	public void dontSplitPackets() {
		sendSplittedBy(9, "Hey Neide");
		assertJoinedMessage("Hey Neide");
	}

	@Test(timeout = 1000)
	public void dontSplitPacketsForEachMessage() {
		sendSplittedBy(12, "Hey Neide", "How are you?");
		assertJoinedMessage("Hey Neide", "How are you?");
	}

	@Test(timeout = 1000)
	public void splitSeveralPackets() {
		sendSplittedBy(5, "Hey Neide");
		assertJoinedMessage("Hey N", "eide");
	}

	@Test(timeout = 1000)
	public void splitSeveralPacketsForEachMessage() {
		sendSplittedBy(5, "Hey Neide", "How are you?");
		assertJoinedMessage("Hey N", "eide", "How a", "re yo", "u?");
	}

	private void assertJoinedMessage(String... expected) {
		List<String> builder = new ArrayList<String>();

		while (schedulerMock.hasMorePackets()) {
			builder.add(new String(splitted.highestPriorityPacketToSend()));
			splitted.previousPacketWasSent();
		}

		assertArrayEquals(expected, builder.toArray());
	}

	private void sendSplittedBy(int payloadSize, String... message) {
		schedulerMock = new PacketSchedulerMock(message);
		splitted = subject.splitScheduler(schedulerMock, payloadSize);
	}

}
