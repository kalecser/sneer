package sneer.bricks.snapps.wackup.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import sneer.bricks.expression.tuples.testsupport.BrickTestWithTuples;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.latches.Latch;
import sneer.bricks.hardware.cpu.threads.latches.Latches;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.snapps.wackup.Wackup;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;

public class WackupTest extends BrickTestWithTuples {
	
	Wackup wackup2;
	@SuppressWarnings("unused")
	private WeakContract _refToAvoidGC;
	
	@Test(timeout=5000)
	public void newFileIsBackedup() {
		Wackup wackup1 = my(Wackup.class);

	//	System.out.println(wackup1.folder());
		
		assertFalse(new File(wackup1.folder(),"my_new_file.txt").exists());

		//Conectando as pontas do pulso de chegada do arquivo
		Latch latch = my(Latches.class).produce();		
		_refToAvoidGC = wackup1.newFileArrived().addPulseReceiver(latch);

		Environments.runWith(remote(), new Closure() {
			@Override
			public void run() {
				wackup2 = my(Wackup.class);
			//	System.out.println(wackup2.folder());
				writeFile(wackup2.folder());
				my(Clock.class).advanceTime(60*1000);
			} });
	
		// Esperando pelo arquivo transferido
		latch.waitTillOpen();
		
		assertTrue(new File(wackup1.folder(),"my_new_file.txt").exists());

	}

	private void writeFile(File folder) {
		try {
			my(IO.class).files().writeString(new File(folder,"my_new_file.txt"), "My new file content");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

}
