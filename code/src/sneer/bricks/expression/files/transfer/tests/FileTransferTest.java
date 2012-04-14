package sneer.bricks.expression.files.transfer.tests;

import static basis.environments.Environments.my;
import static basis.environments.Environments.runWith;

import java.io.File;
import java.io.IOException;

import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.expression.files.client.FileClient;
import sneer.bricks.expression.files.transfer.FileTransfer;
import sneer.bricks.expression.files.transfer.FileTransferSugestion;
import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
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
	@Test
	@Ignore
	public void singleFileTransfer() throws IOException {
		
		final Latch latch = new Latch();
		checking(new Expectations(){{
			oneOf(_fileClient).startFileDownload(null, -42, null, null);will(new CustomAction("") {  @Override public Object invoke(Invocation invocation) throws Throwable {
				latch.open();
				return null;
			}});
		}});
		_ref = _subject.registerHandler(new Consumer<FileTransferSugestion>(){  @Override public void consume(FileTransferSugestion sugestion) {
			_subject.accept(sugestion);
		}});
		
		final Seal ownSeal = my(OwnSeal.class).get().currentValue();
		runWith(remote(), new ClosureX<IOException>() { @Override public void run() throws IOException {
			File file = createTmpFileWithFileNameAsContent("banana");
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
