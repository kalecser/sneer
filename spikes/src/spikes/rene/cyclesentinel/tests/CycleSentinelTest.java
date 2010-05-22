package spikes.rene.cyclesentinel.tests;

import org.junit.Test;

import sneer.bricks.software.folderconfig.testsupport.BrickTestWithFiles;
import spikes.rene.cyclesentinel.CycleSentinel;
import spikes.rene.cyclesentinel.DependencyCycle;
import spikes.rene.cyclesentinel.impl.CycleSentinelImpl;

public class CycleSentinelTest extends BrickTestWithFiles {

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

	@Test
	public void packageDependencies() throws DependencyCycle {
		_subject.checkForCycles("main.banana.lixo.Main", "game.resourses.noob.Game"); // "main" depends on "game"
		
		try {
			_subject.checkForCycles("game.test.Player", "main.foo.Starter");  // "game" depends on "main"
			fail();
		} catch (DependencyCycle e) {
			assertEquals(
					"Dependency cycle detected:\n" +
					"	main -> game  (main.banana.lixo.Main -> game.resourses.noob.Game)\n" +
					"	game -> main  (game.test.Player -> main.foo.Starter)",
					e.getMessage());
		}
	}

	@Test
	public void innerPackageDependencies() throws DependencyCycle {
		_subject.checkForCycles("main.banana.lixo.Main", "main.banana.test.Player"); // "main.banana.lixo" -> "main.banana.test" 
		try {
			_subject.checkForCycles("main.banana.test.Kart", "main.banana.lixo.Starter"); // "main.banana.test" -> "main.banana.lixo"
			fail();
		} catch (DependencyCycle e) {
			assertEquals(
					"Dependency cycle detected:\n" +
					"	main.banana.test -> main.banana.lixo  (main.banana.test.Kart -> main.banana.lixo.Starter)\n" +
					"	main.banana.lixo -> main.banana.test  (main.banana.lixo.Main -> main.banana.test.Player)", e.getMessage());
		} 
	}

	@Test
	public void indirectCycle() throws DependencyCycle {
		_subject.checkForCycles("a", "b"); 
		_subject.checkForCycles("b", "c"); 
		try {
			_subject.checkForCycles("c", "a"); 
			fail();
		} catch (DependencyCycle e) {
			assertEquals(
					"Dependency cycle detected:\n" +
					"	a -> b\n" +
					"	b -> c\n" +
					"	c -> a", e.getMessage());
		}

		_subject.checkForCycles("foo", "bar");
	}

	
	
}
