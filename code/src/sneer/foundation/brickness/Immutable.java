package sneer.foundation.brickness;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public abstract class Immutable {

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		
		for (Field field : fields())
			result = (prime * result) + hashCode(getFieldValue(field, this));
		
		return result;
	}

	
	@Override
	public final boolean equals(Object other) {
		if (this == other) return true;
		if (other == null) return false;
		if (getClass() != other.getClass()) return false;
	
		for (Field field : fields())
			if (!isSameFieldValue(field, other)) return false;
		
		return true;
	}

	
	@Override
	public String toString() {
		return getClass().getSimpleName() + fieldsToString();
	}


	private Field[] fields() {
		//Optimize Implement a WEAK cache using the class as key
		
		List<Field> list = new ArrayList<Field>();
		accumulateFields(list, getClass());
	    
		Field[] result = list.toArray(new Field[list.size()]);
		sort(result);
		return result;
	}

	
	private void accumulateFields(List<Field> list, Class<?> clazz) {
		if (clazz == Object.class) return;
		
		for (Field f : clazz.getDeclaredFields())
			list.add(f);
	
		accumulateFields(list, clazz.getSuperclass());
	}

	
	private void sort(Field[] fields) {
		Arrays.sort(fields, new Comparator<Field>() { @Override public int compare(Field f1, Field f2) {
			return f1.getName().compareTo(f2.getName()); 
		}});
	}

	
	private int hashCode(Object obj) {
		if (obj == null) return 0;
		return obj.hashCode();
	}

	
	private Object getFieldValue(Field field, Object object) {
		try {
			return checkForArray(field.get(object));
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Tuple classes should be public and all of their fields should be public and not static. This was not the case with: " + object.getClass() + "." + field.getName() + " Also, tuple classes declared as inner classes dont work.", e);
		}
	}

	
	private Object checkForArray(Object object) {
		if (object == null) return null;
		if (object.getClass().isArray()) throw new IllegalStateException("Tuples cannot have fields which are arrays. Use ImmutableArrays instead. Class: " + getClass());
		return object;
	}

	
	private boolean isSameFieldValue(Field field, Object other) {
		Object myValue = getFieldValue(field, this);
		Object hisValue = getFieldValue(field, other);
		
		return equals(myValue, hisValue);
	}

	
	private boolean equals(Object myValue, Object hisValue) {
		if (myValue == null) return hisValue == null;
		return myValue.equals(hisValue);
	}

	
	private String fieldsToString() {
		StringBuilder result = new StringBuilder();
		for (Field field : fields()) {
			result.append("|");
			result.append(field.getName());
			result.append(":");
			result.append(getFieldValue(field, this));
		}
		return result.toString();
	}

}
