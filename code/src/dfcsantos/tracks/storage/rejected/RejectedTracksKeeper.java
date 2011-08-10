package dfcsantos.tracks.storage.rejected;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.foundation.brickness.Brick;

@Brick(Prevalent.class)
public interface RejectedTracksKeeper {

	void reject(Hash hash);

	boolean isRejected(Hash hash);

}
