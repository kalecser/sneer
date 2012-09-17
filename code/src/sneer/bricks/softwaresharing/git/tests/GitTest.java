package sneer.bricks.softwaresharing.git.tests;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.bricks.softwaresharing.git.Git;
import sneer.bricks.softwaresharing.git.MergeRequired;
import basis.lang.types.Classes;

public class GitTest extends BrickTestBase {

	private final Git subject = my(Git.class);
	private final Path fromRepo = newTmpFile("repo1").toPath();
	private final Path emptyRepo = newTmpFile("repo2").toPath();
	private final Path conflictingRepo = newTmpFile("repo3").toPath();

	{
		try {
			prepareRepoWithOneCommit(fromRepo);
			prepareEmptyRepo(emptyRepo);
			prepareConflictingRepo(conflictingRepo);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	
	@Test
	public void pull() throws Exception {
		assertFalse(Files.exists(emptyRepo.resolve("readme.txt")));
		subject.pull(fromRepo, emptyRepo);
		assertTrue(Files.exists(emptyRepo.resolve("readme.txt")));
	}

	@Test(expected = Exception.class)
	public void pullWithError_UntrackedEmptyFileSameAsFileBeingPulled() throws Exception {
		Files.createFile(emptyRepo.resolve("readme.txt"));
		subject.pull(fromRepo, emptyRepo);
	}
	
	@Test(expected = MergeRequired.class)
	public void pullWithConflict() throws Exception {
		subject.pull(fromRepo, conflictingRepo);
	}

	public static void prepareEmptyRepo(Path path) throws IOException {
		prepare(".git-empty-repo", path);
	}

	public static void prepareRepoWithOneCommit(Path path) throws IOException {
		prepare(".git-repo-with-one-commit", path);
	}

	private void prepareConflictingRepo(Path path) throws IOException {
		prepare(".git-repo-with-conflicting-commit", path);
	}
	
	private static void prepare(String repoFixture, Path repo) throws IOException {
		Files.createDirectory(repo);
		Path fixture = Classes.fileFor(GitTest.class).getParentFile().toPath().resolve("gitfixtures/" + repoFixture);
		my(IO.class).files().copyFolder(fixture.toFile(), repo.resolve(".git").toFile());
	}
}
