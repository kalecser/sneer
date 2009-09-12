package sneer.foundation.lang;


public interface ProducerWithThrowable<T, X extends Throwable> {

	T produce() throws X;
	
}
