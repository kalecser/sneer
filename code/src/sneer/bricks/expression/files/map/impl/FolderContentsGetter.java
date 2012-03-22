package sneer.bricks.expression.files.map.impl;

import static basis.environments.Environments.my;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import basis.lang.arrays.ImmutableArray;

import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.Lang.Strings;


class FolderContentsGetter {

	private static final Strings Strings = my(Lang.class).strings();

	private final FileMapData _data;
	private final Hash _hash;


	FolderContentsGetter(FileMapData data, Hash hash) {
		_data = data;
		_hash = hash;
	}


	FolderContents result() {
		
		List<String> folders = _data.getFolders(_hash);
		if (folders.isEmpty())
			return null;
		
		SortedSet<FileOrFolder> contents = new TreeSet<FileOrFolder>(fileOrFolderNameComparator());
		String[] allPaths = _data.allPaths();
		for (String path : folders) {
			String folder = path + "/";			
			for (String candidate : allPaths)
				accumulateDirectChild(candidate, folder, contents);
		}
		return new FolderContents(new ImmutableArray<FileOrFolder>(contents));
	}


	private Comparator<FileOrFolder> fileOrFolderNameComparator() {
		return new Comparator<FileOrFolder>() { @Override public int compare(FileOrFolder f1, FileOrFolder f2) {
			return f1.name.compareTo(f2.name);
		}};
	}

	
	private void accumulateDirectChild(String candidate, String folder, Collection<FileOrFolder> result) {
		accumulateChild(_data, candidate, folder, result, true);
	}


	public static void accumulateChild(FileMapData data, String candidate, String folder, Collection<FileOrFolder> result, boolean directChildrenOnly) {
		
		if (!candidate.startsWith(folder)) return;
		
		String name = Strings.removeStart(candidate, folder);
		if (directChildrenOnly)
			if (name.indexOf('/') != -1) return; //Not a direct child.
		
		Hash hash = data.getHash(candidate);
		long lastModified = data.getLastModified(candidate);
		result.add(lastModified == -1
			? new FileOrFolder(name, hash)
			: new FileOrFolder(name, lastModified, hash) 
		);
	}

}
