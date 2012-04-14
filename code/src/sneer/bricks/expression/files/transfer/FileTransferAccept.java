package sneer.bricks.expression.files.transfer;

import sneer.bricks.expression.tuples.Tuple;

public class FileTransferAccept extends Tuple {

	public final FileTransferSugestion _sugestion;

	public FileTransferAccept(FileTransferSugestion sugestion) {
		_sugestion = sugestion;
	}

}
