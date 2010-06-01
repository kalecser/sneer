package sneer.bricks.hardware.gui.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.util.List;

import sneer.bricks.hardware.gui.guithread.GuiThread;
import sneer.bricks.hardware.gui.nature.GUI;
import sneer.bricks.software.bricks.interception.InterceptionEnhancer;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.lang.ByRef;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;

class GUIImpl implements GUI {
	
	static private final GuiThread GuiThread = my(GuiThread.class);

	
	@Override
	public Object invoke(Class<?> brick, Object instance, String methodName, final Object[] args, final Continuation continuation) {
		final ByRef<Object> result = ByRef.newInstance();
		Closure closure = new Closure() { @Override public void run() {
			result.value = continuation.invoke(args);
		}};
		GuiThread.invokeAndWaitForWussies(closure);
		return result.value;
	}

	
	@Override
	public <T> T instantiate(Class<T> brick, Class<T> implClass, final Producer<T> producer) {
		final ByRef<T> result = ByRef.newInstance();
		Closure closure = new Closure() { @Override public void run() {
			result.value = producer.produce();
		}};
		GuiThread.invokeAndWaitForWussies(closure);
		return result.value;
	}
	
	
	@Override
	public List<ClassDefinition> realize(Class<?> brick, ClassDefinition classDef) {
		return my(InterceptionEnhancer.class).realize(brick, GUI.class, classDef);
	}
}