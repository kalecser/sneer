package sneer.bricks.snapps.tasks.impl;

import sneer.bricks.expression.tuples.Tuple;

public class TaskMessage extends Tuple {
	
	public TaskMessage(String task) {
		_task = task;
	}

	public String _task;

}
