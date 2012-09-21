package sneer.bricks.snapps.tasks.impl;

import static basis.environments.Environments.my;

import java.util.ArrayList;
import java.util.List;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.tasks.TaskList;
import basis.lang.Consumer;

public class TaskListImpl implements TaskList {

	
	@SuppressWarnings("unused")
	private WeakContract _refToAvoidGc;

	{
		setupContextMenu();
		subscribeToReceiveGoMessages();
	}
	
	private void setupContextMenu() {
		my(ContactActionManager.class).addContactAction(new ContactAction() {
			@Override public void run() {
				TaskMessage taskMessage = new TaskMessage("foo");
				my(TupleSpace.class).add(taskMessage);
			}

			@Override public String caption() { return "Task"; }
			@Override public boolean isVisible() { return true; }
			@Override public boolean isEnabled() { return true; }
			@Override public int positionInMenu() { return 400; }

		});
	}
	
	private void subscribeToReceiveGoMessages() {
		_refToAvoidGc = my(TupleSpace.class).addSubscription(TaskMessage.class, new Consumer<TaskMessage>() { @Override public void consume(final TaskMessage message) {
			my(TaskList.class).addTask(message._task);
		}});
	
}	
	private List<Task> _toDos = new ArrayList<Task>();
	
	@Override
	public void addTask(String description) {
		_toDos.add(new Task(description));
	}

	@Override
	public int size() {
		return _toDos.size();
	}

	@Override
	public String getTasks() {
		return _toDos.toString();
	}
	

}
