package sneer.foundation.testsupport;

import sneer.foundation.lang.Functor;
import sneer.foundation.lang.exceptions.NotImplementedYet;

public class PrettyPrinter {

	static public String toString(@SuppressWarnings("unused") Object object) {
		throw new NotImplementedYet();
	}
	
	static public <T> void registerFor(@SuppressWarnings("unused") Class<T> type, @SuppressWarnings("unused") Functor<T, String> prettyPrinter) {
		throw new NotImplementedYet();
	};
	
}
