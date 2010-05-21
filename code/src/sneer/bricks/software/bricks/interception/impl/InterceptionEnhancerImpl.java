package sneer.bricks.software.bricks.interception.impl;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import sneer.bricks.software.bricks.interception.InterceptionEnhancer;
import sneer.bricks.software.bricks.interception.Interceptor;
import sneer.foundation.brickness.ClassDefinition;

class InterceptionEnhancerImpl implements InterceptionEnhancer {

	@Override
	public List<ClassDefinition> realize(Class<?> targetBrick, Class<? extends Interceptor> interceptorClass, final ClassDefinition classDef) {
		
		ArrayList<ClassDefinition> resultingClasses = new ArrayList<ClassDefinition>();
		
		ClassReader cr = new ClassReader(classDef.bytes);            
		ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		
		ClassEnhancer transformer = new ClassEnhancer(cw, targetBrick, interceptorClass, resultingClasses);
		cr.accept(transformer, ClassReader.EXPAND_FRAMES);
		
		resultingClasses.add(new ClassDefinition(classDef.name, cw.toByteArray()));
		
		return resultingClasses;
		
	}
}
