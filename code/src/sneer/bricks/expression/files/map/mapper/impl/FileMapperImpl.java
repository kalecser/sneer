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
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.foundation.lang.CacheMap;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;
import sneer.foundation.lang.arrays.ImmutableArray;

class FileMapperImpl implements FileMapper {

	private final static FileMap FileMap = my(FileMap.class);

	private final CacheMap<File, FolderMapping> _mappingsByFolder = CacheMap.newInstance();


	@Override
	public Hash mapFile(File file) {
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
		FileMap.putFile(path, file.lastModified(), result);
		return result;
	}


	@Override
	public Hash mapFolder(final File folder, final String... acceptedFileExtensions) throws MappingStopped, IOException {
		Hash hash = FileMap.getHash(folder.getAbsolutePath());
		return (hash != null)
			? hash
			: mappingFor(folder, acceptedFileExtensions).result();
	}


	private FolderMapping mappingFor(final File folder, final String... acceptedFileExtensions) {
		return _mappingsByFolder.get(folder, new Producer<FolderMapping>() { @Override public FolderMapping produce() {
			return new FolderMapping(folder, acceptedFileExtensions);
		}});
	}


	@Override
	public void stopFolderMapping(final File folder) {
		if (_mappingsByFolder.get(folder) == null) return;
		final FolderMapping folderMapping = _mappingsByFolder.remove(folder);
		folderMapping.stop();
	}


	private class FolderMapping {

		private final File _folder;
		private final String[] _acceptedFileExtensions;

		private Hash _result;
		private Exception _exception;

		private final AtomicBoolean _stop = new AtomicBoolean(false);


		private FolderMapping(File folder, String... acceptedFileExtensions) {
			if (!folder.isDirectory())
				throw new IllegalArgumentException("Parameter 'folder' must be a directory");

			_folder = folder;
			_acceptedFileExtensions = acceptedFileExtensions;
		}


		private void run() {
			my(CpuThrottle.class).limitMaxCpuUsage(20, new Closure() { @Override public void run() {
				try {
					_result = mapFolder(_folder, _acceptedFileExtensions);
				} catch (Exception e) {
					_exception = e;
				} finally {
					_mappingsByFolder.remove(_folder);					
				}
			}});
		}


		synchronized
		Hash result() throws MappingStopped, IOException {
			if (_result == null && _exception == null) run();
			if (_exception != null) throwNarrowed(_exception);
			return _result;
		}


		private void throwNarrowed(Exception e) throws MappingStopped, IOException {
			if (e instanceof MappingStopped) throw (MappingStopped) e;
			if (e instanceof IOException) throw (IOException) e;
			throw new IllegalStateException(e);
		}


		void stop() {
			_stop.set(true);
		}


		private Hash mapFolder(File folder, String... acceptedFileExtensions) throws MappingStopped, IOException {
			FolderContents contents = new FolderContents(immutable(mapFolderEntries(folder, acceptedFileExtensions)));
			Hash hash = my(FolderContentsHasher.class).hash(contents);
			FileMap.putFolder(folder.getAbsolutePath(), hash);
			return hash;
		}


		private List<FileOrFolder> mapFolderEntries(File folder, String... acceptedExtensions) throws MappingStopped, IOException{
			List<FileOrFolder> result = new ArrayList<FileOrFolder>();
			for (File entry : sortedFiles(folder, acceptedExtensions)) {
				if (_stop.get()) throw new MappingStopped();
				result.add(mapFolderEntry(entry, acceptedExtensions));
			}
			return result;
		}


		private FileOrFolder mapFolderEntry(File fileOrFolder, String... acceptedExtensions) throws IOException, MappingStopped {
			Hash hash;
			String name = fileOrFolder.getName();
			
			if (fileOrFolder.isDirectory()) {
				hash = mapFolder(fileOrFolder, acceptedExtensions);
				return new FileOrFolder(name, hash);
			}
			
			hash = mapFile(fileOrFolder);
			return new FileOrFolder(name, fileOrFolder.lastModified(), hash);
		}


		private File[] sortedFiles(File folder, final String... acceptedExtensions) {
			File[] result = listFiles(folder, acceptedExtensions);
			if (result == null)	return new File[0];
			Arrays.sort(result, new Comparator<File>() { @Override public int compare(File file1, File file2) {
				return file1.getName().compareTo(file2.getName());
			}});
			return result;
		}


		private File[] listFiles(File folder, final String... acceptedExtensions) {
			return acceptedExtensions.length > 0
					? folder.listFiles(my(IO.class).fileFilters().foldersAndExtensions(acceptedExtensions))
					: folder.listFiles();
		}


		private ImmutableArray<FileOrFolder> immutable(List<FileOrFolder> entries) {
			return new ImmutableArray<FileOrFolder>(entries);
		}

	}

}
