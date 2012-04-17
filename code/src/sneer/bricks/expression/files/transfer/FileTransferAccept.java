package sneer.bricks.expression.files.transfer;

import sneer.bricks.expression.tuples.Tuple;

public class FileTransferAccept extends Tuple {

	public final FileTransferSugestion sugestion;

	public FileTransferAccept(FileTransferSugestion sugestion) {
		this.sugestion = sugestion;
	}

}
