package sneer.tests.prevalence;

import org.junit.Test;

import sneer.tests.SovereignFunctionalTestBase;
import sneer.tests.SovereignParty;
import sneer.tests.prevalence.fixtures.assertion.PrevalenceTestAssertion;
import sneer.tests.prevalence.fixtures.setup.PrevalenceTestSetup;

public class PrevalenceTest extends SovereignFunctionalTestBase {
	
	@Test
	public void test() {
		SovereignParty a1 = a();
		a1.loadBrick(PrevalenceTestSetup.class.getName());
		
		SovereignParty a2 = newSession(a1);
		a2.loadBrick(PrevalenceTestAssertion.class.getName());
	}

}
