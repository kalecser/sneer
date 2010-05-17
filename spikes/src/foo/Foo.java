package foo;

public class Foo extends FooBase {
	
	@Override
	public int bar() {
		return 42;
	}
	
	public int baz(Foo foo) {
		return foo.bar();
	}

}
