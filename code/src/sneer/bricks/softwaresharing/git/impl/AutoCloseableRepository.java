package sneer.bricks.softwaresharing.git.impl;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;


class AutoCloseableRepository implements AutoCloseable {

	final FileRepository delegate;

	AutoCloseableRepository(Path path) throws IOException {
		delegate = new FileRepositoryBuilder()
		.setGitDir(path.resolve(".git").toFile())
		.readEnvironment()
		.findGitDir()
		.build();

	}

	@Override
	public void close() throws Exception {
		delegate.close();
	}

}
