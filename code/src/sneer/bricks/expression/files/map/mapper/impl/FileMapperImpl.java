package sneer.bricks.expression.files.map.mapper.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import sneer.bricks.expression.files.hasher.FolderContentsHasher;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.arrays.ImmutableArray;

class FileMapperImpl implements FileMapper {

	private final static FileMap FileMap = my(FileMap.class);

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
		if (_workersByFileOrFolder.get(folder) == null) return;
		final MapperWorker folderMapping = _workersByFileOrFolder.remove(folder);
		folderMapping.stop();
	}


	private class MapperWorker {

		private final File _fileOrFolder;
		private final FileFilter _extensionsFilter;

		private Hash _result;
		private Exception _exception;

		private final AtomicBoolean _stop = new AtomicBoolean(false);


		private MapperWorker(File fileOrFolder, String... acceptedFileExtensions) {
			_fileOrFolder = fileOrFolder;
			_extensionsFilter = filterFor(acceptedFileExtensions);
		}


		private FileFilter filterFor(String... acceptedFileExtensions) {
			return acceptedFileExtensions.length > 0
				? my(IO.class).fileFilters().foldersAndExtensions(acceptedFileExtensions)
				: my(IO.class).fileFilters().any();
		}


		synchronized
		Hash result() throws MappingStopped, IOException {
			if (_result == null && _exception == null) run();
			if (_exception != null) throwNarrowed(_exception);
			return _result;
		}
		
		
		private void run() {
			my(CpuThrottle.class).limitMaxCpuUsage(20, new Closure() { @Override public void run() {
				try {
					_result = _fileOrFolder.isDirectory()
						? mapFolder(_fileOrFolder)
						: mapFile(_fileOrFolder);
				} catch (Exception e) {
					_exception = e;
				}
			}});
		}


		private void throwNarrowed(Exception e) throws MappingStopped, IOException {
			if (e instanceof MappingStopped) throw (MappingStopped) e;
			if (e instanceof IOException) throw (IOException) e;
			throw new IllegalStateException(e);
		}


		void stop() {
			_stop.set(true);
		}


		private Hash mapFolder(File folder) throws MappingStopped, IOException {
			FolderContents contents = new FolderContents(immutable(mapFolderEntries(folder)));
			Hash hash = my(FolderContentsHasher.class).hash(contents);
			try {
				FileMap.putFolder(folder.getAbsolutePath(), hash);
			} catch (RuntimeException e) {
				String entries = "";
				for (FileOrFolder entry : contents.contents)
					entries += "\n" + entry.toString();
				throw new IllegalStateException("Exception trying to map folder: " + folder + " entries: " + entries, e);
			}
			return hash;
		}


		private List<FileOrFolder> mapFolderEntries(File folder) throws MappingStopped, IOException{
			List<FileOrFolder> result = new ArrayList<FileOrFolder>();
			for (File entry : sortedFiles(folder)) {
				if (_stop.get()) throw new MappingStopped();
				result.add(mapFolderEntry(entry));
			}
			return result;
		}


		private FileOrFolder mapFolderEntry(File fileOrFolder) throws IOException, MappingStopped {
			Hash hash;
			String name = fileOrFolder.getName();
			
			if (fileOrFolder.isDirectory()) {
				hash = mapFolder(fileOrFolder);
				return new FileOrFolder(name, hash);
			}
			
			hash = mapFile(fileOrFolder);
			return new FileOrFolder(name, fileOrFolder.lastModified(), hash);
		}


		private Hash mapFile(File file) {
			String path = file.getAbsolutePath();
			long lastModified = file.lastModified();

			Hash result = FileMap.getHash(path);
			if (result != null && lastModified == FileMap.getLastModified(path))
				return result;

			try {
				result = my(Crypto.class).digest(file);
			} catch (IOException e) {
				my(BlinkingLights.class).turnOn(LightType.ERROR, "File Mapping Error", "This can happen if your file has weird characters in the name or if your disk is failing.", e);
				return my(Crypto.class).digest(new byte[0]);
			}
			FileMap.putFile(path, lastModified, result);
			return result;
		}

		
		private File[] sortedFiles(File folder) {
			File[] result = folder.listFiles(_extensionsFilter);
			if (result == null)	return new File[0];
			Arrays.sort(result, new Comparator<File>() { @Override public int compare(File file1, File file2) {
				return file1.getName().compareTo(file2.getName());
			}});
			return result;
		}


		private ImmutableArray<FileOrFolder> immutable(List<FileOrFolder> entries) {
			return new ImmutableArray<FileOrFolder>(entries);
		}

	}

}
