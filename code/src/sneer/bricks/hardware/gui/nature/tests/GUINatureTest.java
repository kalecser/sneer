package sneer.bricks.hardware.gui.nature.tests;

import static basis.environments.Environments.my;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.junit.Test;

import sneer.bricks.hardware.gui.nature.tests.fixtures.SomeGuiBrick;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import basis.environments.Environment;
import basis.environments.Environments;
import basis.lang.Closure;

//TODO: nature inheritance (annotation Instrument interface for instance)
//TODO: methods declaring checked exceptions
public class GUINatureTest extends BrickTestBase {

	@Test
	public void instantiationHappensInTheSwingThread() {
		if (GraphicsEnvironment.isHeadless()) return;
		assertTrue(isGuiThread(my(SomeGuiBrick.class).constructorThread()));
	}

	@Test
	public void invocationHappensInTheSwingThread() {
		if (GraphicsEnvironment.isHeadless()) return;
		assertTrue(isGuiThread(my(SomeGuiBrick.class).currentThread()));
	}

	@Test
	public void listenerInvocationHappensInBricknessEnvironment() {
		if (GraphicsEnvironment.isHeadless()) return;
		final ActionListener listener = my(SomeGuiBrick.class).listenerFor(my(Environment.class));
		Environments.runWith(null, new Closure() { @Override public void run() {
			listener.actionPerformed(new ActionEvent(this, 0, null));
		}});
	}

	@Test
	public void invocationHappensInBricknessEnvironment() {
		if (GraphicsEnvironment.isHeadless()) return;
		assertSame(my(Environment.class), my(SomeGuiBrick.class).currentEnvironment());
	}

	@Test
	public void invocationInTheSwingThreadForVoidMethod() {
		if (GraphicsEnvironment.isHeadless()) return;
		assertFalse(isGuiThread(Thread.currentThread()));
		my(SomeGuiBrick.class).run(new Closure() { @Override public void run() {
			assertTrue(isGuiThread(Thread.currentThread()));
		}});
	}
	
	@Test
	public void complexMethodWithVariablesAndFinallyBlock() {
		if (GraphicsEnvironment.isHeadless()) return;
		assertTrue(isGuiThread(my(SomeGuiBrick.class).complexMethodWithVariablesAndFinallyBlock()));
	}

	private boolean isGuiThread(Thread thread) {
		return thread.getName().contains("AWT");
	}

}
