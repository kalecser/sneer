package sneer.foundation.testsupport;

import java.util.HashMap;
import java.util.Map;

import sneer.foundation.lang.Functor;

public class PrettyPrinter {

	private static Map<Class<?>, Functor<?, String>> _printersByType = new HashMap<Class<?>, Functor<?,String>>();

	public static <T> String toString(T object) {
		if (object == null) return null;

		Functor<?, String> printer = _printersByType.get(object.getClass());
		return (printer == null) ? object.toString() : ((Functor<T, String>) printer).evaluate(object);
	}
	
	public static <T> void registerFor(Class<T> type, Functor<T, String> prettyPrinter) {
		_printersByType.put(type, prettyPrinter);
	};
	
}
