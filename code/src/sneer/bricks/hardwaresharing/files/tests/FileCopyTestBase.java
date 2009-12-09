package sneer.bricks.hardwaresharing.files.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.cpu.algorithms.crypto.Sneer1024;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.software.code.classutils.ClassUtils;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Consumer;

/** Abstract test class names must not end in "Test" or else Hudson will try to instantiate them and fail. :P */

public abstract class FileCopyTestBase extends BrickTest {

	protected final FileMap _fileMap = my(FileMap.class);

	@Ignore
	@Test (timeout = 3000)
	public void testWithZeroLengthFile() throws IOException {
		testWith(zeroLengthFile());
	}
	
	@Test (timeout = 4000)
	public void testWithSmallFile() throws IOException {
		testWith(anySmallFile());
	}

	@Test (timeout = 6000)
	public void testWithFolder() throws IOException {
		testWith(folderWithAFewFiles());
	}

	@Test (timeout = 7000)
	public void testWithLargeFile() throws IOException {
		testWith(createLargeFile());
	}

	private File createLargeFile() throws IOException {
		File result = newTmpFile();
		my(IO.class).files().writeByteArrayToFile(result, randomBytes(1000000));
		return result;
	}
	
	private byte[] randomBytes(int size) {
		byte[] result = new byte[size];
		new Random().nextBytes(result);
		return result;
	}

	private void testWith(File fileOrFolder) throws IOException {
		Sneer1024 hash = _fileMap.put(fileOrFolder);
		assertNotNull(hash);

		File copy = newTmpFile();
		
		@SuppressWarnings("unused")	WeakContract refToAvoidGc =
			my(BlinkingLights.class).lights().addReceiver(new Consumer<CollectionChange<Light>>(){@Override public void consume(CollectionChange<Light> deltas) {
				if (!deltas.elementsAdded().isEmpty())
					fail();
			}});
		
		if (fileOrFolder.isFile())
			copyFileFromFileMap(hash, copy);
		else
			copyFolderFromFileMap(hash, copy);

		assertSameContents(fileOrFolder, copy);
	}

	abstract protected void copyFileFromFileMap(Sneer1024 hashOfContents, File destination) throws IOException;

	abstract protected void copyFolderFromFileMap(Sneer1024 hashOfContents, File destination) throws IOException;

	private File zeroLengthFile() throws IOException {
		return createTmpFile("tmp" + System.nanoTime());
	}

	private File anySmallFile() {
		return myClassFile();
	}

	private File myClassFile() {
		return my(ClassUtils.class).classFile(getClass());
	}

	private File folderWithAFewFiles() {
		final File result = new File(myClassFile().getParent(), "fixtures");

		final long now = System.currentTimeMillis();
		setLastModifiedRecursively(new File(result, "directory1"), now);
		setLastModifiedRecursively(new File(result, "directory1copy"), now);

		return result;
	}

	private void setLastModifiedRecursively(File file, long lastModified) {
		if (file.isDirectory())
			for (File child : file.listFiles())
				setLastModifiedRecursively(child, lastModified);

		file.setLastModified(lastModified);
	}

	private void assertSameContents(File file1, File file2) throws IOException {
		my(IO.class).files().assertSameContents(file1, file2);
	}

}