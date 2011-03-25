package spikes.klaus;

import java.io.File;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;

import net.sbbi.upnp.impls.InternetGatewayDevice;


@SuppressWarnings("deprecation")
public class Anything {

	public static void main(String[] args) throws Exception {
		Process process = Runtime.getRuntime().exec("git pull");
		InputStream in = process.getInputStream();
		while ("".isEmpty()) {
			int read = in.read();
			if (read == -1) break;
			System.out.write(read);
		}
		System.out.println("Process: " + process.waitFor());
		
		
		while ("".isEmpty()) {
			System.out.println(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
			Thread.sleep(1000);
		}
		
		
		int discoveryTimeout = 5000; // 5 secs to receive a response from
										// devices
		try {
			InternetGatewayDevice[] IGDs = InternetGatewayDevice
					.getDevices(discoveryTimeout);
			if (IGDs != null) {
				// let's the the first device found
				InternetGatewayDevice testIGD = IGDs[0];
				System.out.println("Found device "
						+ testIGD.getIGDRootDevice().getModelName());
				// now let's open the port
				String localHostIP = "192.168.1.100";
				System.out.println("My ip: " + localHostIP);
				// we assume that localHostIP is something else than 127.0.0.1
				boolean mapped = testIGD.addPortMapping(
						"Some mapping description", null, 5900, 9090,
						localHostIP, 0, "TCP");
				if (mapped) {
					System.out.println("Port 9090 mapped to " + localHostIP);
					// and now close it
//					boolean unmapped = testIGD.deletePortMapping(null, 9090,
//							"TCP");
//					if (unmapped) {
//						System.out.println("Port 9090 unmapped");
//					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
//		catch (UPNPResponseException respEx) {
//			// oups the IGD did not like something !!
//		}
		
		
		System.exit(0);
		
		
		File classFile = new File(Anything.class.getResource("Anything.class").toURI());
//		File classFile = new File("lixo35345435");
		System.out.println(classFile);
//		classFile.createNewFile();
		System.out.println(classFile.exists());
		File banana = new File(classFile.getParentFile(), "banana");
		System.out.println(banana);
		System.out.println("Renamed: " + classFile.renameTo(banana));
		System.out.println(classFile.exists());
		
		System.exit(0);
		
		new Daemon("Spike") { @Override public void run() {
			while (true) {
				new Object();
				//System.out.println("creating");
			}
		}};
		
		WeakReference<Object> weak = new WeakReference<Object>(new Object());
		//Object obj = weak.get();
		while (true) {
//			byte[] lixo = new byte[1000000];
			Thread.yield();
			Object object = weak.get();
			if (object == null) return;
			System.out.println(System.currentTimeMillis());
			object.toString();
			object = null;
			//System.gc();
		}

//		System.out.println(System.nanoTime() % 3);
		
//		for (int i = 0; i <= 0; i++)
//			new Thread("Test " + i) { @Override public void run() {
//				Thread.currentThread().setPriority(MAX_PRIORITY);
//				testTime();
//			}}.start();

		
//		while (true) {
//			long now = now();
//			if ((now - _lastTime) < (1000L * 1000 * 1000 * 5)) continue;
//			System.out.println("" + now + "  " + (now - _lastTime));
//			_lastTime = now;
//		}

		
//		ImageIO.write(image, "png", output);
		
//		JWindow window = new JWindow();
//		window.setBounds(0, 0, 300, 300);
//		window.setName("Banana");
//		window.setVisible(true);
		
//		GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//		DisplayMode mode = device.getDisplayMode();
//		System.out.println("" + mode.getWidth() + " x " + mode.getHeight());
	}

//	private static void testTime() {
//		long t0 = now();
//		int counter = 0;
//
//		while (true) {
//			Threads.sleepWithoutInterruptions(1);
//			long now = now();
//			
//			if (now - t0 > 20L * 1000 * 1000 * 1000) break;
//			counter++;
//		}
//		
//		System.out.println(counter / 20);
//	}
//	
//	static private long now() {
//		return System.nanoTime();
//	}
}
