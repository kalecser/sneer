package sneer.bricks.expression.files.protocol;

import basis.lang.arrays.ImmutableArray;
import sneer.bricks.expression.tuples.Tuple;

public class FolderContents extends Tuple {

	public final ImmutableArray<FileOrFolder> contents;

	public FolderContents(ImmutableArray<FileOrFolder> contents_) {
		contents = contents_;
	}

}
