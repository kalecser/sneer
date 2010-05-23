/**
 * 
 */
package sneer.bricks.software.bricks.interception.impl;

import static org.objectweb.asm.Opcodes.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import sneer.bricks.software.bricks.interception.Boxing;
import sneer.bricks.software.bricks.interception.InterceptionRuntime;
import sneer.bricks.software.bricks.interception.Interceptor;
import sneer.foundation.brickness.ClassDefinition;

final class ClassEnhancer extends ClassAdapter {
	
	private final ArrayList<ClassDefinition> _resultingClasses;
	private final ArrayList<InterceptedMethod> _interceptedMethods = new ArrayList<InterceptedMethod>();
	private Type _classType;
	private final Class<?> _brick;
	private final Class<? extends Interceptor> _interceptorClass;
	private BrickMetadataEmitter _brickMetadataEmitter;
	private boolean _usingExistingInitializer;
	
	static class InterceptedMethod {

		final int access;
		final String name;
		final String desc;
		final String signature;
		final String[] exceptions;
		final Type[] argumentTypes;
		final Type returnType;
		final String implName;

		public InterceptedMethod(int access_, String name_, String desc_, String signature_, String[] exceptions_, String implName_) {
			access = access_;
			name = name_;
			desc = desc_;
			signature = signature_;
			exceptions = exceptions_;
			implName = implName_;
			argumentTypes = Type.getArgumentTypes(desc);
			returnType = Type.getReturnType(desc);
		}
		
		boolean isVoidMethod() {
			return returnType == Type.VOID_TYPE;
		}

		public boolean isPrimitiveMethod() {
			return Types.isPrimitive(returnType);
		}
	}

	ClassEnhancer(ClassVisitor delegate, Class<?> brick, Class<? extends Interceptor> interceptorClass, ArrayList<ClassDefinition> resultingClasses) {
		super(delegate);
		_brick = brick;
		_interceptorClass = interceptorClass;
		_resultingClasses = resultingClasses;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if (_classType != null)
			throw new IllegalStateException();
		super.visit(version, access, name, signature, superName, interfaces);
		_classType = typeFromInternalName(name);
		if (containsBrickInterface(interfaces))
			emitBrickMetadata();
	}

	private Type typeFromInternalName(String name) {
		return Type.getType("L" + name + ";");
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		if (isAccessibleInstanceMethod(access) && !isConstructor(name)) {
			// original method gets renamed 
			String implName = uniqueName(name);
			_interceptedMethods.add(new InterceptedMethod(access, name, desc, signature, exceptions, implName));
			return super.visitMethod(ACC_FINAL, implName, desc, signature, exceptions);
		}
		
		MethodVisitor originalMethodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
		if (name.equals("<clinit>") && _brickMetadataEmitter != null) {
			_usingExistingInitializer = true;
			return prependBrickMetadataInitializationCode(originalMethodVisitor);
		}
		
		return originalMethodVisitor;
	}

	private MethodVisitor prependBrickMetadataInitializationCode(MethodVisitor originalMethodVisitor) {
		return new MethodAdapter(originalMethodVisitor) {
			@Override
			public void visitCode() {
				super.visitCode();
//				_brickMetadataEmitter.emitBrickMetadataInitializationCode(this);
			}
		};
	}

	@Override
	public void visitEnd() {
		
		if (_brickMetadataEmitter != null && !_usingExistingInitializer)
			_brickMetadataEmitter.emitBrickMetadataInitializer(this);
		
		for (InterceptedMethod im : _interceptedMethods) {
			ClassDefinition continuation = emitContinuationFor(im);
			emitMethodInterceptionFor(im, internalNameFromClassName(continuation.name));
			_resultingClasses.add(continuation);
		}
		
		super.visitEnd();
	}
	
