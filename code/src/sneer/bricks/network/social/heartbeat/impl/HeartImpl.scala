package sneer.bricks.network.social.heartbeat.impl

import sneer.foundation.environments.Environments._
import sneer.bricks.hardware.clock.timer.Timer
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract
import sneer.bricks.network.social.heartbeat.Heart
import sneer.bricks.network.social.heartbeat.Heartbeat
import sneer.bricks.pulp.tuples.TupleSpace
import sneer.foundation.lang.Closure

class HeartImpl extends Heart {

  	val _timerContract = my(classOf[Timer]).wakeUpNowAndEvery(10 * 1000, new Closure() { override def run() {
			beat() }})

	private def beat() {
		my(classOf[TupleSpace]).acquire(new Heartbeat())
	}

}
