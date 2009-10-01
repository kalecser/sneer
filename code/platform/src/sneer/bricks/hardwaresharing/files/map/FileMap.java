package sneer.bricks.hardwaresharing.files.map;

import java.io.File;
import java.io.IOException;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileMap {

	Sneer1024 put(File file) throws IOException;

	File get(Sneer1024 hash);

}
