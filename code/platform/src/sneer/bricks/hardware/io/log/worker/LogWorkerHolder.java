package sneer.bricks.hardware.io.log.worker;

import sneer.foundation.brickness.Brick;


@Brick
public interface LogWorkerHolder {

	void setWorker(LogWorker worker);
	LogWorker worker();

}
