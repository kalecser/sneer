package dfcsantos.tracks.rejected;

import sneer.bricks.hardware.cpu.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface RejectedTracksKeeper {

	void reject(Sneer1024 hash);

	boolean isRejected(Sneer1024 hash);

}
