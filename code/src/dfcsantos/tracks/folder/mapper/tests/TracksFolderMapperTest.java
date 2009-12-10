package dfcsantos.tracks.folder.mapper.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import dfcsantos.tracks.folder.keeper.TracksFolderKeeper;
import dfcsantos.tracks.folder.mapper.TracksFolderMapper;

public class TracksFolderMapperTest extends BrickTest {

	@Bind private final TracksFolderKeeper _tracksFolderKeeper = mock(TracksFolderKeeper.class);
	@Bind private final FileMap _fileMap = mock(FileMap.class);

	private final TracksFolderMapper _subject = my(TracksFolderMapper.class);

//	@Before
//	public void init() throws IOException {
//		createTmpFiles("track1.mp3", "track2.mp3", "track3.mp3", "track4.ogg", "track5.wav");
//	}

	@Test (timeout = 2000)
	public void tracksFolderMappingTest() throws IOException {
		checking(new Expectations(){{
			exactly(1).of(_tracksFolderKeeper).peerTracksFolder(); will(returnValue(tmpFolder()));
			exactly(2).of(_tracksFolderKeeper).sharedTracksFolder(); will(returnValue(my(Signals.class).constant(tmpFolder())));
			exactly(2).of(_fileMap).put(tmpFolder(), "mp3");
//			exactly(1).of(_fileMap).put(new File(tmpFolder(), "track1.mp3"));
//			exactly(1).of(_fileMap).put(new File(tmpFolder(), "track2.mp3"));
//			exactly(1).of(_fileMap).put(new File(tmpFolder(), "track3.mp3"));
		}});

		_subject.startMapping();
		_subject.waitMapping();
	}

}
