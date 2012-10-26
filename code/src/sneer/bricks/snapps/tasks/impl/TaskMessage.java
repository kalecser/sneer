package sneer.bricks.snapps.tasks.impl;

import sneer.bricks.expression.tuples.Tuple;

public class TaskMessage extends Tuple {
	
	private Task _task;
	
	public TaskMessage(Task task) {
		setTask(task);
	}

	public Task getTask() {
		return _task;
	}

	public void setTask(Task _task) {
		this._task = _task;
	}

}
