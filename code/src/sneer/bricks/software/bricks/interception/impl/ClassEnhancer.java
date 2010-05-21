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

final class ClassEnhancer extends ClassAdapter {
	
	private final ArrayList<ClassDefinition> _resultingClasses;
	private final ArrayList<MethodDescriptor> _interceptedMethods = new ArrayList<MethodDescriptor>();
	private Type _className;
	private final Class<?> _brick;
	private final Class<? extends Interceptor> _interceptorClass;
	
	static class MethodDescriptor {

		final String name;
		final String desc;
		final String signature;
		final String[] exceptions;
		final Type[] argumentTypes;
		final Type returnType;

		public MethodDescriptor(String name_, String desc_, String signature_, String[] exceptions_) {
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
			new BrickMetadataEmitter(_brick, _interceptorClass).emitBrickMetadataInitializer(this);
	}
	
	private boolean containsBrickInterface(String[] interfaces) {
		return Arrays.asList(interfaces).contains(Type.getInternalName(_brick)); 
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		
		if (isAccessibleInstanceMethod(access) && !isConstructor(name)) {
			_interceptedMethods.add(new MethodDescriptor(name, desc, signature, exceptions));
			// original method gets renamed 
			return super.visitMethod(access, privateNameFor(name), desc, signature, exceptions);
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
		
		MethodVisitor mv = super.visitMethod(ACC_PUBLIC, m.name, m.desc, m.signature, m.exceptions);
		mv.visitCode();
		mv.visitFieldInsn(GETSTATIC, BrickMetadataDefinition.CLASS_NAME, BrickMetadataDefinition.Fields.BRICK, BrickMetadataDefinition.Fields.BRICK_TYPE);
		mv.visitFieldInsn(GETSTATIC, BrickMetadataDefinition.CLASS_NAME, BrickMetadataDefinition.Fields.INTERCEPTOR, BrickMetadataDefinition.Fields.INTERCEPTOR_TYPE);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitLdcInsn(m.name);
		
		mv.visitLdcInsn(m.argumentTypes.length);
		mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
		
		for (int i=0; i<m.argumentTypes.length; ++i) {
			mv.visitInsn(DUP);
			mv.visitLdcInsn(i);
			mv.visitVarInsn(ALOAD, i + 1);
			mv.visitInsn(AASTORE);
		}
		
		mv.visitTypeInsn(NEW, continuationInternalName);
		mv.visitInsn(DUP);
		
		mv.visitVarInsn(ALOAD, 0);

		for (int i=0; i<m.argumentTypes.length; ++i)
			mv.visitVarInsn(ALOAD, i + 1);
		
		mv.visitMethodInsn(INVOKESPECIAL, continuationInternalName, "<init>", constructorDescriptor(continuationConstructorArgTypesFor(m)));
		mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(InterceptionRuntime.class), "dispatch", Type.getMethodDescriptor(interceptionRuntimeDispatchMethod()));
		
		// box/unbox return value here
		if (m.isVoidMethod()) {
			mv.visitInsn(POP);
			mv.visitInsn(RETURN);
		} else {
			mv.visitTypeInsn(CHECKCAST, m.returnType.getInternalName());
			mv.visitInsn(ARETURN);
		}
		
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		
	}

	private Method interceptionRuntimeDispatchMethod() {
		try {
			return InterceptionRuntime.class.getMethod(
					"dispatch",
					Class.class,
					Interceptor.class,
					Object.class,
					String.class,
					Object[].class,
					Interceptor.Continuation.class);
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
		
		String self = "self";
		cw.visitField(ACC_FINAL + ACC_SYNTHETIC, self, classDescriptor(), null, null).visitEnd();
		
		for (Type type : m.argumentTypes)
			cw.visitField(ACC_FINAL + ACC_SYNTHETIC, self, type.getDescriptor(), null, null).visitEnd();
		
		Type[] ctorArgs = continuationConstructorArgTypesFor(m);
		MethodVisitor ctor = cw.visitMethod(ACC_PUBLIC, "<init>", constructorDescriptor(ctorArgs), null, null);
		ctor.visitCode();
		
		for (int i = 0; i < ctorArgs.length; i++) {
			Type ctorArg = ctorArgs[i];
			ctor.visitVarInsn(ALOAD, 0);
			ctor.visitVarInsn(ALOAD, i + 1);
			ctor.visitFieldInsn(PUTFIELD, continuationName, self, ctorArg.getDescriptor());
		}
		
		ctor.visitVarInsn(ALOAD, 0);
		ctor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		ctor.visitInsn(RETURN);
		ctor.visitMaxs(0, 0);
		ctor.visitEnd();
		
		MethodVisitor invoke = cw.visitMethod(ACC_PUBLIC, "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		invoke.visitCode();
		invoke.visitVarInsn(ALOAD, 0);
		invoke.visitFieldInsn(GETFIELD, continuationName, self, classDescriptor());
		
		for (Type argType : m.argumentTypes) {
			invoke.visitVarInsn(ALOAD, 0);
			invoke.visitFieldInsn(GETFIELD, continuationName, self, argType.getDescriptor());
		}
		
		invoke.visitMethodInsn(INVOKEVIRTUAL, internalClassName(), privateNameFor(m.name), m.desc);
		if (m.isVoidMethod())
			invoke.visitInsn(ACONST_NULL);
		invoke.visitInsn(ARETURN);
		invoke.visitMaxs(0, 0);
		invoke.visitEnd();
		
		cw.visitEnd();

		return new ClassDefinition(continuationName, cw.toByteArray());

	}

	private String constructorDescriptor(Type[] ctorArgs) {
		return Type.getMethodDescriptor(Type.VOID_TYPE, ctorArgs);
	}

	private Type[] continuationConstructorArgTypesFor(MethodDescriptor m) {
		return insertBefore(m.argumentTypes, classType());
	}

	private Type[] insertBefore(Type[] array, Type prefix) {
		int newSize = array.length + 1;
		ArrayList<Type> list = new ArrayList<Type>(newSize);
		list.add(prefix);
		for (Type type : array) list.add(type);
		return list.toArray(new Type[newSize]);
	}

	private Type classType() {
		return _className;
	}

	private String internalClassName() {
		return _className.getInternalName();
	}

	private String classDescriptor() {
		return _className.getDescriptor();
	}

	private boolean isConstructor(String name) {
		return "<init>".equals(name);
	}
	
	private boolean isAccessibleInstanceMethod(int modifiers) {
		if (Modifier.isAbstract(modifiers)) return false;
		if (Modifier.isStatic(modifiers)) return false;
		if (Modifier.isPublic(modifiers)) return true;
		if (Modifier.isProtected(modifiers)) return true;
		return false;
	}
}