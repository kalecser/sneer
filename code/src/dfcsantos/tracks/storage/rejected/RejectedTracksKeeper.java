package dfcsantos.tracks.storage.rejected;

import basis.brickness.Brick;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;

@Brick(Prevalent.class)
public interface RejectedTracksKeeper {

	void strongReject(Hash hash);
	void weakReject(Hash hash);


	boolean isWeakRejected(Hash hash);
	boolean isRejected(Hash hash);

}
