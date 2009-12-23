package sneer.bricks.expression.files.map.mapper.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
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
import sneer.bricks.hardware.cpu.algorithms.crypto.Crypto;
import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.ram.arrays.ImmutableArray;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.ProducerWithThrowable;

class FileMapperImpl implements FileMapper {

	private final CacheMap<File, FolderMapping> _mappingsByFolder = CacheMap.newInstance();

	@Override
	public Sneer1024 mapFile(File file) throws IOException {
		Sneer1024 hash = my(Crypto.class).digest(file);
		my(FileMap.class).putFile(file, hash);
		return hash;
	}

	@Override
	public Sneer1024 mapFolder(final File folder, final String... acceptedFileExtensions) throws MappingStopped {
		FolderMapping folderMapping = _mappingsByFolder.get(folder, new Producer<FolderMapping>() { @Override public FolderMapping produce() {
			return new FolderMapping(folder, acceptedFileExtensions);
		}});
		return folderMapping.run();
	}

	@Override
	public void stopFolderMapping(final File folder) {
		if (_mappingsByFolder.get(folder) == null) return;
		_mappingsByFolder.remove(folder).stop();
		my(Threads.class).startDaemon("Removing \'" + folder.getName() + "\' folder from FileMap...", new Runnable() { @Override public void run() {
			my(FileMap.class).remove(folder);
		}});
	}

	private class FolderMapping {

		private final File _folder;
		private final String[] _acceptedFileExtensions;

		private final AtomicBoolean _stop = new AtomicBoolean(false);
		private final Latch _isFinished = my(Latches.class).produce();

		private FolderMapping(File folder, String... acceptedFileExtensions) {
			if (!folder.isDirectory())
				throw new IllegalArgumentException("Parameter 'folder' must be a directory");
			_folder = folder;
			_acceptedFileExtensions = acceptedFileExtensions;
		}

		Sneer1024 run() throws MappingStopped {
			return my(CpuThrottle.class).limitMaxCpuUsage(15, new ProducerWithThrowable<Sneer1024, MappingStopped>() { @Override public Sneer1024 produce() throws MappingStopped {
				Sneer1024 result = mapFolder(_folder, _acceptedFileExtensions);
				finish();
				return result;
			}});
		}

		private void finish() {
			_mappingsByFolder.remove(_folder);
			_isFinished.open();
		}

		void stop() {
			_stop.set(true);
		}

		private Sneer1024 mapFolder(File folder, String... acceptedFileExtensions) throws MappingStopped {
			FolderContents contents = new FolderContents(immutable(mapFolderEntries(folder, acceptedFileExtensions)));
			Sneer1024 hash = my(FolderContentsHasher.class).hash(contents);
			my(FileMap.class).putFolderContents(folder, contents, hash);
			return hash;
		}

		private List<FileOrFolder> mapFolderEntries(File folder, String... acceptedExtensions) throws MappingStopped{
			List<FileOrFolder> folderEntries = new ArrayList<FileOrFolder>();
			for (File fileOrFolder : sortedFiles(folder, acceptedExtensions)) {
				if (_stop.get()) throw new MappingStopped();
				try {
					folderEntries.add(mapFolderEntry(fileOrFolder, acceptedExtensions));
				} catch (IOException e) {
					my(Logger.class).log("Error mapping: ", fileOrFolder);
				}
			}
			return folderEntries;
		}

		private FileOrFolder mapFolderEntry(File fileOrFolder, String... acceptedExtensions) throws IOException, MappingStopped {
			Sneer1024 hash = (fileOrFolder.isDirectory()) ? mapFolder(fileOrFolder, acceptedExtensions) : mapFile(fileOrFolder);
			return new FileOrFolder(fileOrFolder.getName(), fileOrFolder.lastModified(), hash, fileOrFolder.isDirectory());
		}

		private File[] sortedFiles(File folder, final String... acceptedExtensions) {
			File[] result = folder.listFiles(my(IO.class).fileFilters().extensions(acceptedExtensions));
			if (result == null)	return new File[0];
			Arrays.sort(result, new Comparator<File>() { @Override public int compare(File file1, File file2) {
				return file1.getName().compareTo(file2.getName());
			}});
			return result;
		}

		private ImmutableArray<FileOrFolder> immutable(List<FileOrFolder> entries) {
			return my(ImmutableArrays.class).newImmutableArray(entries);
		}

	}

}
