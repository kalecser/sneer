package spikes.gandhi.classloading;

public class ObjectImplementation implements ObjectInterface{

	@Override
	public String toLowercase(String text) {
		return text.toLowerCase();
	}

	@Override
	public InnerClassInterface plus() {
		return new InnerClassInterface(){
			@Override
			public int execute(int a, int b) {
				return a + b;
			}
		};
	}

	@Override
	public InnerClassInterface minus() {
		return new InnerClassInterface(){
			@Override
			public int execute(int a, int b) {
				return a - b;
			}
		};
	}

	@Override
	public InnerClassInterface multiply() {
		return new InnerClassInterface(){
			@Override
			public int execute(int a, int b) {
				return a * b;
			}
		};
	}	
	
	static byte[] arrayToConsumeMemory = new byte[1024 * 1024 * 10];
}
