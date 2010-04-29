package sneer.foundation.lang;

public interface Consumer<T> extends PickyConsumer<T> {

	void consume(T value);
	
}
