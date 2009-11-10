package sneer.foundation.brickness.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import sneer.foundation.brickness.Brick;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.brickness.LoadTimeNature;
import sneer.foundation.brickness.RuntimeNature;

public class RuntimeNatureEnhancer implements LoadTimeNature {

	public static final String BRICK_METADATA_CLASS = "natures.runtime.BrickMetadata";
	private final ClassPool classPool = new ClassPool(true);
	private int _continuations;

	public RuntimeNatureEnhancer() {
	}

	@Override
	public List<ClassDefinition> realize(final ClassDefinition classDef) {
		
		final ArrayList<ClassDefinition> result = new ArrayList<ClassDefinition>();
		try {
			final CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classDef.bytes));
			CtClass metadata = null;
			try {
				metadata = defineBrickMetadata(ctClass);
				if (isBrickImplementation(ctClass)) {
					result.add(toClassDefinition(metadata));
					introduceMetadataInitializer(ctClass);
				}
				enhanceMethods(ctClass, result);
				result.add(toClassDefinition(ctClass));
			} finally {
				ctClass.detach();
				if(metadata != null)
					metadata.detach();
			}
			return result;
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		} catch (CannotCompileException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		} catch (NotFoundException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

	private void introduceMetadataInitializer(CtClass brickClass) throws NotFoundException {
		try {
			CtConstructor initializer = brickClass.makeClassInitializer();
			initializer.insertAfter(BRICK_METADATA_CLASS + ".BRICK = " + brickInterface(brickClass).getName() + ".class;");
			initializer.insertAfter(BRICK_METADATA_CLASS + ".NATURES = " + RuntimeNatureDispatcher.class.getName() + ".runtimeNaturesFor(" + BRICK_METADATA_CLASS + ".BRICK);");
		} catch (CannotCompileException e) {
			throw new IllegalStateException(e);
		}
	}

	private CtClass brickInterface(CtClass ctClass) throws NotFoundException {
		for (CtClass intrface : ctClass.getInterfaces())
			if (isBrickInterface(intrface))
				return intrface;
		return null;
	}

	private CtClass defineBrickMetadata(@SuppressWarnings("unused") CtClass brickClass) {
		CtClass metadata = classPool.makeClass(BRICK_METADATA_CLASS);
		metadata.setModifiers(javassist.Modifier.PUBLIC);
		try {
			metadata.addField(CtField.make("public static " + Class.class.getName() + " BRICK;", metadata));
			metadata.addField(CtField.make("public static " + RuntimeNature.class.getName() + "[] NATURES;", metadata));
			
			return metadata;
		} catch (CannotCompileException e) {
			throw new IllegalStateException(e);
		}
	}

	private boolean isBrickImplementation(CtClass ctClass) throws NotFoundException {
		return brickInterface(ctClass) != null;
	}

	private boolean isBrickInterface(CtClass intrface) {
		for (Object annotation : intrface.getAvailableAnnotations())
			if (annotation instanceof Brick)
				return true;
		return false;
	}

	private void enhanceMethods(final CtClass ctClass,
			final ArrayList<ClassDefinition> result) {
		for (CtMethod m : ctClass.getDeclaredMethods()) {
			if (Modifier.isStatic(m.getModifiers()))
				continue;
			new RuntimeNatureMethodEnhancer(continuationNameFor(m), classPool, ctClass, m, result).run();
		}
	}

	private String continuationNameFor(CtMethod m) {
		return m.getName() + "$" + (++_continuations);
	}

	public static ClassDefinition toClassDefinition(final CtClass ctClass)
			throws CannotCompileException {
		try {
			return new ClassDefinition(ctClass.getName(), ctClass.toBytecode());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
