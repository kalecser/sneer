package sneer.bricks.expression.files.tests;

import static basis.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.reactive.collections.CollectionChange;
import sneer.bricks.software.code.classutils.ClassUtils;
import basis.lang.Consumer;

/** Abstract test class names must not end in "Test" or else Hudson will try to instantiate them and fail. :P */

public abstract class FileCopyTestBase extends BrickTestWithTuples {

	protected final FileMapper _fileMapper = my(FileMapper.class);

	@Test (timeout = 3000)
	public void testWithZeroLengthFile() throws Exception {
		testWith(zeroLengthFile());
	}
	
	@Test (timeout = 4000)
	public void testWithSmallFile() throws Exception {
		testWith(anySmallFile());
	}

	@Test (timeout = 6000)
	public void testWithFolder() throws Exception {
		testWith(folderWithAFewFiles());
	}

	@Test (timeout = 7000)
	public void testWithLargeFile() throws Exception {
		File largefile = newTmpFile();
		writePseudoRandomBytesTo(largefile, 1000000);
		testWith(largefile);
	}

	protected void writePseudoRandomBytesTo(File file, int size) throws IOException {
		my(IO.class).files().writeByteArrayToFile(file, pseudoRandomBytes(size));
	}
	
	private byte[] pseudoRandomBytes(int size) {
		byte[] result = new byte[size];
		new Random(0).nextBytes(result);
		return result;
	}

	protected void testWith(File fileOrFolder) throws Exception {
		@SuppressWarnings("unused")	WeakContract refToAvoidGc =
			my(BlinkingLights.class).lights().addReceiver(new Consumer<CollectionChange<Light>>(){@Override public void consume(CollectionChange<Light> deltas) {
				if (!deltas.elementsAdded().isEmpty()) {
					Throwable error = deltas.elementsAdded().iterator().next().error();
					if (error != null) error.printStackTrace();
				}
			}});

		File copy = newTmpFile();
		Hash hash = hash(fileOrFolder);
		if (fileOrFolder.isDirectory())
			copyFolderFromFileMap(hash, copy);
		else
			copyFileFromFileMap(hash, fileOrFolder.length(), fileOrFolder.lastModified(), copy);

		assertNotNull(hash);

		assertSameContents(fileOrFolder, copy);
	}

	protected Hash hash(File fileOrFolder) throws MappingStopped, IOException {
		return _fileMapper.mapFileOrFolder(fileOrFolder);
	}

	abstract protected void copyFileFromFileMap(Hash hashOfContents, long size, long lastModified, File destination) throws Exception;

	abstract protected void copyFolderFromFileMap(Hash hashOfContents, File destination) throws Exception;

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