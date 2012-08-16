package sneer.bricks.expression.files.map.mapper.impl;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import basis.lang.CacheMap;
import basis.lang.Producer;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.io.log.Logger;

class FileMapperImpl implements FileMapper {

	private final CacheMap<File, MapperWorker> _workersByFileOrFolder = CacheMap.newInstance();


	@Override
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
			return new MapperWorker(fileOrFolder.toPath(), acceptedFileExtensions);
		}});
	}


	@Override
	public void stopMapping(final File folder) {
		MapperWorker worker = _workersByFileOrFolder.get(folder);
		if (worker == null) return;
		worker.stop();
		_workersByFileOrFolder.remove(folder);
	}

	@Override
	public File getExistingMappedFile(final Hash hash) throws FileNotFoundException {
		List<String> fileNames = my(FileMap.class).getFiles(hash);
		for (String fileName : fileNames) {
			File file = new File(fileName);
			if (file.exists()) return file;
		}
		throw new FileNotFoundException("Mapped file not found with hash " + hash + " (might have been deleted or renamed)");
	}
}
