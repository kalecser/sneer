package sneer.bricks.pulp.httpclient;

import java.io.IOException;

import basis.brickness.Brick;
import basis.lang.Pair;


@Brick
public interface HttpClient {

	String get(String url, Pair<String, String>... headers) throws IOException;

}
