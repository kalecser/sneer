package sneer.bricks.hardwaresharing.files.map;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMap {

//	FileMapper
//		startMappingFolder(File folder) - Call FileMap.put... recursively for files and folders.
//		stopMappingFolder(File folder) - Abort mapping and call FileMap.removeTree().

//	FileMap	
//		void put(File file, Sneer1024 hash)
//		void putFolderContents(File folder, FolderContents contents, hash)
//		void removeTree(File fileOrFolder)
	
//		File getFile(Sneer1024 hash);
//		FolderContents getFolder(Sneer1024 hash);
	
	
	Sneer1024 put(File fileOrFolder, String... acceptedFileExtensions) throws IOException;
	Sneer1024 put(File fileOrFolder, AtomicBoolean stopOperation, String... acceptedFileExtensions) throws IOException;
	Sneer1024 putFolderContents(FolderContents contents);
	
	File getFile(Sneer1024 hash);
	FolderContents getFolder(Sneer1024 hash);

}
