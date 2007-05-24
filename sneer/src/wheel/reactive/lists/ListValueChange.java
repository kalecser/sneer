package wheel.reactive.lists;

public interface ListValueChange {
	
	void accept(Visitor visitor);
	
	public interface Visitor { //Refactor: Consider: For removal and replacement do a "pre"Removal/Replacement notification.
		void elementAdded(int index);
		void elementRemoved(int index);
		void elementReplaced(int index);
		void listReplaced();
	}
}