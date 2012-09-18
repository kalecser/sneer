package sneer.tests.freedom7;

import org.junit.Ignore;
import org.junit.Test;

import sneer.tests.SovereignFunctionalTestBase;

public class Freedom7TestGit extends SovereignFunctionalTestBase {

	@Ignore
	@Test (timeout = 1000 * 10)
	public void meToo() throws Exception {
		//LoggerMocks.showLog = true;

		a().gitPrepareEmptyRepo();
		b().gitPrepareRepoWithOneCommit();
		a().gitPullFrom(b().ownName());
		assertTrue(a().gitHasOneCommit());
	}

}