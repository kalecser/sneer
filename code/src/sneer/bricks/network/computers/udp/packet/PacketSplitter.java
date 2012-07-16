package sneer.bricks.network.computers.udp.packet;

import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import basis.brickness.Brick;

@Brick
public interface PacketSplitter {

	PacketScheduler splitScheduler(PacketScheduler scheduler, int payloadSize);

}
