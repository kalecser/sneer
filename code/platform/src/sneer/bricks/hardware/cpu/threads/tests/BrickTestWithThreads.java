package sneer.bricks.hardware.cpu.threads.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.After;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.tests.BrickTestWithLogger;

public abstract class BrickTestWithThreads extends BrickTestWithLogger {

	@After
	public void afterBrickTestWithTreads() {
		my(Threads.class).crashAllThreads();

	}

}