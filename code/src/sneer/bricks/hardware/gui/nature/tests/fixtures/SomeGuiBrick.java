package sneer.bricks.hardware.gui.nature.tests.fixtures;

import java.awt.event.*;

import basis.brickness.Brick;
import basis.environments.Environment;

import sneer.bricks.hardware.gui.nature.GUI;

@Brick(GUI.class)
public interface SomeGuiBrick {

	Thread currentThread();

	Thread constructorThread();
	
	Environment currentEnvironment();

	void run(Runnable runnable);

	ActionListener listenerFor(Environment expectedEnvironment);
	
	Thread complexMethodWithVariablesAndFinallyBlock();


}
