package dfcsantos.tracks.storage.rejected.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import dfcsantos.tracks.storage.rejected.RejectedTracksKeeper;

public class RejectedTracksKeeperTest extends BrickTestBase {

	private RejectedTracksKeeper _subject = my(RejectedTracksKeeper.class);

	@Test
	public void testTrackRejection() throws Exception {
		assertFalse(_subject.isRejected(hash(1)));
		_subject.strongReject(hash(1));
		assertTrue(_subject.isRejected(hash(1)));
		assertFalse(_subject.isRejected(hash(2)));
	}

	private Hash hash(int byte_) {
		return new Hash(new byte[]{(byte)byte_});
	}

}
