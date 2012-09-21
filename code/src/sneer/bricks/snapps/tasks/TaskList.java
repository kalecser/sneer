package sneer.bricks.snapps.tasks;

import sneer.bricks.software.bricks.snapploader.Snapp;
import basis.brickness.Brick;

@Snapp
@Brick
public interface TaskList {

	void addTask(String string);

	int size();

	String getTasks();
	
}
