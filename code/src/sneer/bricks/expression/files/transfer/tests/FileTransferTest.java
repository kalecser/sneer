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
import sneer.bricks.expression.files.client.downloads.Download;
import sneer.bricks.expression.files.map.mapper.FileMapper;
import sneer.bricks.expression.files.map.mapper.MappingStopped;
import sneer.bricks.expression.files.transfer.FileTransfer;
import sneer.bricks.expression.files.transfer.FileTransferSugestion;
import sneer.bricks.expression.files.transfer.downloadfolder.DownloadFolder;
import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.hardware.cpu.crypto.Hash;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Signals;
import basis.brickness.testsupport.Bind;
import basis.lang.Closure;
import basis.lang.ClosureX;
import basis.lang.Consumer;
import basis.util.concurrent.Latch;

public class FileTransferTest extends BrickTestWithTuples {

	private final FileTransfer subject = my(FileTransfer.class);
	@Bind private final FileClient fileClient = mock(FileClient.class);
	private final Download download = mock(Download.class);
	
	@SuppressWarnings("unused")	private WeakContract ref;
	

	@Test (timeout=4000)
	public void singleFileTransfer() throws IOException, MappingStopped {
		File file = createTmpFileWithFileNameAsContent("banana");
		transfer(file);
	}

	
	@Test (timeout=4000)
	public void folderTransfer() throws IOException, MappingStopped {
		File file = createTmpFileWithFileNameAsContent("folder/banana");
		File folder = file.getParentFile();
		
		transfer(folder);
	}


	private void transfer(final File fileOrFolder) throws MappingStopped, IOException {
		final String fileOrFolderName = fileOrFolder.getName();
		final boolean isFolder = fileOrFolder.isDirectory();
		final long size = fileOrFolder.length();
		final long lastModified = fileOrFolder.lastModified();
		final Hash hash = my(FileMapper.class).mapFileOrFolder(fileOrFolder);

		ref = subject.registerHandler(new Consumer<FileTransferSugestion>(){  @Override public void consume(FileTransferSugestion sugestion) {
			subject.accept(sugestion);
		}});
		
		final String downloadFolder = tmpFolderName();
		final Latch latch = new Latch();
		checking(new Expectations(){{
			oneOf(fileClient).startDownload(
				new File(downloadFolder, fileOrFolderName), isFolder, size, lastModified, hash, remoteSeal());
				will(new CustomAction("") {  @Override public Object invoke(Invocation invocation) throws Throwable {
					latch.open();
					return download;
				}}
			);
			allowing(download).onFinished(with(any(Closure.class)));
			allowing(download).progress();will(returnValue(my(Signals.class).constant(100)));
		}});
		
		setDownloadFolder(downloadFolder);
		final Seal ownSeal = my(OwnSeal.class).get().currentValue();
		runWith(remote(), new ClosureX<IOException>() { @Override public void run() {
			my(FileTransfer.class).tryToSend(fileOrFolder, ownSeal);
		}});
		
		latch.waitTillOpen();
	}


	private void setDownloadFolder(String path) {
		my(Attributes.class).myAttributeSetter(DownloadFolder.class).consume(path );
	}

	
	@Test
	public void ignoredTransfer() {
		Seal peer = new Seal(new byte[]{42});	
		File file = new File("tmp");
		subject.tryToSend(file, peer);
	}
	
	
}
