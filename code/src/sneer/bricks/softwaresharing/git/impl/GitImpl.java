package sneer.bricks.softwaresharing.git.impl;

import java.nio.file.Path;

import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryState;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import sneer.bricks.softwaresharing.git.Git;
import sneer.bricks.softwaresharing.git.MergeRequired;

class GitImpl implements Git {

	@Override
	public void pull(Path fromRepo, Path toRepo) throws Exception {
		Repository repository = new FileRepositoryBuilder()
			.setGitDir(toRepo.resolve(".git").toFile())
			.readEnvironment()
			.findGitDir()
			.build();

		try{
			tryToPull(fromRepo, repository);
			if (isMergeRequired(repository)) throw new MergeRequired();
		}finally{
			repository.close();			
		}
		
	}

	private boolean isMergeRequired(Repository repository) {
		return repository.getRepositoryState() == RepositoryState.MERGING;
	}

	private void tryToPull(Path fromRepo, Repository repository)
			throws GitAPIException, WrongRepositoryStateException,
			InvalidConfigurationException, DetachedHeadException,
			InvalidRemoteException, CanceledException, RefNotFoundException,
			NoHeadException, TransportException {
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
