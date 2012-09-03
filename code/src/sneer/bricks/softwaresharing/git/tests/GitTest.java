package sneer.bricks.softwaresharing.git.tests;

import static basis.environments.Environments.my;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

import sneer.bricks.hardware.io.IO;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.bricks.softwaresharing.git.Git;
import basis.lang.types.Classes;

public class GitTest extends BrickTestBase {

	private final Git subject = my(Git.class);
	
	@Test
	public void pull() throws Exception {
		Path fromRepo = newTmpFile("repo1").toPath();
		prepare(".git-repo-with-one-commit", fromRepo);

		Path toRepo = newTmpFile("repo2").toPath();
		prepare(".git-empty-repo", toRepo);
		
		assertFalse(Files.exists(toRepo.resolve("readme.txt")));
		subject.pull(fromRepo, toRepo);
		assertTrue(Files.exists(toRepo.resolve("readme.txt")));
	}

	
	private void prepare(String repoFixture, Path repo) throws IOException {
		Files.createDirectory(repo);
		Path fixture = Classes.fileFor(getClass()).getParentFile().toPath().resolve("gitfixtures/" + repoFixture);
		my(IO.class).files().copyFolder(fixture.toFile(), repo.resolve(".git").toFile());
	}
	
}
