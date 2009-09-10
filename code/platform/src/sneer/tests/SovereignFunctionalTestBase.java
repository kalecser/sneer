package sneer.tests;

import org.junit.After;

import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.tests.adapters.SneerCommunity;


/** Abstract test class names must not end in "Test" or else Hudson will try to instantiate them and fail. :P */
public abstract class SovereignFunctionalTestBase extends BrickTest {

	private SovereignCommunity _subject = createNewCommunity();
	
	private SovereignParty _a;
	private SovereignParty _b;


	private SovereignCommunity createNewCommunity() {
		//Refactor This dependency on SneerCommunity should not exist, but there is no way to inject an abstract dependency into a JUnit test.
		return new SneerCommunity(tmpFolder()); 
	}

	protected SovereignParty a() {
		init();
		return _a;
	}

	protected SovereignParty b() {
		init();
		return _b;
	}

	protected SovereignParty createParty(String name) {
		return _subject.createParty(name);
	}

	private void init() { //This is done lazily because it has to run as part of the test and not during the constructor or even during the @Before method because JUnit will not count those as part of the test's timeout. :(
		if (_a != null) return;
		
		_a = _subject.createParty("Ana Almeida");
		_b = _subject.createParty("Bruno Barros");
		
		connect(_a, _b);
	}

	protected void connect(SovereignParty a, SovereignParty b) {
		_subject.connect(a, b);
	}

	
	@After
	public void releaseCommunity() {
		_subject.crash();
		_subject = null;
		_a = null;
		_b = null;
	}


	protected SovereignParty newSession(SovereignParty party) {
		SovereignParty restarted = _subject.newSession(party);
		
		if (party == _a) _a = restarted;
		if (party == _b) _b = restarted;
		
		return restarted;
	}
	
	
}
