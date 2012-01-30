/**
 * 
 */
package sneer.bricks.expression.files.map.mapper.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
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
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardware.io.log.stacktrace.StackTraceLogger;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.arrays.ImmutableArray;

class MapperWorker {

	private final static FileMap FileMap = my(FileMap.class);


	private final File _fileOrFolder;
	private final FileFilter _extensionsFilter;

	private Hash _result;
	private Exception _exception;

	private final AtomicBoolean _stop = new AtomicBoolean(false);


	MapperWorker(File fileOrFolder, String... acceptedFileExtensions) {
		_fileOrFolder = fileOrFolder;
		_extensionsFilter = filterFor(acceptedFileExtensions);
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


	void stop() {
		_stop.set(true);
	}


	private Hash mapFolder(File folder) throws MappingStopped, IOException {
		String folderPath = folder.getAbsolutePath();
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


	private void checkPutHasWorked(File folder, FolderContents expected, Hash hash) {
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
			//if (!newEntries.contains(oldEntry))
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


	private Hash putFolderHash(File folder, FolderContents newContents) {
		Hash hash = my(FolderContentsHasher.class).hash(newContents);
		try {
			FileMap.putFolder(folder.getAbsolutePath(), hash);
		} catch (RuntimeException e) {
			throw withDetails(folder,	newContents, e);
		}
		return hash;
	}


	private List<FileOrFolder> mapFolderEntries(File folder) throws MappingStopped, IOException {
		List<FileOrFolder> result = new ArrayList<FileOrFolder>();
		for (File entry : sortedFiles(folder))
			result.add(mapFolderEntry(entry));
		return result;
	}


	private FileOrFolder mapFolderEntry(File fileOrFolder) throws IOException, MappingStopped {
		if (_stop.get()) throw new MappingStopped();

		String name = fileOrFolder.getName();
		
		if (fileOrFolder.isDirectory()) {
			Hash hash = mapFolder(fileOrFolder);
			return new FileOrFolder(name, hash);
		}
		
		Hash hash = mapFile(fileOrFolder);
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
	
	
	static private FileFilter filterFor(String... acceptedFileExtensions) {
		return acceptedFileExtensions.length > 0
		? my(IO.class).fileFilters().foldersAndExtensions(acceptedFileExtensions)
				: my(IO.class).fileFilters().any();
	}

	
	static private IllegalStateException withDetails(File folder,	FolderContents contents, RuntimeException e) {
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

}



