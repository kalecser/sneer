package sneer.bricks.softwaresharing.demolisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;

import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.pulp.crypto.Sneer1024;

class BrickFilter {

	private static final FileMap FileMap = my(FileMap.class);
	

	static Sneer1024 cacheOnlyFilesFromThisBrick(Sneer1024 hashOfPackage) {
		FolderContents packageContents = packageContents(hashOfPackage);
		FolderContents brickContents = filterOtherBricksOutOf(packageContents);
		return brickContents.contents.length() == packageContents.contents.length()
			? hashOfPackage
			: FileMap.putFolderContents(brickContents);
	}


	private static FolderContents filterOtherBricksOutOf(FolderContents packageContents) {
		List<FileOrFolder> result = new ArrayList<FileOrFolder>();
		for (FileOrFolder candidate : packageContents.contents)
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
		if (FileMap.getFolder(candidate.hashOfContents) != null) return true;
		if (FileMap.getFile(candidate.hashOfContents) != null) return false;
		throw new IllegalStateException("Unable to find FileMap entry for: " + candidate);
	}


	private static FolderContents asTuple(List<FileOrFolder> result) {
		return new FolderContents(my(ImmutableArrays.class).newImmutableArray(result));
	}


	private static FolderContents packageContents(Sneer1024 hashOfPackage) {
		return FileMap.getFolder(hashOfPackage);
	}

}
