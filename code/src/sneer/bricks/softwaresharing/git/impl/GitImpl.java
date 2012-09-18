package sneer.bricks.softwaresharing.git.impl;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryState;
import org.eclipse.jgit.lib.StoredConfig;

import sneer.bricks.softwaresharing.git.Git;

class GitImpl implements Git {

	@Override
	public void pull(Path fromRepo, Path toRepo) throws Exception {
		try (AutoCloseableRepository repository = open(toRepo)) {
			tryToPull(fromRepo, repository.delegate);
		}
	}

	
	@Override
	public  boolean isMergeRequired(Path repository) throws IOException {
		return open(repository).delegate.getRepositoryState() == RepositoryState.MERGING;
	}

	
	private void tryToPull(Path fromRepo, Repository repository) throws Exception {
		StoredConfig config = repository.getConfig();
		config.setString("remote", "origin", "fetch",
				"+refs/heads/*:refs/remotes/origin/*");
		config.setString("remote", "origin", "url", fromRepo.toAbsolutePath().toString());
		config.setString("branch", "master", "remote", "origin");
		config.setString("branch", "master", "merge", "refs/heads/master");

		org.eclipse.jgit.api.Git git = new org.eclipse.jgit.api.Git(repository);

		PullCommand a = git.pull();
		a.call();
	}

	
	static private AutoCloseableRepository open(Path path) throws IOException {
		return new AutoCloseableRepository(path);
	}
}

