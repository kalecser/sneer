package sneer.bricks.pulp.natures.gui.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.pulp.natures.gui.GUI;
import sneer.foundation.brickness.RuntimeNature;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ByRef;

class GUIImpl implements GUI, RuntimeNature {
	
	private final Environment _environment = my(Environment.class);

	@Override
	public Object invoke(Class<?> brick, Object instance, String methodName,
			final Object[] args, final Continuation continuation) {
		
		final ByRef<Object> result = ByRef.newInstance();
		Environments.runWith(_environment, new Runnable() { @Override public void run() {
			my(GuiThread.class).invokeAndWaitForWussies(new Runnable() { @Override public void run() {
				result.value = continuation.invoke(args);
			}});
		}});
		
		return result.value;
	}
	
}