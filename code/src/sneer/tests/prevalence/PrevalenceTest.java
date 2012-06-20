package sneer.tests.prevalence;

import org.junit.Test;

import sneer.tests.SovereignFunctionalTestBase;
import sneer.tests.SovereignParty;
import sneer.tests.prevalence.fixtures.assertion.PrevalenceTestAssertion;
import sneer.tests.prevalence.fixtures.setup.PrevalenceTestSetup;

public class PrevalenceTest extends SovereignFunctionalTestBase {
	
	@Test (timeout = 6000)
	public void test() {
		
		SovereignParty session1 = createParty("Neide");
		session1.loadUnsharedBrick(PrevalenceTestSetup.class.getName());
		
		SovereignParty session2 = newSession(session1);
		session2.loadUnsharedBrick(PrevalenceTestAssertion.class.getName());
	}

}
