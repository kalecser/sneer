package sneer.bricks.snapps.tasks.impl;

public class Task {

	String _description; 
	
	public Task(String description) {
		_description = description;
	}
	
	@Override
	public String toString() {
		return _description;
	}

}
