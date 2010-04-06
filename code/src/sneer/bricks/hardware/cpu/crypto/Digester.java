package sneer.bricks.hardware.cpu.crypto;

public interface Digester {

	void update(byte[] bytes);

	void update(byte[] bytes, int offset, int length);

	Hash digest();

	Hash digest(byte[] bytes);

}
