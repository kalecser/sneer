package sneer.bricks.software.code.compilers.classpath.impl;

import java.io.File;

class SimpleClasspath extends JarBasedClasspath {

	private static final File RT_JAR = new File(System.getProperty("java.home")+File.separator+"lib"+File.separator+"rt.jar"); 

	SimpleClasspath() {
		super(RT_JAR);
	}
}
