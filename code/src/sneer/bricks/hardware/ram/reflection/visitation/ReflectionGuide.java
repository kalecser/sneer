package sneer.bricks.hardware.ram.reflection.visitation;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class ReflectionGuide {

	private final Set<Object> _attractionsVisited = new HashSet<Object>();
	private final LinkedList<Tour> _toursToVisit = new LinkedList<Tour>();

	
	public void guide(ReflectionVisitor visitor, Object start) {
		visitLater(new Tour(start));
		while (!_toursToVisit.isEmpty())
			guide(visitor, _toursToVisit.removeFirst());
	}

	
	protected void guide(ReflectionVisitor visitor, Tour tour) {
		Object attraction = tour._attraction;

		if (!_attractionsVisited.add(attraction)) return;

		if (!visitor.visit(tour)) return;

		Class<?> type = attraction.getClass();
		visitFieldsLater(tour, type);
	}

	
	protected void visitFieldsLater(Tour tour, Class<?> type) {
		for (Field field : type.getDeclaredFields())
			visitLater(tour, field);

		Class<?> superclass = type.getSuperclass();
		if (superclass == null) return;
		visitFieldsLater(tour, superclass);
	}

	
	private void visitLater(Tour tour, Field field) {
		if (isStatic(field)) return;
		
		field.setAccessible(true);

		Object nextAttraction = getValue(field, tour._attraction);
		visitLater(tour.fork(field.getName(), nextAttraction));
	}

	
	private boolean isStatic(Field field) {
		return Modifier.isStatic(field.getModifiers());
	}

	
	private Object getValue(Field field, Object object) {
		try {
			return field.get(object);
		} catch (Exception e) {
			throw new IllegalStateException("Exception thrown while getting field value.", e);
		}
	}

	
	protected void visitArrayLater(Tour tour, Object array) {
		for (int i = 0; i < Array.getLength(array); i++)
			visitLater(tour.fork("" + i, Array.get(array, i)));
	}

	
	protected void visitLater(Tour tour) {
		Object attraction = tour._attraction;
		if (attraction == null) return;
		
		Class<?> type = attraction.getClass();
		if (isDeadEnd(type)) return;

		if (type.isArray()) {
			visitArrayLater(tour, attraction);
			return;
		}

		_toursToVisit.add(tour);
	}

	
	protected boolean isDeadEnd(Class<?> type) {
		if (type == String.class) return true;

		if (type.isPrimitive()) return true;
		if (type == Boolean.class) return true;
		if (type == Integer.class) return true;
		if (type == Long.class) return true;
		if (type == Float.class) return true;
		if (type == Double.class) return true;
		if (type == Byte.class) return true;
		if (type == Character.class) return true;
		
		if (type == Date.class) return true;

		if (type.isArray() && isDeadEnd(type.getComponentType())) return true;
		
		return false;
	}

}