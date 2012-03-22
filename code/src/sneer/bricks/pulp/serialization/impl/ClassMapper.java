/**
 * 
 */
package sneer.bricks.pulp.serialization.impl;

import static basis.environments.Environments.my;

import basis.brickness.BrickSerializationMapper;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

final class ClassMapper extends MapperWrapper {
	
	static private BrickSerializationMapper mapper = my(BrickSerializationMapper.class);

	
	ClassMapper() {
		super(new XStream().getMapper());  //Refactor: Better ideas?
	}

	
	@SuppressWarnings("rawtypes")
	@Override
	public String serializedClass(Class type) {
		return type == null
			? super.serializedClass(type)
			: mapper.serializationHandleFor(type);
	}

	
	@SuppressWarnings("rawtypes")
	@Override
	public Class realClass(String elementName) {
		try {
			return mapper.classGiven(elementName);
		} catch (Exception e) { //Refactor Why not catch only ClassNotFound? Avoid using exceptions. Very slow.
			return super.realClass(elementName);
		}
	}
}