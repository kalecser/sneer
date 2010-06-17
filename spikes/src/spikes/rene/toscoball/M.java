package spikes.rene.toscoball;
//a Math utility pack.


public class M{
	public static final double degtorad=Math.PI/180;
	
	public static double correctAngle(double dir) {
		while (dir<0) dir+=360;
		while (dir>360) dir-=360;
		return dir;
	}
	public static double lengthdirx(double len, double dir) {
		return len*Math.cos(dir*degtorad);
	}
	public static double lengthdiry(double len, double dir) {
		return len*-Math.sin(dir*degtorad);
	}
	public static double pointDirection(double x1, double y1, double x2, double y2) {
		return correctAngle(Math.atan2(y1-y2,x2-x1)/degtorad);
	}
	public static double pointDistance(double x1, double y1, double x2, double y2) {
		return Math.hypot((x1-x2),(y1-y2));
	}
	public static double sqr(double a) {
		return a*a;
	}
}
