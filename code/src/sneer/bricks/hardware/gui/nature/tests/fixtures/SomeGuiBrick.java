package sneer.bricks.hardware.gui.nature.tests.fixtures;

import java.awt.event.*;

import sneer.bricks.hardware.gui.nature.GUI;
import sneer.foundation.brickness.Brick;
import sneer.foundation.environments.Environment;

@Brick(GUI.class)
public interface SomeGuiBrick {

	Thread currentThread();

	Thread constructorThread();
	
	Environment currentEnvironment();

	void run(Runnable runnable);

	ActionListener listenerFor(Environment expectedEnvironment);
	
	Thread complexMethodWithVariablesAndFinallyBlock();


}
