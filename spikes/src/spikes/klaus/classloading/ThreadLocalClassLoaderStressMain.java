package spikes.klaus.classloading;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.net.URL;

import basis.brickness.impl.EagerClassLoader;


public class ThreadLocalClassLoaderStressMain {

	public static void main(String[] args) throws Exception {
		long count = 0;
		while(true) {
			ClassLoader loader = new EagerClassLoader(new URL[] { new File("bin").toURI().toURL() }, ThreadLocalClassLoaderStressMain.class.getClassLoader()) {
				@Override
				protected boolean isEagerToLoad(String className) {
					return className.equals(Keeper.class.getName());
				}
			};
			Class<?> keeperClass = loader.loadClass(Keeper.class.getName());
			Method loaderMethod = keeperClass.getMethod("loader", ClassLoader.class);
			loaderMethod.invoke(null, loader);
			count++;
			if((count % 1000) == 0) {
				System.gc();
				Thread.sleep(10);
				System.out.println(count);
			}
		}
	}
	
	public static class Keeper {
		private final static ThreadLocal<SoftReference<ClassLoader>> LOADER = new ThreadLocal<SoftReference<ClassLoader>>();
		
		public static void loader(ClassLoader loader) {
			LOADER.set(new SoftReference<ClassLoader>(loader));
			//LOADER.remove();
		}
	}
	
}