package spikes.neo.packet;

import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import basis.brickness.Brick;
import basis.lang.Consumer;

@Brick
public interface UdpPackets {

	PacketScheduler splitScheduler(PacketScheduler scheduler, int payloadSize);

	Consumer<byte[]> joinReceiver(Consumer<byte[]> receiver);

}
