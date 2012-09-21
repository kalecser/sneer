package sneer.bricks.snapps.tasks.impl;

import junit.framework.Assert;

import org.junit.Test;

import sneer.bricks.snapps.tasks.TaskList;


public class TaskTests {

	@Test
	public void addTask() {
		TaskList taskList = new TaskListImpl();
		taskList.addTask("Foo");
		taskList.addTask("Bar");
		Assert.assertEquals(2, taskList.size());
		Assert.assertEquals("[Foo, Bar]",taskList.getTasks());
	}
	
}
