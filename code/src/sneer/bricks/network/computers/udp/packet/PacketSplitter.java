package sneer.bricks.network.computers.udp.packet;

import java.nio.ByteBuffer;
import basis.brickness.Brick;

@Brick
public interface PacketSplitter {
	
	ByteBuffer[] splitBy(int size, ByteBuffer packet);
	
	ByteBuffer join(ByteBuffer[] packets);

}
