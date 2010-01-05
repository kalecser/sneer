package sneer.bricks.hardware.gui.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.List;

import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.software.bricks.interception.InterceptionEnhancer;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;

class GUIImpl implements GUI {
	
	private final Environment _environment = my(Environment.class);

	
	@Override
	public Object invoke(Class<?> brick, Object instance, String methodName,	final Object[] args, final Continuation continuation) {
		final ByRef<Object> result = ByRef.newInstance();
		invokeInGuiThread(new Closure() { @Override public void run() {
			result.value = continuation.invoke(args);
		}});
		return result.value;
	}

	
	@Override
	public <T> T instantiate(Class<T> brick, Class<T> implClass, final Producer<T> producer) {
		
		final ByRef<T> result = ByRef.newInstance();
		invokeInGuiThread(new Closure() { @Override public void run() {
			result.value = producer.produce();
		}});
		return result.value;
	}
	
	
	@Override
	public List<ClassDefinition> realize(ClassDefinition classDef) {
		return my(InterceptionEnhancer.class).realize(GUI.class, classDef);
	}

	
	private void invokeInGuiThread(final Closure closure) {
		Environments.runWith(_environment, new Closure() { @Override public void run() {
			my(GuiThread.class).invokeAndWaitForWussies(closure);
		}});
	}
}