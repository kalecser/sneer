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
	
	public void foo() {
		InterceptionRuntime.dispatch(BrickMetadata.BRICK, BrickMetadata.INTERCEPTOR, this, "foo", new Object[0], new fooContinuation());
	}
	
	class fooContinuation implements Interceptor.Continuation {
		@Override
		public Object invoke(Object[] args) {
			_foo();
			return null;
		}
	}
	
	void _foo() {
	}
}