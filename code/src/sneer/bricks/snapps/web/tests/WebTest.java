package sneer.bricks.snapps.web.tests;

import org.junit.Ignore;
import org.junit.Test;

import sneer.tests.SovereignFunctionalTestBase;

public class WebTest  extends SovereignFunctionalTestBase{

	@Ignore
	@Test
	public void testHappyDay(){
		a().startHomePage();
		b().startHomePage(12346);
		
		
		String content = a().wgetOrCry("b");
		assertEquals(
				"HTTP/1.1 200 OK\r\n" + 
				"Connection: close\r\n" + 
				"Server: Jetty(8.y.z-SNAPSHOT)\r\n" + 
				"\r\n" + 
				"<h1>It works!! from+Ana Almeida</h1>", content);
		
		
	}
}
