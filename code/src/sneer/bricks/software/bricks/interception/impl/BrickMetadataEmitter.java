package sneer.bricks.software.bricks.interception.impl;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import basis.brickness.ClassDefinition;
import basis.environments.Environments;

import sneer.bricks.software.bricks.interception.Interceptor;

class BrickMetadataEmitter {
	
	private final Class<?> _targetBrick;
	private final Class<? extends Interceptor> _interceptorClass;

	public BrickMetadataEmitter(Class<?> targetBrick, Class<? extends Interceptor> interceptorClass) {
		_targetBrick = targetBrick;
		_interceptorClass = interceptorClass;
	}

	public ClassDefinition emitBrickMetadataClass() {
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);

		cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, BrickMetadataDefinition.CLASS_NAME, null, "java/lang/Object", null);
		cw.visitField(ACC_PUBLIC + ACC_STATIC, BrickMetadataDefinition.Fields.BRICK, BrickMetadataDefinition.Fields.BRICK_TYPE, null, null).visitEnd();
		cw.visitField(ACC_PUBLIC + ACC_STATIC, BrickMetadataDefinition.Fields.INTERCEPTOR, BrickMetadataDefinition.Fields.INTERCEPTOR_TYPE, null, null).visitEnd();
		
		writeEmptyConstructor(cw);
		
		cw.visitEnd();
		
		return new ClassDefinition(BrickMetadataDefinition.CLASS_NAME, cw.toByteArray());
	}

	public void emitBrickMetadataInitializationCode(MethodVisitor mv) {
		
		// BrickMetadata.BRICK = targetBrick;
		mv.visitLdcInsn(Type.getType(_targetBrick));
		mv.visitFieldInsn(PUTSTATIC, BrickMetadataDefinition.CLASS_NAME, BrickMetadataDefinition.Fields.BRICK, BrickMetadataDefinition.Fields.BRICK_TYPE);
		
		// BrickMetadata.INTERCEPTOR = my(interceptorClass); 
		mv.visitLdcInsn(Type.getType(_interceptorClass));
		mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Environments.class), "my", "(Ljava/lang/Class;)Ljava/lang/Object;");
		mv.visitTypeInsn(CHECKCAST, BrickMetadataDefinition.Fields.INTERCEPTOR_TYPE_NAME);		
		mv.visitFieldInsn(PUTSTATIC, BrickMetadataDefinition.CLASS_NAME, BrickMetadataDefinition.Fields.INTERCEPTOR, BrickMetadataDefinition.Fields.INTERCEPTOR_TYPE);
	}
	
	private void writeEmptyConstructor(ClassWriter cw) {
		MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		mv.visitInsn(RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
	}
}
