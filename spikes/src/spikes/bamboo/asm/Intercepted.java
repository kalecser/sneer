package spikes.bamboo.asm;

interface Interceptor {
	
	public interface Continuation {
		Object invoke(Object[] args);
	}
	
	Object invoke(Class<?> brick, Object instance, String methodName, Object[] args, Continuation defaultInvocation);
}

class InterceptionRuntime {

	public static Object dispatch(
			Class<?> brick,
			Interceptor interceptor,
			Object instance,
			String methodName,
			Object[] args,
			Interceptor.Continuation continuation) {
		
		return interceptor.invoke(brick, instance, methodName, args, continuation);
	}
}

interface SomeInterceptor extends Interceptor {
}

class BrickMetadata {
	public static Class<?> BRICK = Intercepted.class;
	public static Interceptor INTERCEPTOR = my(SomeInterceptor.class);
	static <T> T my(Class<T> t) { return t.cast(null); }
}

public class Intercepted {
	
	public void foo(String arg) {
		InterceptionRuntime.dispatch(BrickMetadata.BRICK, BrickMetadata.INTERCEPTOR, this, "foo", new Object[] { arg }, new fooContinuation(arg));
	}
	
	class fooContinuation implements Interceptor.Continuation {
		private final String _arg;

		public fooContinuation(String arg) {
			_arg = arg;
		}

		@Override
		public Object invoke(Object[] args) {
			_foo(_arg);
			return null;
		}
	}
	
	void _foo(@SuppressWarnings("unused") String arg) {
	}
}