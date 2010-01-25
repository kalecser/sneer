package sneer.bricks.softwaresharing.demolisher.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sneer.bricks.expression.files.hasher.FolderContentsHasher;
import sneer.bricks.expression.files.map.FileMap;
import sneer.bricks.expression.files.protocol.FileOrFolder;
import sneer.bricks.expression.files.protocol.FolderContents;
import sneer.bricks.hardware.cpu.codecs.crypto.Sneer1024;
import sneer.bricks.hardware.ram.arrays.ImmutableArray;

class BrickFilter {

	private static final FileMap FileMap = my(FileMap.class);
	

	static Sneer1024 mapOnlyFilesFromThisBrick(Sneer1024 hashOfPackage) {
		FolderContents packageContents = packageContents(hashOfPackage);
		FolderContents brickContents = filterOtherBricksOutOf(packageContents);
		
		Sneer1024 result = brickContents.contents.length() == packageContents.contents.length()
			? hashOfPackage
			: my(FolderContentsHasher.class).hash(brickContents);
		
		FileMap.putFolderContents(new File("BogusFileBecauseBrickMappingRemovalIsNotImplementedYet"), brickContents, result);

		return result;
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
		if (FileMap.getFolderContents(candidate.hashOfContents) != null) return true;
		if (FileMap.getFile(candidate.hashOfContents) != null) return false;
		throw new IllegalStateException("Unable to find FileMap entry for: " + candidate);
	}


	private static FolderContents asTuple(List<FileOrFolder> result) {
		return new FolderContents(new ImmutableArray<FileOrFolder>(result));
	}


	private static FolderContents packageContents(Sneer1024 hashOfPackage) {
		return FileMap.getFolderContents(hashOfPackage);
	}

}
