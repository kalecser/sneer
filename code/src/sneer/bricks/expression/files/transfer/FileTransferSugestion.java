package sneer.bricks.expression.files.transfer;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class FileTransferSugestion extends Tuple {

	public final String fileOrFolderName;
	public final boolean isFolder;
	public final long fileSize;
	public final long fileLastModified;

	public FileTransferSugestion(Seal addressee, String fileOrFolderName, boolean isFolder, long fileSize, long fileLastModified) {
		super(addressee);
		this.fileOrFolderName = fileOrFolderName;
		this.isFolder = isFolder;
		this.fileSize = fileSize;
		this.fileLastModified = fileLastModified;
	}

}
