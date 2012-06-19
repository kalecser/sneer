package sneer.main.stun;

import static basis.environments.Environments.my;
import sneer.bricks.network.computers.udp.holepuncher.server.listener.StunServerListener;
import sneer.main.SneerSessionBase;

public class StunServerSession extends SneerSessionBase {

	public static void main(String[] args) {
		new StunServerSession();
	}

	@Override
	protected void start() {
		my(StunServerListener.class);
	}

}
