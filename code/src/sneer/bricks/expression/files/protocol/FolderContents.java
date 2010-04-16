package sneer.bricks.expression.files.protocol;

import sneer.bricks.expression.tuples.Tuple;
import sneer.bricks.hardware.ram.arrays.ImmutableArray;

public class FolderContents extends Tuple {

	public final ImmutableArray<FileOrFolder> contents;

	public FolderContents(ImmutableArray<FileOrFolder> contents_) {
		contents = contents_;
	}

}