	private void emitMethodInterceptionFor(InterceptedMethod m, String continuationClass) {
		
		String continuationInternalName = internalNameFromClassName(continuationClass);
		
		MethodVisitor mv = super.visitMethod(m.access, m.name, m.desc, m.signature, m.exceptions);
		mv.visitCode();
		
		// brick
		mv.visitFieldInsn(GETSTATIC, BrickMetadataDefinition.CLASS_NAME, BrickMetadataDefinition.Fields.BRICK, BrickMetadataDefinition.Fields.BRICK_TYPE);
		
		// interceptor
		mv.visitFieldInsn(GETSTATIC, BrickMetadataDefinition.CLASS_NAME, BrickMetadataDefinition.Fields.INTERCEPTOR, BrickMetadataDefinition.Fields.INTERCEPTOR_TYPE);
		
		// targetObject
		mv.visitVarInsn(ALOAD, 0);
		
		// methodName
		mv.visitLdcInsn(m.name);
		
		// args
		mv.visitLdcInsn(m.argumentTypes.length);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		
		for (int i=0; i<m.argumentTypes.length; ++i) {
			mv.visitInsn(DUP);
			mv.visitLdcInsn(i);
			Type argumentType = m.argumentTypes[i];
			emitLoad(mv, argumentType, i + 1);
			if (Types.isPrimitive(argumentType))
				emitBoxing(mv, argumentType);
			
			mv.visitInsn(AASTORE);
		}
		
		// continuation
		mv.visitTypeInsn(NEW, continuationInternalName);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		for (int i=0; i<m.argumentTypes.length; ++i)
			emitLoad(mv, m.argumentTypes[i], i + 1);
		
		mv.visitMethodInsn(INVOKESPECIAL, continuationInternalName, "<init>", constructorDescriptor(continuationConstructorArgTypesFor(m)));
		
		// InterceptionRuntime.dispatch(...)
		mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(InterceptionRuntime.class), "dispatch", Type.getMethodDescriptor(interceptionRuntimeDispatchMethod()));
		
