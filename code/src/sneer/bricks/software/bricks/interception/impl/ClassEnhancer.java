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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import scala.actors.threadpool.Arrays;
import sneer.bricks.software.bricks.interception.InterceptionRuntime;
import sneer.bricks.software.bricks.interception.Interceptor;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.lang.exceptions.NotImplementedYet;

final class ClassEnhancer extends ClassAdapter {
	
	private final ArrayList<ClassDefinition> _resultingClasses;
	private final ArrayList<MethodDescriptor> _interceptedMethods = new ArrayList<MethodDescriptor>();
	private Type _className;
	private final Class<?> _brick;
	private final Class<? extends Interceptor> _interceptorClass;
	
	static class MethodDescriptor {

		final int access;
		final String name;
		final String desc;
		final String signature;
		final String[] exceptions;
		final Type[] argumentTypes;
		final Type returnType;

		public MethodDescriptor(int access_, String name_, String desc_, String signature_, String[] exceptions_) {
			access = access_;
			name = name_;
			desc = desc_;
			signature = signature_;
			exceptions = exceptions_;
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
		super.visit(version, access, name, signature, superName, interfaces);
		_className = Type.getType("L" + name + ";");
		if (containsBrickInterface(interfaces))
			emitBrickMetadata();
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		if (isAccessibleInstanceMethod(access) && !isConstructor(name)) {
			_interceptedMethods.add(new MethodDescriptor(access, name, desc, signature, exceptions));
			// original method gets renamed 
			return super.visitMethod(ACC_PUBLIC, privateNameFor(name), desc, signature, exceptions);
		}
		return super.visitMethod(access, name, desc, signature, exceptions);
	}

	private String privateNameFor(String name) {
		return "$" + name;
	}
	
	@Override
	public void visitEnd() {
		
		for (MethodDescriptor im : _interceptedMethods) {
			ClassDefinition continuation = emitContinuationFor(im);
			emitMethodInterceptionFor(im, internalNameFromClassName(continuation.name));
			_resultingClasses.add(continuation);
		}
		
		super.visitEnd();
	}
	
	private void emitMethodInterceptionFor(MethodDescriptor m, String continuationClass) {
		
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
				emitAutoBoxing(mv, argumentType);
			
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

	private ClassDefinition emitContinuationFor(MethodDescriptor m) {
		String continuationName = ContinuationNameProvider.continuationNameFor(m.name);
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
		
		cw.visit(V1_6, ACC_SUPER | ACC_PUBLIC | ACC_FINAL, continuationName, null, "java/lang/Object", new String[] { Type.getInternalName(Interceptor.Continuation.class) });
		cw.visitInnerClass(continuationName, internalClassName(), continuationName, 0);
		
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
			ctor.visitFieldInsn(PUTFIELD, continuationName, fieldName(i), ctorArg.getDescriptor());
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
			invoke.visitFieldInsn(GETFIELD, continuationName, fieldName(i), ctorArg.getDescriptor());
		}
		
		invoke.visitMethodInsn(INVOKEVIRTUAL, internalClassName(), privateNameFor(m.name), m.desc);
		if (m.isVoidMethod())
			invoke.visitInsn(ACONST_NULL);
		else if (m.isPrimitiveMethod())
			emitAutoBoxing(invoke, m.returnType);
		
		invoke.visitInsn(ARETURN);
		invoke.visitMaxs(0, 0);
		invoke.visitEnd();
		
		cw.visitEnd();

		return new ClassDefinition(continuationName, cw.toByteArray());

	}

	private String fieldName(int i) {
		return "_" + i;
	}

	private void emitAutoBoxing(MethodVisitor mv, Type type) {
		if (type == Type.INT_TYPE)
			emitMethodInsn(mv, INVOKESTATIC, Integer.class, "valueOf", int.class);
		else
			throw new NotImplementedYet(type.toString());
	}
	
	private void emitUnboxing(MethodVisitor mv, Type type) {
		if (type == Type.INT_TYPE)
			emitUnboxingSequence(mv, Integer.class, "intValue");
		else
			throw new NotImplementedYet(type.toString());
	}

	private void emitUnboxingSequence(MethodVisitor mv, Class<Integer> owner, String methodName) {
		mv.visitTypeInsn(CHECKCAST, Type.getInternalName(owner));
		emitMethodInsn(mv, INVOKEVIRTUAL, owner, methodName);
	}

	private void emitMethodInsn(MethodVisitor mv, int opcode, Class<?> owner, String methodName, Class<?>... parameterTypes) {
		Method method = getMethod(owner, methodName, parameterTypes);
		mv.visitMethodInsn(opcode, Type.getInternalName(method.getDeclaringClass()), method.getName(), Type.getMethodDescriptor(method));
	}

	private String constructorDescriptor(Type[] ctorArgs) {
		return Type.getMethodDescriptor(Type.VOID_TYPE, ctorArgs);
	}

	private Type[] continuationConstructorArgTypesFor(MethodDescriptor m) {
		return Types.insertBefore(m.argumentTypes, classType());
	}

	private Type classType() {
		return _className;
	}

	private String internalClassName() {
		return _className.getInternalName();
	}
	
	private boolean isConstructor(String name) {
		return "<init>".equals(name);
	}

	private void emitBrickMetadata() {
		BrickMetadataEmitter emitter = new BrickMetadataEmitter(_brick, _interceptorClass);
		emitter.emitBrickMetadataInitializer(this);
		_resultingClasses.add(emitter.emit());
	}
	
	private boolean containsBrickInterface(String[] interfaces) {
		return Arrays.asList(interfaces).contains(Type.getInternalName(_brick)); 
	}
	
	private boolean isAccessibleInstanceMethod(int modifiers) {
		if (Modifier.isAbstract(modifiers)) return false;
		if (Modifier.isStatic(modifiers)) return false;
		if (Modifier.isPublic(modifiers)) return true;
		if (Modifier.isProtected(modifiers)) return true;
		return false;
	}
}