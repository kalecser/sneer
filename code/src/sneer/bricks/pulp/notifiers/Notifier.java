package sneer.bricks.pulp.notifiers;

public interface Notifier<T> {

	Source<T> output();

	void notifyReceivers(T something);
	
}
