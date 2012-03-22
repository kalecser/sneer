package sneer.bricks.hardware.cpu.threads.tests;

import static basis.environments.Environments.my;

import org.junit.After;

import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.tests.BrickTestWithLogger;

public abstract class BrickTestWithThreads extends BrickTestWithLogger {

	@After
	public void afterBrickTestWithTreads() {
		crash();
	}

	protected void crash(Environment environment) {
		Environments.runWith(environment, new Closure() { @Override public void run() {
			crash();
		}});
	}

	private void crash() {
		my(Threads.class).crashAllThreads();
	}

}