		if (m.isVoidMethod()) {
			mv.visitInsn(POP);
			mv.visitInsn(RETURN);
		} else if (m.isPrimitiveMethod()) {
			emitUnboxing(mv, m.returnType);
			mv.visitInsn(m.returnType.getOpcode(IRETURN));
		} else {
			mv.visitTypeInsn(CHECKCAST, m.returnType.getInternalName());
			mv.visitInsn(ARETURN);
		}
		
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		
	}

	private void emitLoad(MethodVisitor mv, Type argumentType, int local) {
		if (Types.isPrimitive(argumentType))
			mv.visitVarInsn(argumentType.getOpcode(ILOAD), local);
		else
			mv.visitVarInsn(ALOAD, local);
	}

	private Method interceptionRuntimeDispatchMethod() {
		Class<?> klass = InterceptionRuntime.class;
		return getMethod(klass, "dispatch",
				Class.class,
				Interceptor.class,
				Object.class,
				String.class,
				Object[].class,
				Interceptor.Continuation.class);
	}

	private Method getMethod(Class<?> klass, String methodName,
			Class<?>... parameterTypes) {
		try {
			return klass.getMethod(methodName, parameterTypes);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private String internalNameFromClassName(String className) {
		return className.replace('.', '/');
	}

	private ClassDefinition emitContinuationFor(InterceptedMethod m) {
		String continuationInternalName = internalClassName() + "$" + uniqueName(m.name);
		
		super.visitInnerClass(continuationInternalName, internalClassName(), null, 0);
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		cw.visit(V1_6, ACC_SUPER | ACC_PUBLIC | ACC_FINAL, continuationInternalName, null, "java/lang/Object", new String[] { Type.getInternalName(Interceptor.Continuation.class) });
		cw.visitOuterClass(internalClassName(), m.name, m.desc);
		
		Type[] ctorArgs = continuationConstructorArgTypesFor(m);
		
		for (int i = 0; i < ctorArgs.length; i++) {
			Type ctorArg = ctorArgs[i];
			cw.visitField(ACC_FINAL + ACC_SYNTHETIC, fieldName(i), ctorArg.getDescriptor(), null, null).visitEnd();
		}
		
		MethodVisitor ctor = cw.visitMethod(ACC_PUBLIC, "<init>", constructorDescriptor(ctorArgs), null, null);
		ctor.visitCode();
		
		for (int i = 0; i < ctorArgs.length; i++) {
			Type ctorArg = ctorArgs[i];
			ctor.visitVarInsn(ALOAD, 0);
			emitLoad(ctor, ctorArg, i + 1);
			ctor.visitFieldInsn(PUTFIELD, continuationInternalName, fieldName(i), ctorArg.getDescriptor());
		}
		
		ctor.visitVarInsn(ALOAD, 0);
		ctor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		ctor.visitInsn(RETURN);
		ctor.visitMaxs(0, 0);
		ctor.visitEnd();
		
		MethodVisitor invoke = cw.visitMethod(ACC_PUBLIC, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		invoke.visitCode();
		
		for (int i = 0; i < ctorArgs.length; i++) {
			Type ctorArg = ctorArgs[i];
			invoke.visitVarInsn(ALOAD, 0);
			invoke.visitFieldInsn(GETFIELD, continuationInternalName, fieldName(i), ctorArg.getDescriptor());
		}
		
		invoke.visitMethodInsn(INVOKEVIRTUAL, internalClassName(), m.implName, m.desc);
		if (m.isVoidMethod())
			invoke.visitInsn(ACONST_NULL);
		else if (m.isPrimitiveMethod())
			emitBoxing(invoke, m.returnType);
		
		invoke.visitInsn(ARETURN);
		invoke.visitMaxs(0, 0);
		invoke.visitEnd();
		
		cw.visitEnd();

		return new ClassDefinition(typeFromInternalName(continuationInternalName).getClassName(), cw.toByteArray());

	}

	private String uniqueName(String name) {
		return UniqueNameProvider.uniqueName(name);
	}
	
	private String fieldName(int i) {
		return "_" + i;
	}

	private void emitBoxing(MethodVisitor mv, Type type) {
		emitAutoBoxingInsn(mv, "box", type, Type.getType(Object.class));
	}
	
	private void emitUnboxing(MethodVisitor mv, Type type) {
		emitAutoBoxingInsn(mv, "unbox" + capitalize(type.getClassName()), Type.getType(Object.class), type);
	}

	private void emitAutoBoxingInsn(MethodVisitor mv, String methodName, Type argType, Type returnType) {
		mv.visitMethodInsn(INVOKESTATIC, getInternalName(Boxing.class), methodName, Type.getMethodDescriptor(returnType, new Type[] { argType }));
	}

	private String capitalize(String className) {
		return className.substring(0, 1).toUpperCase().concat(className.substring(1));
	}

	private String getInternalName(Class<?> klass) {
		return Type.getInternalName(klass);
	}

	private String constructorDescriptor(Type[] ctorArgs) {
		return Type.getMethodDescriptor(Type.VOID_TYPE, ctorArgs);
	}

	private Type[] continuationConstructorArgTypesFor(InterceptedMethod m) {
		return Types.insertBefore(m.argumentTypes, classType());
	}

	private Type classType() {
		return _classType;
	}

	private String internalClassName() {
		return _classType.getInternalName();
	}
	
	private boolean isConstructor(String name) {
		return "<init>".equals(name);
	}

	private void emitBrickMetadata() {
		_brickMetadataEmitter = new BrickMetadataEmitter(_brick, _interceptorClass);
		_resultingClasses.add(_brickMetadataEmitter.emit());
	}
	
	private boolean containsBrickInterface(String[] interfaces) {
		return java.util.Arrays.asList(interfaces).contains(Type.getInternalName(_brick)); 
	}
	
	private boolean isAccessibleInstanceMethod(int modifiers) {
		if (Modifier.isAbstract(modifiers)) return false;
		if (Modifier.isStatic(modifiers)) return false;
		if (Modifier.isPublic(modifiers)) return true;
		if (Modifier.isProtected(modifiers)) return true;
		return false;
	}
}