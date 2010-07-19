package sneer.bricks.softwaresharing.demolisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;

import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.foundation.lang.arrays.ImmutableArray;

class BrickFilter {

	private static final FileMap FileMap = my(FileMap.class);
	

	static FolderContents retrieveOnlyFilesFromThisBrick(Hash hashOfPackage) {
		List<FileOrFolder> result = new ArrayList<FileOrFolder>();
		ImmutableArray<FileOrFolder> packageContents = packageContents(hashOfPackage).contents;
		for (FileOrFolder candidate : packageContents)
			if (isPartOfBrick(candidate))
				result.add(candidate);
		
		return asTuple(result);
	}


	private static boolean isPartOfBrick(FileOrFolder candidate) {
		if (!isFolder(candidate)) return true;
		if (candidate.name.equals("impl")) return true;
		if (candidate.name.equals("tests")) return true;
		return false;
	}


	private static boolean isFolder(FileOrFolder candidate) {
		if (FileMap.getFolderContents(candidate.hashOfContents) != null) return true;
		if (FileMap.getFile(candidate.hashOfContents) != null) return false;
		throw new IllegalStateException("Unable to find FileMap entry for: " + candidate);
	}


	private static FolderContents asTuple(List<FileOrFolder> result) {
		return new FolderContents(new ImmutableArray<FileOrFolder>(result));
	}


	private static FolderContents packageContents(Hash hashOfPackage) {
		return FileMap.getFolderContents(hashOfPackage);
	}

}
