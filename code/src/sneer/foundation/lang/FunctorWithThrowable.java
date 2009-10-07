package sneer.foundation.lang;


public interface FunctorWithThrowable<A, B, X extends Throwable> {
	
	B evaluate(A value) throws X;

}
