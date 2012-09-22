package sneer.bricks.expression.files.transfer;

import java.io.File;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.identity.seals.Seal;
import basis.brickness.Brick;
import basis.lang.Consumer;

@Brick
public interface FileTransfer {

	void tryToSend(File file, Seal peer);

	WeakContract registerSugestionHandler(Consumer<FileTransferSugestion> sugestionHandler);
	void accept(FileTransferSugestion sugestion);
	
	WeakContract registerStatusHandler(Consumer<FileTransferStatus> statusHandler);


}
