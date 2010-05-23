package sneer.bricks.software.bricks.interception.impl;

import java.util.ArrayList;

import org.objectweb.asm.Type;

public class Types {

	static boolean isPrimitive(Type type) {
		return type.getDescriptor().length() == 1;
	}

	static Type[] insertBefore(Type[] array, Type prefix) {
		int newSize = array.length + 1;
		ArrayList<Type> list = new ArrayList<Type>(newSize);
		list.add(prefix);
		for (Type type : array) list.add(type);
		return list.toArray(new Type[newSize]);
	}

}
