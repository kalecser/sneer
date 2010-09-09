package sneer.bricks.hardware.cpu.threads.impl;


abstract class Daemon extends Thread {

	Daemon(String name) {
		super(name);
		setDaemon(true);
		start();
	}
	
}
