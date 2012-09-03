package sneer.bricks.softwaresharing.git.impl;

import java.nio.file.Path;

import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import sneer.bricks.softwaresharing.git.Git;

class GitImpl implements Git {

	@Override
	public void pull(Path fromRepo, Path toRepo) throws Exception {
		Repository repository = new FileRepositoryBuilder()
			.setGitDir(toRepo.resolve(".git").toFile())
			.readEnvironment()
			.findGitDir()
			.build();

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

}
