package sneer.bricks.hardware.cpu.algorithms.crypto;


public interface Digester {

	void update(byte[] bytes);

	Sneer1024 digest();

}
