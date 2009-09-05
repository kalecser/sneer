package sneer.bricks.hardware.io.log.worker;

public interface LogWorker {

	void log(String message, Object... messageInsets);

}
