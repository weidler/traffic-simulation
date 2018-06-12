package datatype;

import util.Geometry;

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
	
	public boolean equals(Point b) {
		return (this.x == b.x && this.y == b.y);
	}
	
	public double[] toArray() {
		double[] coords = {x, y};
		return coords;
	}

	public Point projectOn(Line line) {
		// get dot product of e1, e2
		Point e1 = new Point(line.B.x - line.A.x, line.B.y - line.A.y);
		Point e2 = new Point(this.x - line.A.x, this.y - line.A.y);
		double valDp = Geometry.dotProduct(e1, e2);
		// get length of vectors
		double lenLineE1 = Math.sqrt(e1.x * e1.x + e1.y * e1.y);
		double lenLineE2 = Math.sqrt(e2.x * e2.x + e2.y * e2.y);
		double cos = valDp / (lenLineE1 * lenLineE2);
		// length of v1P'
		double projLenOfLine = cos * lenLineE2;
		Point p = new Point((int)(line.A.x + (projLenOfLine * e1.x) / lenLineE1),
				(int)(line.A.y + (projLenOfLine * e1.y) / lenLineE1));
		return p;
	}
}
