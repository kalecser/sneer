package sneer.bricks.hardwaresharing.files.client;

import java.io.File;
import java.io.IOException;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileClient {

	void fetch(File file, long lastModified, Sneer1024 hashOfContents) throws IOException;
	void fetch(File file, Sneer1024 hashOfContents) throws IOException;

}
