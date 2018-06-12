package datatype;

import util.Geometry;

public class Line {
	public Point A;
	public Point B;

	public Line(Point A, Point B) {
		this.A = A;
		this.B = B;
	}

	public Line(double A_x, double A_y, double B_x, double B_y) {
		this.A = new Point(A_x, A_y);
		this.B = new Point(B_x, B_y);
	}

	public double length() {
		return Geometry.distance(A, B);
	}

	public double slope() {
		if (B.x - A.x == 0) return 0;
		return (B.y - A.y) / (B.x - A.x);
	}

	public double yIntercept() {
		if (B.x - A.x == 0) return 0;
		return (A.y * B.x - B.y * A.x) / (B.x - A.x);
	}

	public double[] midpoint() {
		double[] mid = new double[2];
		mid[0] = (A.x + B.x) / 2;
		mid[1] = (A.y + B.y) / 2;
		return mid;
	}

	public Point intersectionWith(Line other) {
		return Geometry.intersection(this, other);
	}

	public Line inverted() {
		return new Line(B, A);
	}
}
