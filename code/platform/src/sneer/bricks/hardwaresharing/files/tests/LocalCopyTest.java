package sneer.bricks.hardwaresharing.files.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Ignore;

import sneer.bricks.hardwaresharing.files.writer.FileWriter;
import sneer.bricks.pulp.crypto.Sneer1024;

@Ignore
public class LocalCopyTest extends FileCopyTestBase {

	@Override
	protected void copyFromFileCache(Sneer1024 hashOfContents, File destination) throws IOException {
		my(FileWriter.class).writeAtomicallyTo(destination, anyReasonableDate(), hashOfContents);
	}

	
	private long anyReasonableDate() {
		return System.currentTimeMillis();
	}
}
