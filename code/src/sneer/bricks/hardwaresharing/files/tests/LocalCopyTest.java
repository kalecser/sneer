package sneer.bricks.hardwaresharing.files.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.writer.FileWriter;
import sneer.bricks.pulp.crypto.Sneer1024;

public class LocalCopyTest extends FileCopyTestBase {

	@Override
	protected void copyFromFileMap(Sneer1024 hashOfContents, File destination) throws IOException {
		File file = my(FileMap.class).getFile(hashOfContents);
		my(FileWriter.class).writeAtomicallyTo(destination, anyReasonableDate(), bytes(file));
	}


	private byte[] bytes(File file) throws IOException {
		return my(IO.class).files().readBytes(file);
	}

	
	private long anyReasonableDate() {
		return System.currentTimeMillis();
	}
}
