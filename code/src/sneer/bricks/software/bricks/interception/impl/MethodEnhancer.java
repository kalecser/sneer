package sneer.bricks.software.bricks.interception.impl;

import java.io.IOException;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import sneer.bricks.software.bricks.interception.Interceptor;
import sneer.bricks.software.bricks.interception.InterceptionRuntime;
import sneer.foundation.brickness.ClassDefinition;
import sneer.foundation.brickness.impl.Boxing;

class MethodEnhancer {

	private final String _continuationName;
	private final List<ClassDefinition> _resultingClasses;
	private final CtMethod _method;
	private final ClassPool _classPool;
	private final CtClass _containingClass;

	public MethodEnhancer(String continuationName, ClassPool classPool, CtClass containingClass, CtMethod method, List<ClassDefinition> resultingClasses) {
		_classPool = classPool;
		_containingClass = containingClass;
		_resultingClasses = resultingClasses;
		_method = method;
		_continuationName = continuationName;
	}

	public void run() {
		try {
			createDelegate();
			_resultingClasses.add(createContinuation());
			enhanceMethod();
		} catch (Exception e) {
			throw new sneer.foundation.lang.exceptions.NotImplementedYet(_method.toString(), e);
		}
	}

	private void createDelegate() throws CannotCompileException {
		_containingClass.addMethod(
				privateFinalCopyOf(_method, delegateMethodName()));
	}

	private CtMethod privateFinalCopyOf(CtMethod method, String copyName)
			throws CannotCompileException {
		CtMethod copy = CtNewMethod.copy(method, copyName, method.getDeclaringClass(), null);
		makePrivateFinal(copy);
		return copy;
	}

	private void makePrivateFinal(CtMethod copy) {
		int modifiers = Modifier.setPrivate(copy.getModifiers()) | Modifier.FINAL;
		copy.setModifiers(modifiers);
	}

	private String delegateMethodName() {
		return "$" + _method.getName();
	}
	
	private void enhanceMethod() throws CannotCompileException, NotFoundException {
		final String fullyQualifiedContinuationName = declaringClassName() + "." + _continuationName;
		_method.setBody(
					"{ " + fullyQualifiedContinuationName + " continuation = new " + fullyQualifiedContinuationName + "(this);"
					+ "Object[] args = " + argumentBoxing() + "; "
					+ "Object result = " 
						+ InterceptionRuntime.class.getName()
							+ ".dispatch("
								+ InterceptionEnhancerImpl.BRICK_METADATA_CLASS + ".BRICK, "
								+ InterceptionEnhancerImpl.BRICK_METADATA_CLASS + ".NATURE, "
								+ "this, "
								+ "\"" + _method.getName() + "\", "
								+ "args, "
								+ "continuation"
							+ "); " 
					+ (hasReturnValue() ? "return " + castReturnValue("result") + "; " : "") + " }");
	}

	private String argumentBoxing() throws NotFoundException {
		CtClass[] parameterTypes = _method.getParameterTypes();
		if (parameterTypes.length == 0)
			return "new Object[0]";
		
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < parameterTypes.length; i++) {
			
			if (code.length() > 0)
				code.append(", ");
			
			CtClass parameterType = parameterTypes[i];
			code.append(boxTo(parameterType, "$" + (i + 1)));
		}
		return "new Object[] { " + code.toString() + " };";
	}
	
	private String argumentUnboxing() throws NotFoundException {
		CtClass[] parameterTypes = _method.getParameterTypes();
		StringBuilder code = new StringBuilder();
		for (int i = 0; i < parameterTypes.length; i++) {
			
			if (code.length() > 0)
				code.append(", ");
			
			CtClass parameterType = parameterTypes[i];
			code.append(cast(parameterType, "args[" + i + "]"));
		}
		return code.toString();
	}

	private boolean hasReturnValue() {
		return !_method.getSignature().endsWith(")V");
	}

	private ClassDefinition createContinuation() throws NotFoundException,
			CannotCompileException, IOException {
		final CtClass thunkClass = _containingClass.makeNestedClass(_continuationName, true);
		thunkClass.addInterface(ctClassFor(Interceptor.Continuation.class));
		
		thunkClass.addField(CtField.make("private final " + declaringClassName() + " _target;", thunkClass));
		
		final String ctorCode = "public " + _continuationName + "(" + declaringClassName() + " target) {\n" +
			"_target = target;" + 
		"}";
		thunkClass.addConstructor(
				CtNewConstructor.make(
					ctorCode, thunkClass));
		
		String invocation = "_target." + delegateMethodName() + "(" + argumentUnboxing() + ")";
		thunkClass.addMethod(
				CtNewMethod.make(
					"public Object invoke(Object[] args) {" +
						(hasReturnValue()
							? "return " + boxReturnValue(invocation) + ";"
							: invocation + "; return null;") +
					"}", thunkClass));
		
		return new ClassDefinition(thunkClass.getName(), thunkClass.toBytecode());
	}

	private String boxReturnValue(String expression) throws NotFoundException {
		return boxTo(returnType(), expression);
	}

	private String boxTo(CtClass boxedType, String expression) {
		if (boxedType.isPrimitive())
			return Boxing.class.getName() + ".box(" + expression + ")";
		return expression;
	}

	private String castReturnValue(String expression) throws NotFoundException {
		return cast(returnType(), expression);
	}

	private String cast(CtClass toType, String expression) {
		if (toType.isPrimitive())
			return Boxing.class.getName() + ".unbox" + capitalize(toType.getSimpleName()) + "(" + expression + ")";
			
		return "((" + toType.getName() + ")" + expression + ")";
	}

	private String capitalize(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

	private CtClass returnType() throws NotFoundException {
		return _method.getReturnType();
	}

	private String declaringClassName() {
		return _method.getDeclaringClass().getName();
	}

	private CtClass ctClassFor(final Class<?> clazz) throws NotFoundException {
		return _classPool.get(clazz.getName());
	}
}