package dfcsantos.tracks.storage.rejected;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.foundation.brickness.Brick;

@Brick
public interface RejectedTracksKeeper {

	void reject(Hash hash);

	boolean isRejected(Hash hash);

}
