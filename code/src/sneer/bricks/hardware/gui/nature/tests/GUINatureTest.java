package sneer.bricks.hardware.gui.nature.tests;

import static basis.environments.Environments.my;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.junit.Test;

import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;

import sneer.bricks.hardware.gui.nature.tests.fixtures.SomeGuiBrick;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;

//TODO: nature inheritance (annotation Instrument interface for instance)
//TODO: methods declaring checked exceptions
public class GUINatureTest extends BrickTestBase {

	@Test
	public void instantiationHappensInTheSwingThread() {
		assertTrue(isGuiThread(my(SomeGuiBrick.class).constructorThread()));
	}

	@Test
	public void invocationHappensInTheSwingThread() {
		assertTrue(isGuiThread(my(SomeGuiBrick.class).currentThread()));
	}

	@Test
	public void listenerInvocationHappensInBricknessEnvironment() {
		final ActionListener listener = my(SomeGuiBrick.class).listenerFor(my(Environment.class));
		Environments.runWith(null, new Closure() { @Override public void run() {
			listener.actionPerformed(new ActionEvent(this, 0, null));
		}});
	}

	@Test
	public void invocationHappensInBricknessEnvironment() {
		assertSame(my(Environment.class), my(SomeGuiBrick.class).currentEnvironment());
	}

	@Test
	public void invocationInTheSwingThreadForVoidMethod() {
		assertFalse(isGuiThread(Thread.currentThread()));
		my(SomeGuiBrick.class).run(new Closure() { @Override public void run() {
			assertTrue(isGuiThread(Thread.currentThread()));
		}});
	}
	
	@Test
	public void complexMethodWithVariablesAndFinallyBlock() {
		assertTrue(isGuiThread(my(SomeGuiBrick.class).complexMethodWithVariablesAndFinallyBlock()));
	}

	private boolean isGuiThread(Thread thread) {
		return thread.getName().contains("AWT");
	}

}
