package sneer.bricks.expression.files.transfer;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.identity.seals.Seal;

public class FileTransferStatus extends Tuple {
	
	public String path;
	public int completion;

	public FileTransferStatus(String path, int completion, Seal sender) {
		super(sender);
		this.path = path;
		this.completion = completion;
	}
	
	@Override
	public String toString() {
		return completion + "% uploading " + path; 
	}
	
}
