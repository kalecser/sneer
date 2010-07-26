package sneer.bricks.expression.files.map.mapper.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.io.log.Logger;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;

class FileMapperImpl implements FileMapper {

	final static FileMap FileMap = my(FileMap.class);

	private final CacheMap<File, MapperWorker> _workersByFileOrFolder = CacheMap.newInstance();


	public Hash mapFileOrFolder(final File fileOrFolder, final String... acceptedFileExtensions) throws MappingStopped,	IOException {
		my(Logger.class).log("FileMapper starting to Map: ", fileOrFolder);
		try {
			return workerFor(fileOrFolder, acceptedFileExtensions).result();
		} finally {
			_workersByFileOrFolder.remove(fileOrFolder);
		}
	}


	private MapperWorker workerFor(final File fileOrFolder, final String... acceptedFileExtensions) {
		return _workersByFileOrFolder.get(fileOrFolder, new Producer<MapperWorker>() { @Override public MapperWorker produce() {
			return new MapperWorker(fileOrFolder, acceptedFileExtensions);
		}});
	}


	@Override
	public void stopMapping(final File folder) {
		MapperWorker worker = _workersByFileOrFolder.get(folder);
		if (worker == null) return;
		worker.stop();
		_workersByFileOrFolder.remove(folder);
	}

}
