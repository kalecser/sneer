package sneer.bricks.hardware.ram.reflection.visitation.impl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import sneer.bricks.hardware.ram.reflection.visitation.ReflectionGuide;
import sneer.bricks.hardware.ram.reflection.visitation.ReflectionVisitor;

class ReflectionGuideImpl implements ReflectionGuide {

	private final Set<Object> _attractionsVisited = new HashSet<Object>();
	private final LinkedList<TourImpl> _toursToVisit = new LinkedList<TourImpl>();

	
	@Override
	public void guide(ReflectionVisitor visitor, Object start) {
		visitLater(new TourImpl(start));
		while (!_toursToVisit.isEmpty())
			guide(visitor, _toursToVisit.removeFirst());
	}

	
	protected void guide(ReflectionVisitor visitor, TourImpl tour) {
		Object attraction = tour.target();

		if (!_attractionsVisited.add(attraction)) return;

		if (!visitor.visit(tour)) return;

		Class<?> type = attraction.getClass();
		visitFieldsLater(tour, type);
	}

	
	protected void visitFieldsLater(TourImpl tour, Class<?> type) {
		for (Field field : type.getDeclaredFields())
			visitLater(tour, field);

		Class<?> superclass = type.getSuperclass();
		if (superclass == null) return;
		visitFieldsLater(tour, superclass);
	}

	
	private void visitLater(TourImpl tour, Field field) {
		if (isStatic(field)) return;
		
		field.setAccessible(true);

		Object nextAttraction = getValue(field, tour.target());
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

	
	protected void visitArrayLater(TourImpl tour, Object array) {
		for (int i = 0; i < Array.getLength(array); i++)
			visitLater(tour.fork("" + i, Array.get(array, i)));
	}

	
	protected void visitLater(TourImpl tour) {
		Object attraction = tour.target();
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