package sneer.bricks.snapps.tasks.tests;

import org.junit.Test;

import sneer.bricks.snapps.tasks.TaskList;
import sneer.bricks.snapps.tasks.impl.TaskListImpl;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;


public class TaskTests extends BrickTestBase{

	@Test
	public void addTask() {
		TaskList taskList = new TaskListImpl();
		taskList.addTask("Foo");
		taskList.addTask("Bar");
		assertEquals(2, taskList.size());
		assertEquals("[Foo, Bar]",taskList.getTasks());
	}
	
}
