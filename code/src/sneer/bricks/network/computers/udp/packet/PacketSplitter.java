package sneer.bricks.network.computers.udp.packet;

import sneer.bricks.network.computers.connections.ByteConnection.PacketScheduler;
import basis.brickness.Brick;

@Brick
public interface PacketSplitter {
	
	public enum OpCode {
		
		Unique, First, Piece;

		static public OpCode search(int ordinal) {
			if (ordinal < 0) return null;
			if (ordinal >= values().length) return null;
			return OpCode.values()[ordinal];
		}
	}

	PacketScheduler splitScheduler(PacketScheduler scheduler, int payloadSize);

}
