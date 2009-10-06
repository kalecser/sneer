package sneer.bricks.hardwaresharing.files.map.tests;

import static sneer.foundation.environments.Environments.my;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.ram.arrays.ImmutableArray;
import sneer.bricks.hardware.ram.arrays.ImmutableArrays;
import sneer.bricks.hardwaresharing.files.map.FileMap;
import sneer.bricks.hardwaresharing.files.protocol.FileOrFolder;
import sneer.bricks.hardwaresharing.files.protocol.FolderContents;
import sneer.bricks.pulp.crypto.Crypto;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.lang.Consumer;

public class FileMapTest extends BrickTest {

	private final FileMap _subject = my(FileMap.class);

	@Ignore
	@Test (timeout = 3000)
	public void cacheFolderContents() {
		final Latch latch = my(Latches.class).produce();
		
		@SuppressWarnings("unused")	WeakContract contract =
			_subject.contentsAdded().addReceiver(new Consumer<Object>() { @Override public void consume(Object contents) {
				latch.open();
			}});
		
		FolderContents folderContents = new FolderContents(immutable(Arrays.asList(new FileOrFolder[]{
			new FileOrFolder("readme.txt", anyReasonableDate(), hash(1)),
			new FileOrFolder("src", anyReasonableDate(), hash(2)),
			new FileOrFolder("docs", anyReasonableDate(), hash(3))
		})));

		Sneer1024 hash = _subject.putFolderContents(folderContents);
		
		latch.waitTillOpen();
		assertEquals(folderContents, _subject.getContents(hash));
	}

	private ImmutableArray<FileOrFolder> immutable(Collection<FileOrFolder> entries) {
		return my(ImmutableArrays.class).newImmutableArray(entries);
	}

	private Sneer1024 hash(int i) {
		return my(Crypto.class).digest(new byte[]{(byte)i});
	}

	private long anyReasonableDate() {
		return System.currentTimeMillis();
	}

}