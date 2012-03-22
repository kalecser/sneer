package sneer.bricks.snapps.system.log.sysout.impl;

import static basis.environments.Environments.my;
import basis.lang.Consumer;
import sneer.bricks.hardware.io.log.notifier.LogNotifier;
import sneer.bricks.snapps.system.log.sysout.LogToSysout;

class LogToSysoutImpl implements LogToSysout {

	@SuppressWarnings("unused")	private final Object _referenceToAvoidGc;

	{
		_referenceToAvoidGc = my(LogNotifier.class).loggedMessages().addReceiver(new Consumer<String>(){ @Override public void consume(String msg) {
			log(msg);
		}});
	}

	private void log(String msg){
		System.out.print(msg);
	}
}