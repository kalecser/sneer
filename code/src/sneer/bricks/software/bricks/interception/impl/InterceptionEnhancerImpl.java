package sneer.bricks.software.bricks.interception.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import sneer.bricks.software.bricks.interception.InterceptionEnhancer;
import sneer.bricks.software.bricks.interception.Interceptor;
import sneer.foundation.brickness.ClassDefinition;

class InterceptionEnhancerImpl implements InterceptionEnhancer {

	private final HashSet<Class<?>> _initializedBricks = new HashSet<Class<?>>();
	
	@Override
	public List<ClassDefinition> realize(Class<?> targetBrick, Class<? extends Interceptor> interceptorClass, final ClassDefinition classDef) {
		
		ArrayList<ClassDefinition> resultingClasses = enhanceClassDefinition(classDef, targetBrick, interceptorClass);
		
		if (!_initializedBricks.contains(targetBrick)) {
			resultingClasses.add(generateBrickMetadataFor(targetBrick, interceptorClass));
			_initializedBricks.add(targetBrick);
		}
		return resultingClasses;
		
	}

	private ArrayList<ClassDefinition> enhanceClassDefinition(final ClassDefinition classDef, Class<?> targetBrick, Class<? extends Interceptor> interceptorClass) {
		ArrayList<ClassDefinition> resultingClasses = new ArrayList<ClassDefinition>();
		
        ClassReader cr = new ClassReader(classDef.bytes);            
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        
        ClassEnhancer transformer = new ClassEnhancer(cw, targetBrick, interceptorClass, resultingClasses);
		cr.accept(transformer, ClassReader.EXPAND_FRAMES);
		
		resultingClasses.add(new ClassDefinition(classDef.name, cw.toByteArray()));
		
		return resultingClasses;
	}
	
	private ClassDefinition generateBrickMetadataFor(Class<?> targetBrick, Class<? extends Interceptor> interceptorClass) {
		return new BrickMetadataEmitter(targetBrick, interceptorClass).emit();
	}	
}
