package spikes.rene.toscoball;
//an utility belt for toscoBall
//should not be instantiated.

import java.awt.Image;
import java.awt.Toolkit;


class M{
	static final double degtorad=Math.PI/180;
	
	static double correctAngle(double dir) {
		while (dir<0) dir+=360;
		while (dir>360) dir-=360;
		return dir;
	}
	static double lengthdirx(double len, double dir) {
		return len*Math.cos(dir*degtorad);
	}
	static double lengthdiry(double len, double dir) {
		return len*-Math.sin(dir*degtorad);
	}
	static double pointDirection(double x1, double y1, double x2, double y2) {
		return correctAngle(Math.atan2(y1-y2,x2-x1)/degtorad);
	}
	static double pointDistance(double x1, double y1, double x2, double y2) {
		return Math.hypot((x1-x2),(y1-y2));
	}
	static double sqr(double a) {
		return a*a;
	}
	public static Image img(String name) {
		return Toolkit.getDefaultToolkit().getImage(M.class.getResource("images/"+name));
	}
}
