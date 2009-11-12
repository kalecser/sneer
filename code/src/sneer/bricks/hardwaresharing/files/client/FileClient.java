package sneer.bricks.hardwaresharing.files.client;

import java.io.File;
import java.io.IOException;

import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.foundation.brickness.Brick;

@Brick
public interface FileClient {

	void fetchFile(File file, long lastModified, Sneer1024 hashOfFile) throws IOException;
	void fetchFile(File file, Sneer1024 hashOfFile) throws IOException;

	void fetchFolder(File folder, long lastModified, Sneer1024 hashOfFolder) throws IOException;
	void fetchFolder(File folder, Sneer1024 hashOfFolder) throws IOException;

}
