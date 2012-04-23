package sneer.bricks.network.computers.tcp.protocol;

import static basis.environments.Environments.my;
import basis.brickness.Brick;
import sneer.bricks.hardware.cpu.lang.Lang;

@Brick
public interface ProtocolTokens {

	static final byte[] FALLBACK = my(Lang.class).strings().toByteArray("Fallback");
	static final byte[] CONFIRMED = my(Lang.class).strings().toByteArray("Confirmed");
	static final byte[] SNEER_WIRE_PROTOCOL_1 = my(Lang.class).strings().toByteArray("Sneer Wire Protocol 1");

}
