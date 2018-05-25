package datatype;

public class Point {

	public double x, y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(double[] coords) {
		this.x = coords[0];
		this.y = coords[1];
	}

	public double[] toArray() {
		double[] coords = {x, y};
		return coords;
	}
}
