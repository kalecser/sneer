package sneer.bricks.hardware.cpu.codecs.crypto;

public interface Digester {

	void update(byte[] bytes);

	void update(byte[] bytes, int offset, int length);

	Sneer1024 digest();

	Sneer1024 digest(byte[] bytes);

	void reset();

}
