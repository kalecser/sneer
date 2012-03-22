package sneer.bricks.software.code.metaclass.asm;

import java.io.File;

import basis.brickness.Brick;


@Brick
public interface ASM {

	ClassReader newClassReader(File classFile);
	Opcodes opcodes();
	
	interface Opcodes{
		int accInterface();
	}

}
