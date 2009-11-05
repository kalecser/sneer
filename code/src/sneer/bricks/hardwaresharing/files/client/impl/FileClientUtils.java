package sneer.bricks.hardwaresharing.files.client.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.crypto.Sneer1024;

class FileClientUtils {

	static Object mappedContentsBy(Sneer1024 hashOfContents) {
		File file = my(FileMap.class).getFile(hashOfContents);
		return file == null
			?  my(FileMap.class).getFolder(hashOfContents)
			: file;
	}
	
}
