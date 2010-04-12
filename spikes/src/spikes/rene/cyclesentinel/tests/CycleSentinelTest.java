package spikes.rene.cyclesentinel.tests;

import org.junit.Ignore;
import org.junit.Test;

import sneer.foundation.testsupport.AssertUtils;
import spikes.rene.cyclesentinel.CycleSentinel;
import spikes.rene.cyclesentinel.DependencyCycle;
import spikes.rene.cyclesentinel.impl.CycleSentinelImpl;

public class CycleSentinelTest extends AssertUtils {

	private CycleSentinel _subject = new CycleSentinelImpl();

	@Test
	public void noCycle() throws DependencyCycle {
		_subject.checkForCycles("Game", "Player");
		_subject.checkForCycles("Main", "Game");
	}

	@Test (expected = DependencyCycle.class)
	public void simpleCycle() throws DependencyCycle {
		_subject.checkForCycles("Game", "Player");
		_subject.checkForCycles("Player", "Game");
	}

	@Test (expected = DependencyCycle.class)
	public void multipleDependencies() throws DependencyCycle {
		_subject.checkForCycles("Game", "Player");
		_subject.checkForCycles("Game", "Kart");
		_subject.checkForCycles("Player", "Game");
	}

	@Ignore
	@Test
	public void packageDependencies() throws DependencyCycle {
		String[] parts = "a.b.c".split("\\.");
		for (String part : parts) System.out.println(part);
		
		_subject.checkForCycles("main.banana.lixo.Main", "game.resourses.noob.Game"); // "main" depends on "game"
		
		try {
			_subject.checkForCycles("game.test.Player", "main.foo.Starter");  // "game" depends on "main"
			fail();
		} catch (DependencyCycle e) {
			assertEquals("main already depends on game", e.getMessage());
		}
	}

	@Ignore
	@Test
	public void innerPackageDependencies() throws DependencyCycle {
		_subject.checkForCycles("main.banana.lixo.Main", "main.banana.test.Player"); // "main.banana.lixo" -> "main.banana.test" 
		try {
			_subject.checkForCycles("main.banana.test.Kart", "main.banana.lixo.Starter"); // "main.banana.test" -> "main.banana.lixo"
			fail();
		} catch (DependencyCycle e) {
			assertEquals("main.banana.lixo already depends on main.banana.test", e.getMessage());
		} 
	}

	
	
}
