package sneer.bricks.expression.files.transfer;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class FileTransferSugestion extends Tuple {

	public final String fileOrFolderName;

	public FileTransferSugestion(String fileOrFolderName, Seal addressee) {
		super(addressee);
		this.fileOrFolderName = fileOrFolderName;
	}

}
