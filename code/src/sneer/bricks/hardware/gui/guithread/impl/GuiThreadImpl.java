package sneer.bricks.hardware.gui.guithread.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

class GuiThreadImpl implements GuiThread {
	
	private static final Environment Environment = my(Environment.class);
	private Set<Thread> _threadsThatShouldNotWaitForGui = new HashSet<Thread>();

	
	@Override
	public void invokeAndWaitForWussies(final Closure closure) { //Fix This method is called sometimes from swing's thread and other times from aplication's thread. Split the caller method (if it is possible), and delete this method.
		if(SwingUtilities.isEventDispatchThread())
			closure.run();
		else
			invokeAndWait(closure);
	}

	
	@Override
	public void invokeAndWait(final Closure closure) { //Fix Calling this from brick code is no longer necessary after the container is calling gui brick code only in the Swing thread.
		assertNotInGuiThread();
		assertThreadCanWaitForGui();
		try {
			SwingUtilities.invokeAndWait(envolve(closure));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	
	@Override
	public void invokeLater(Closure closure) {
		SwingUtilities.invokeLater(envolve(closure));
	}

	
	@Override
	public void assertInGuiThread() {
		if (!SwingUtilities.isEventDispatchThread()) throw new IllegalStateException("Should be running in the GUI thread."); 
	}

	
	@Override
	public void assertNotInGuiThread() {
		if (SwingUtilities.isEventDispatchThread()) throw new IllegalStateException("Should NOT be running in the GUI thread."); 
	}
	
	
	private Closure envolve(final Closure delegate) {
		return new Closure() { @Override public void run() {
			Environments.runWith(Environment, delegate);
		}};
	}

	
	private void assertThreadCanWaitForGui() {
		if (_threadsThatShouldNotWaitForGui.contains(Thread.currentThread())) throw new IllegalStateException("The current thread should not have to wait for the GUI thread."); 
	}

}
