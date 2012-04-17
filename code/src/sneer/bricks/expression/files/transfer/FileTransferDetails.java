package sneer.bricks.expression.files.transfer;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.cpu.crypto.Hash;

public class FileTransferDetails extends Tuple {

	public final FileTransferAccept accept;
	public final Hash hash;

	public FileTransferDetails(FileTransferAccept accept, Hash hash) {
		this.accept = accept;
		this.hash = hash;
	}

}
