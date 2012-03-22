package sneer.bricks.hardware.io.log.worker;

import basis.brickness.Brick;


@Brick
public interface LogWorkerHolder {

	void setWorker(LogWorker worker);
	LogWorker worker();

}
