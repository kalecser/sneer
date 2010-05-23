package sneer.bricks.software.bricks.interception.impl;

import org.objectweb.asm.Type;

import sneer.bricks.software.bricks.interception.Interceptor;


public class BrickMetadataDefinition {

	public static final String CLASS_NAME = "BrickMetadata";
	
	public static class Fields {
		public static final String BRICK = "BRICK";
		public static final String BRICK_TYPE = "Ljava/lang/Class;";
		public static final String INTERCEPTOR = "INTERCEPTOR";
		public static final String INTERCEPTOR_TYPE = Type.getDescriptor(Interceptor.class);
		public static final String INTERCEPTOR_TYPE_NAME = Type.getInternalName(Interceptor.class);
	}

}
