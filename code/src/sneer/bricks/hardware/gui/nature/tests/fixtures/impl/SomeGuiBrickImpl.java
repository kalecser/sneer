package sneer.bricks.hardware.gui.nature.tests.fixtures.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Assert;

import sneer.bricks.hardware.gui.nature.tests.fixtures.SomeGuiBrick;
import sneer.foundation.environments.Environment;

class SomeGuiBrickImpl implements SomeGuiBrick {
	
	private Thread _constructorThread = Thread.currentThread();

	@Override
	public Thread constructorThread() {
		return _constructorThread;
	}

	@Override
	public Thread currentThread() {
		return Thread.currentThread();
	}

	@Override
	public Environment currentEnvironment() {
		return my(Environment.class);
	}

	@Override
	public void run(Runnable runnable) {
		runnable.run();
	}

	@Override
	public ActionListener listenerFor(final Environment expectedEnvironment) {
		return new ActionListener() { @Override public void actionPerformed(ActionEvent e) {
			Assert.assertSame(expectedEnvironment, currentEnvironment());
		}};
	}

	@Override
	public Thread complexMethodWithVariablesAndFinallyBlock() {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream("foo".getBytes()); 
		} catch (Exception e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		} finally {
			try { is.close(); } catch (Exception e2) { /* ignore */ }
		}
		return Thread.currentThread();
	}
	
}