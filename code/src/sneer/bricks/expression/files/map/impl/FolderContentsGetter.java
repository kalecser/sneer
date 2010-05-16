package sneer.bricks.expression.files.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.Lang;
import sneer.bricks.hardware.cpu.lang.Lang.Strings;
import sneer.foundation.lang.arrays.ImmutableArray;


class FolderContentsGetter {

	private static final Strings Strings = my(Lang.class).strings();

	private final FileMapData _data;
	private final String _folder;


	FolderContentsGetter(FileMapData data, String path) {
		_data = data;
		_folder = path + "/";
	}


	FolderContents result() {
		List<FileOrFolder> contents = new ArrayList<FileOrFolder>();
		for (String candidate : _data.allPaths())
			accumulateDirectChildren(candidate, _folder, contents);
		
		Collections.sort(contents, new Comparator<FileOrFolder>() { @Override public int compare(FileOrFolder f1, FileOrFolder f2) {
			return f1.name.compareTo(f2.name);
		}});
		
		return new FolderContents(new ImmutableArray<FileOrFolder>(contents));
	}

	
	private void accumulateDirectChildren(String candidate, String folder, List<FileOrFolder> result) {
		if (!candidate.startsWith(folder)) return;
		
		String name = Strings.removeStart(candidate, folder);
		if (name.indexOf('/') != -1) return; //Not a direct child.
		
		Hash hash = _data.getHash(candidate);
		long lastModified = _data.getLastModified(candidate);
		result.add(lastModified == -1
			? new FileOrFolder(name, hash)
			: new FileOrFolder(name, lastModified, hash) 
		);
	}

}
