package sneer.bricks.expression.files.transfer.tests;

import static basis.environments.Environments.my;
import static basis.environments.Environments.runWith;

import java.io.File;
import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Test;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.transfer.FileTransfer;
import sneer.bricks.expression.files.transfer.FileTransferSugestion;
import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.hardware.cpu.crypto.Crypto;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.software.folderconfig.FolderConfig;
import basis.brickness.testsupport.Bind;
import basis.lang.ClosureX;
import basis.lang.Consumer;
import basis.util.concurrent.Latch;

public class FileTransferTest extends BrickTestWithTuples {

	private final FileTransfer _subject = my(FileTransfer.class);
	@Bind private final FileClient _fileClient = mock(FileClient.class);
	
	@SuppressWarnings("unused")	private WeakContract _ref;
	

	/*
	 * - Send directory
	 * - Choose download directory
	 * - Ignore invalid accept
	 */
	@Test (timeout=2000)
	public void singleFileTransfer() throws IOException {
		
		final File file = createTmpFileWithFileNameAsContent("banana");
		final long lastModified = file.lastModified();
		final Hash hash = my(Crypto.class).digest(file);

		_ref = _subject.registerHandler(new Consumer<FileTransferSugestion>(){  @Override public void consume(FileTransferSugestion sugestion) {
			_subject.accept(sugestion);
		}});
		
		final Latch latch = new Latch();
		checking(new Expectations(){{
			oneOf(_fileClient).startFileDownload(new File(my(FolderConfig.class).tmpFolder().get(), "banana"), lastModified, hash, remoteSeal());will(new CustomAction("") {  @Override public Object invoke(Invocation invocation) throws Throwable {
				latch.open();
				return null;
			}});
		}});
		
		final Seal ownSeal = my(OwnSeal.class).get().currentValue();
		runWith(remote(), new ClosureX<IOException>() { @Override public void run() {
			my(FileTransfer.class).tryToSend(file, ownSeal);
		}});
		
		latch.waitTillOpen();
	}

	
	@Test
	public void ignoredTransfer() {
		Seal peer = new Seal(new byte[]{42});	
		File file = new File("tmp");
		_subject.tryToSend(file, peer);
	}
	
	
}
