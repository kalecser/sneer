package sneer.bricks.software.bricks.compiler.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import sneer.bricks.software.bricks.compiler.BrickCompiler;
import sneer.bricks.software.code.compilers.java.JavaCompiler;
import sneer.foundation.lang.exceptions.NotImplementedYet;

class BrickCompilerImpl implements BrickCompiler {

	@Override
	public void compile(File srcFolder, File destinationFolder) {
		/*
		 	compile all interfaces to stage bin
			compile each brick to tmp folder with stage bin in the classpath
			move all bricks to stage bin
		 */
		try {
			my(JavaCompiler.class).compile(srcFolder, destinationFolder, destinationFolder);
		} catch (Exception e) {
			throw new NotImplementedYet(e);
		}
	}

}
