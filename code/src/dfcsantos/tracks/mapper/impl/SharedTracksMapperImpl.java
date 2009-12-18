package dfcsantos.tracks.mapper.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardwaresharing.files.map.mapper.FileMapper;
import sneer.foundation.lang.Consumer;
import dfcsantos.tracks.folder.keeper.TracksFolderKeeper;
import dfcsantos.tracks.mapper.SharedTracksMapper;

class SharedTracksMapperImpl implements SharedTracksMapper {

	private static final FileMapper _fileMapper = my(FileMapper.class);

	private File _oldSharedTracksFolder;

	@SuppressWarnings("unused") private WeakContract _toAvoidGC;

	{
		_toAvoidGC = my(TracksFolderKeeper.class).sharedTracksFolder().addReceiver(new Consumer<File>() { @Override public void consume(File newSharedTracksFolder) {
			if (_oldSharedTracksFolder != null)
				_fileMapper.stopFolderMapping(_oldSharedTracksFolder);

			_fileMapper.startFolderMapping(sharedTracksFolder(), "mp3");

			_oldSharedTracksFolder = newSharedTracksFolder;
		}});
	}

	@Override
	public void waitTillMappingIsFinished() {
		_fileMapper.waitTillFolderMappingIsFinished(sharedTracksFolder());
	}

	private File sharedTracksFolder() {
		return my(TracksFolderKeeper.class).sharedTracksFolder().currentValue();
	}

}
