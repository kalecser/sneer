package sneer.bricks.pulp.natures.gui.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.pulp.natures.gui.GUI;
import sneer.foundation.brickness.RuntimeNature;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Producer;

class GUIImpl implements GUI, RuntimeNature {
	
	private final Environment _environment = my(Environment.class);

	@Override
	public Object invoke(Class<?> brick, Object instance, String methodName,
			final Object[] args, final Continuation continuation) {
		
		final ByRef<Object> result = ByRef.newInstance();
		invokeInGuiThread(new Runnable() { @Override public void run() {
			result.value = continuation.invoke(args);
		}});
		return result.value;
	}

	@Override
	public <T> T instantiate(Class<T> brick, Class<?> implClass, final Producer<T> producer) {
		
		final ByRef<T> result = ByRef.newInstance();
		invokeInGuiThread(new Runnable() { @Override public void run() {
			result.value = producer.produce();
		}});
		return result.value;
	}
	
	private void invokeInGuiThread(final Runnable runnable) {
		Environments.runWith(_environment, new Runnable() { @Override public void run() {
			my(GuiThread.class).invokeAndWaitForWussies(runnable);
		}});
	}
	
}