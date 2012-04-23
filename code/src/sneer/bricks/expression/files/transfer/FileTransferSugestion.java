package sneer.bricks.expression.files.transfer;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class FileTransferSugestion extends Tuple {

	public final String fileOrFolderName;
	public final long fileLastModified;
	public final boolean isFolder;

	public FileTransferSugestion(Seal addressee, String fileOrFolderName, boolean isFolder, long fileLastModified) {
		super(addressee);
		this.fileOrFolderName = fileOrFolderName;
		this.isFolder = isFolder;
		this.fileLastModified = fileLastModified;
	}

}
