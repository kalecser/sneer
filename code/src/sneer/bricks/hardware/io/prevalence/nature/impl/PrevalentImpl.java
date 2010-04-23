package sneer.bricks.hardware.io.prevalence.nature.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodAdapter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.hardware.io.log.Logger;
import sneer.bricks.hardware.io.prevalence.nature.Prevalent;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.Producer;

class PrevalentImpl implements Prevalent {
	
	private final class InstantiationProcessor extends ClassAdapter {
		
		private final class MethodProcessor extends MethodAdapter {
			
			private boolean _new;
			private boolean _dup;

			private MethodProcessor(MethodVisitor delegate) {
				super(delegate);
			}

			@Override
			public void visitTypeInsn(int opcode, String type) {
				super.visitTypeInsn(opcode, type);
				if (opcode == Opcodes.NEW)
					_new = true;
				else
					matchFailed();
			}

			@Override
			public void visitInsn(int opcode) {
				super.visitInsn(opcode);
				if (_new)
					_dup = opcode == Opcodes.DUP;
				else
					matchFailed();
			}

			@Override
			public void visitMethodInsn(int opcode, String owner,
					String name, String desc) {
				super.visitMethodInsn(opcode, owner, name, desc);
				
				if (_dup && opcode == Opcodes.INVOKESPECIAL)
					registerInstantiation();
				
				matchFailed();
			}

			private void matchFailed() {
				_new = _dup = false;
			}
			
			@Override
			public void visitFieldInsn(int opcode, String owner, String name,
					String desc) {
				super.visitFieldInsn(opcode, owner, name, desc);
				matchFailed();
			}

			private void registerInstantiation() {
				throw new sneer.foundation.lang.exceptions.NotImplementedYet(); // Implement
			}
		}

		private InstantiationProcessor(ClassVisitor delegate) {
			super(delegate);
		}
		
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
			return new MethodProcessor(super.visitMethod(access, name, desc, signature, exceptions));
		}
	}

	private Prevayler _prevayler;

	@SuppressWarnings("unused")	private final WeakContract _refToAvoidGc;


	{
		_refToAvoidGc = my(Threads.class).crashing().addPulseReceiver(new Closure() { @Override public void run() {
			crash();
		}});
	}
	
	
	@Override
	public List<ClassDefinition> realize(ClassDefinition classDef) {
		
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		ClassReader reader = new ClassReader(classDef.bytes);
		reader.accept(new InstantiationProcessor(writer), 0);
		return Arrays.asList(new ClassDefinition(classDef.name, writer.toByteArray()));
	}

	boolean _prevailing;
	
	@Override
	public synchronized <T> T instantiate(final Class<T> brick, Class<T> implClass, final Producer<T> producer) {
		
		if (null == _prevayler)
			_prevayler = createPrevayler(prevalenceBase());
		
		Producer<T> nonPrevailing = new Producer<T>() { @Override public T produce() throws RuntimeException {			
			
			PrevalentBuilding building = (PrevalentBuilding) _prevayler.prevalentSystem();
			T existing = building.brick(brick);
			T instance = existing != null
				? existing
				: (T)_prevayler.execute(new InstantiateBrick<T>(brick, producer));
			
			return instance;
		}};
		
		return Bubble.wrap(InPrevailingState.produce(producer, nonPrevailing), _prevayler);
	}

	private <T> File prevalenceBase() {
		return my(FolderConfig.class).storageFolderFor(Prevalent.class);
	}

	private Prevayler createPrevayler(File prevalenceBase) {
		PrevaylerFactory factory = createPrevaylerFactory(new PrevalentBuilding(), prevalenceBase);
		try {
			return factory.create();
		} catch (IOException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		} catch (ClassNotFoundException e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(e); // Fix Handle this exception.
		}
	}

	private PrevaylerFactory createPrevaylerFactory(Object system, File prevalenceBase) {
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(system);
		factory.configurePrevalenceDirectory(prevalenceBase.getAbsolutePath());
		factory.configureTransactionFiltering(false);
		factory.configureJournalSerializer("xstreamjournal", new SerializerAdapter());
		return factory;
	}
	
	private void crash() {
		try {
			_prevayler.close();
		} catch (IOException e) {
			my(Logger.class).log("Exception closing prevayler: " + e);
		}
	}
}