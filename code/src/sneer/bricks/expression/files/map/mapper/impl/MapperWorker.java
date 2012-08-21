package sneer.bricks.expression.files.map.mapper.impl;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import sneer.bricks.expression.files.hasher.FolderContentsHasher;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.threads.throttle.CpuThrottle;
import sneer.bricks.hardware.io.log.stacktrace.StackTraceLogger;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import basis.lang.Closure;
import basis.lang.arrays.ImmutableArray;
import basis.lang.exceptions.NotImplementedYet;

class MapperWorker {

	private final static FileMap FileMap = my(FileMap.class);


	private final Path _fileOrFolder;
	private final PathMatcher extensionsMatcher;

	private Hash _result;
	private Exception _exception;

	private final AtomicBoolean _stop = new AtomicBoolean(false);


	MapperWorker(Path fileOrFolder, String... acceptedFileExtensions) {
		_fileOrFolder = fileOrFolder;
		extensionsMatcher = matcherFor(acceptedFileExtensions);
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
				_result = Files.isDirectory(_fileOrFolder)
					? mapFolder(_fileOrFolder)
					: mapFile(_fileOrFolder);
			} catch (Exception e) {
				_exception = e;
			}
		}});
	}


	void stop() {
		_stop.set(true);
	}


	private Hash mapFolder(Path folder) throws MappingStopped, IOException {
		String folderPath = absolute(folder);
		Hash mappedHash = FileMap.getHash(folderPath);
		FolderContents mappedContents = null;
		if (mappedHash != null)
			mappedContents = FileMap.getFolderContents(mappedHash);

		List<FileOrFolder> newEntries = mapFolderEntries(folder);
		FolderContents newContents = new FolderContents(immutable(newEntries));

		if (mappedContents != null)
			if (areEqualContents(newEntries, mappedContents.contents))
				return mappedHash;
		
		unmapDeletedFiles(folderPath, newEntries, mappedContents);
		
		Hash result = putFolderHash(folder, newContents);
		checkPutHasWorked(folder, newContents, result);
		return result;
	}


	private boolean areEqualContents(Collection<?> a, Collection<?> b) {
		if (a.size() != b.size()) return false;
		Iterator<?> itA = a.iterator();
		Iterator<?> itB = b.iterator();
		while (itA.hasNext())
			if (!itA.next().equals(itB.next()))
				return false;
		return true;
	}


	private void checkPutHasWorked(Path folder, FolderContents expected, Hash hash) {
		FolderContents actual = FileMap.getFolderContents(hash);
		if (actual != null && actual.contents.size() == expected.contents.size()) return;

		my(BlinkingLights.class).turnOn(LightType.ERROR, "" + folder + " not mapped correctly. Expected: " + entries(expected) + " Actual: " + entries(actual), "Contact a sneer expert!");
		my(StackTraceLogger.class).logStackTrace();
	}

	private String entries(FolderContents contents) {
		if (contents == null) return "null";
		if (contents.contents.isEmpty()) return "(empty)";
		String result = "";
		for (FileOrFolder entry : contents.contents)
			result += entry.name + ", ";
		return result;
	}


	private void unmapDeletedFiles(String folderPath, List<FileOrFolder> newEntries, FolderContents oldContents) {
		if (oldContents == null) return;
		for (FileOrFolder oldEntry : oldContents.contents)
			if (!containsFileName(newEntries, oldEntry))
				unmap(folderPath, oldEntry);
	}


	private boolean containsFileName(List<FileOrFolder> newEntries, FileOrFolder oldEntry) {
		for (FileOrFolder newEntry : newEntries)
			if (newEntry.name.equals(oldEntry.name))
				return true;
		return false;
	}


	private void unmap(String folderPath, FileOrFolder oldEntry) {
		FileMap.remove(folderPath + "/" + oldEntry.name);
	}


	private Hash putFolderHash(Path folder, FolderContents newContents) {
		Hash hash = my(FolderContentsHasher.class).hash(newContents);
		try {
			FileMap.putFolder(absolute(folder), hash);
		} catch (RuntimeException e) {
			throw withDetails(folder,	newContents, e);
		}
		return hash;
	}


	private List<FileOrFolder> mapFolderEntries(Path folder) throws MappingStopped, IOException {
		List<FileOrFolder> result = new ArrayList<FileOrFolder>();
		for (Path entry : sortedEntries(folder))
			result.add(mapFolderEntry(entry));
		return result;
	}


	private FileOrFolder mapFolderEntry(Path fileOrFolder) throws IOException, MappingStopped {
		if (_stop.get()) throw new MappingStopped();

		String name = fileOrFolder.getFileName().toString();
		
		if (Files.isDirectory(fileOrFolder)) {
			Hash hash = mapFolder(fileOrFolder);
			return new FileOrFolder(name, hash);
		}
		
		Hash hash = mapFile(fileOrFolder);
		return new FileOrFolder(name, Files.size(fileOrFolder), lastModified(fileOrFolder), hash);
	}


	private Hash mapFile(Path file) throws IOException {
		String path = absolute(file);
		long size = Files.size(file);
		long lastModified = lastModified(file);

		Hash cached = FileMap.getHash(path);
		if (cached != null && lastModified == FileMap.getLastModified(path))
			return cached;

		Hash result;
		try {
			result = my(Crypto.class).digest(file.toFile());
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "File Mapping Error", "This can happen if your file has weird characters in the name or if your disk is failing.", e);
			return my(Crypto.class).digest(new byte[0]);
		}
		FileMap.putFile(path, size, lastModified, result);
		return result;
	}

	
	private Path[] sortedEntries(Path folder) throws IOException {
		ArrayList<Path> list = new ArrayList<Path>();
		try (DirectoryStream<Path> entries = Files.newDirectoryStream(folder)) {
		for (Path path : entries) 
			if (Files.isDirectory(path) || extensionsMatcher.matches(path.getFileName()))
				list.add(path);
		}
		
		Path[] result = list.toArray(new Path[0]);
		Arrays.sort(result, new Comparator<Path>() { @Override public int compare(Path path1, Path path2) {
			return path1.getFileName().compareTo(path2.getFileName());
		}});
		return result;
	}
	
	
	static private PathMatcher matcherFor(String... fileExtensions) {
		if(fileExtensions.length > 1) 
			throw new NotImplementedYet();
		
		String glob = fileExtensions.length == 0 ? "*"	: "*."+fileExtensions[0];
		return FileSystems.getDefault().getPathMatcher("glob:"+glob);
	}

	
	static private IllegalStateException withDetails(Path folder,	FolderContents contents, RuntimeException e) {
		String entries = "";
		for (FileOrFolder entry : contents.contents)
			entries += "\n" + entry.toString();
		return new IllegalStateException("Exception trying to map folder: " + folder + " entries: " + entries, e);
	}


	static private void throwNarrowed(Exception e) throws MappingStopped, IOException {
		if (e instanceof MappingStopped) throw (MappingStopped) e;
		if (e instanceof IOException) throw (IOException) e;
		throw new IllegalStateException(e);
	}
	
	
	static private ImmutableArray<FileOrFolder> immutable(List<FileOrFolder> entries) {
		return new ImmutableArray<FileOrFolder>(entries);
	}

	
	static private String absolute(Path path) {
		return path.toAbsolutePath().toString();
	}
	
	
	static private long lastModified(Path file) throws IOException {
		return Files.getLastModifiedTime(file).toMillis();
	}
}



