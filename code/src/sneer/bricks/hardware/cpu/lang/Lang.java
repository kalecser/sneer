package sneer.bricks.hardware.cpu.lang;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import basis.brickness.Brick;


@Brick
public interface Lang {

	Arrays arrays(); 
	Serialization serialization(); 
	Strings strings();
	SystemUtils system();

	interface Arrays { 
		void reverse(Object[] array);
		byte[] concat(byte[] a, byte[] b);
	}
	
	interface Serialization {
		byte[] serialize(Serializable obj) ;
		<T> T serialize(byte[] data);
	}
	
	interface Strings { 
		boolean isEmpty(String str);
		String abbreviate(String str, int maxWidth);
		String join(Collection<?> collection, String separator);
		String trimToNull(String str);
		String remove(String str, String remove);
		String removeStart(String str, String remove);
		String chomp(String str, String suffix);
		String strip(String str, String stripChars);
		String substringBeforeLast(String str, String separator);
		String deleteWhitespace(String str);
		String insertSpacedSeparators(String str, String separator, int interval);
		List<String> readLines(String input);
		byte[] toByteArray(String string);
		String[] splitRight(String line, char separator, int maxParts);
		String substringAfterLast(String str, String separator);
	}

	public interface SystemUtils {
		boolean isOsLinux();
	}
}

