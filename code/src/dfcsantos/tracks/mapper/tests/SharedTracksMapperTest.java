package dfcsantos.tracks.mapper.tests;

import static sneer.foundation.environments.Environments.my;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import dfcsantos.tracks.folder.keeper.TracksFolderKeeper;
import dfcsantos.tracks.mapper.SharedTracksMapper;

public class SharedTracksMapperTest extends BrickTest {

	@Bind private final TracksFolderKeeper _tracksFolderKeeper = mock(TracksFolderKeeper.class);
	@Bind private final FileMapper _folderMapper = mock(FileMapper.class);

	@Ignore
	@Test (timeout = 2000)
	public void sharedTracksMappingTest() {
		checking(new Expectations(){{
			exactly(2).of(_tracksFolderKeeper).sharedTracksFolder(); will(returnValue(my(Signals.class).constant(tmpFolder())));
			exactly(1).of(_folderMapper).startFolderMapping(tmpFolder(), "mp3");
		}});

		SharedTracksMapper mapper = my(SharedTracksMapper.class); 
		mapper.waitTillMappingIsFinished();
	}

}
