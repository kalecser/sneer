package sneer.foundation.lang;


public interface Producer<T> extends ProducerX<T, RuntimeException> {
	
	T produce();

}
