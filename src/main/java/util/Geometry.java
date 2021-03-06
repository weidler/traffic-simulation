package util;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import geometry.Line;
import geometry.Point;
import road.Road;

public class Geometry {

	public static boolean liesOnSegment(Point s_a, Point s_b, Point q) {
	    if (q.x <= Math.max(s_a.x, s_b.x) && q.x >= Math.min(s_a.x, s_b.x) &&
	        q.y <= Math.max(s_a.y, s_b.y) && q.y >= Math.min(s_a.y, s_b.y))
	       return true;
	 
	    return false;
	}

	public static boolean ccw(Point A, Point B, Point C) {
		return (C.y-A.y) * (B.x-A.x) > (B.y-A.y) * (C.x-A.x);
	}

	public static boolean lineSegmentsIntersect(Point A, Point B, Point C, Point D) {
		if (A.equals(C) || A.equals(D) || B.equals(C) || B.equals(D)) {
			return false;
		}

		return ccw(A,C,D) != ccw(B,C,D) && ccw(A,B,C) != ccw(A,B,D);
	}
	
	public static Point intersection(Line P, Line Q) {
		double m_a = P.slope();
		double b_a = P.yIntercept();

		double m_b = Q.slope();
		double b_b = Q.yIntercept();
		
		if (m_a == 0 && m_b == 0) return null;
		
		double x, y;
		// if one of the line is parallel to y axis
		if (P.A.x == P.B.x) {
			x = P.A.x;
			y = m_b * x + b_b;
		} else if (Q.A.x == Q.B.x) {
			x = Q.A.x;
			y = m_a * x + b_a;
		} else {
			x = (b_b - b_a) / (m_a - m_b);
			y = m_a * x + b_a;
		}
		
		return new Point(x, y);
	}
	
	public static double vectorMagnitude(double x, double y) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}

	public static double vectorMagnitude(double[] vec) {
		return vectorMagnitude(vec[0], vec[1]);
	}

	public static double dotProduct(double x1, double y1, double x2, double y2) {
		return (x1 * x2) + (y1 * y2);
	}

	public static double dotProduct(double[] vec_a, double[] vec_b) {
		return dotProduct(vec_a[0], vec_a[1], vec_b[0], vec_b[1]);
	}

	public static double dotProduct(Point A, Point B) {
		return dotProduct(A.x, A.y, B.x, B.y);
	}

	public static double toDegrees(double radians) {
		return radians * (180 / Math.PI);
	}
	
	public static double toRadians(double degrees) {
		return degrees * (Math.PI / 180);
	}

	public static double clockwiseAngle(double x, double y, double x2, double y2, double x3, double y3) {
		// move intersection to origin
		double centered_x1 = x - x3;
		double centered_y1 = y - y3;

		double centered_x2 = x2 - x3;
		double centered_y2 = y2 - y3;

		double dot = centered_x1 * centered_x2 + centered_y1 * centered_y2;
		double det = centered_x1 * centered_y2 - centered_y1 * centered_x2;
		
		double angle = toDegrees(Math.atan2(det, dot));
		if (angle > 0) {
			return angle;
		} else {
			return 360 + angle;
		}
	}

	public static double distance(Point A, Point B) {
		return Math.sqrt(Math.pow((B.x - A.x), 2) + Math.pow(B.y - A.y, 2));
	}

	public static double[] offset(Road road, int lane_size) {
		double[] offsets = new double[5];

		double angle = Math.atan2(road.getY2() - road.getY1(), road.getX1() - road.getX2());
		if (angle < 0) angle += Math.PI * 2;
		double offsetAngle = angle + (Math.PI / 2);
		if (offsetAngle > Math.PI * 2) offsetAngle -= Math.PI * 2;

		offsets[0] = (Math.round(Math.cos(offsetAngle) * lane_size));
		offsets[1] = (Math.round(Math.sin(offsetAngle) * lane_size));
		offsets[2] = offsetAngle;

		return offsets;
	}

	public static boolean liesLeft(double a_x, double a_y, double b_x, double b_y, double p_x, double p_y) {
		// go to origin
		b_x -= a_x;
		b_y -= a_y;
		p_x -= a_x;
		p_y -= a_y;

		double cross_product = b_x * p_y - b_y * p_x;

		if (cross_product > 0) return false;
		return true;

	}

	public static Point getPointBetween(double d, Point A, Point B) {
		double distance_between = distance(A, B);

		return new Point(
				A.x + (d/distance_between) * (B.x - A.x),
				A.y + (d/distance_between) * (B.y - A.y)
		);
	}

	public static Point rotateByAngle(Point p, double angle) {
		angle = toRadians(angle);
		return new Point(Math.cos(angle) * p.x - Math.sin(angle) * p.y, Math.sin(angle) * p.x + Math.cos(angle) * p.y);
	}

	public static Point midpoint(Point a, Point b) {
		return new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
	}
	
	public static Point rectangleCenter(double[] coordinates) {
		return midpoint(new Point(coordinates[0], coordinates[1]), new Point(coordinates[4], coordinates[5]));
	}
	
	public static double[] rotateRectangleAroundCenter(double[] coordinates, double angle) {
		double[] new_coordinates = new double[8];
		Point center = rectangleCenter(coordinates);
		
		for (int i = 0; i < 8; i = i + 2) {
			// move to origin
			new_coordinates[i] = coordinates[i] - center.x;
			new_coordinates[i + 1] = coordinates[i + 1] - center.y;
			
			Point rotated = rotateByAngle(new Point(new_coordinates[i], new_coordinates[i + 1]), angle);
			new_coordinates[i] = rotated.x;
			new_coordinates[i + 1] = rotated.y;
			
			// move back
			new_coordinates[i] = new_coordinates[i] + center.x;
			new_coordinates[i + 1] = new_coordinates[i + 1] + center.y;
		}
		
		return new_coordinates;
	}
	
	public static ArrayList<Point> orderPointsClockwise(ArrayList<Point> points, Point center) {
		if (points.size() <= 3) {
    		return points;
    	}
		
		TreeMap<Double, Point> angles =  new TreeMap<Double, Point>();
		
		Point starting_point = points.get(0);
		double angle;
		for (Point p : points.subList(1, points.size())) {
			angle = clockwiseAngle(starting_point.x, starting_point.y, p.x, p.y, center.x, center.y);
			angles.put(angle, p);
		}
		
		ArrayList<Point> out = new ArrayList<Point>();
		for (Map.Entry<Double, Point> entry : angles.entrySet()) {
			out.add(entry.getValue());
		}
		
		return out;
		
	}
	
	public static void main(String[] args) {
		double Ax = -30;
		double Ay = 10; // A(-30, 10)
		double Bx = 29;
		double By = -15; // B(29, -15)
		double Px = 15;
		double Py = 28; // P(15, 28)

		System.out.println(Geometry.liesLeft(Ax, Ay, Bx, By, Px, Py));
		System.out.println(Geometry.liesLeft(Bx, By, Ax, Ay, Px, Py));

		double a = Geometry.clockwiseAngle(1, 4, 4, 5, 3, 3);

		System.out.println(a);

		System.out.println(Geometry.lineSegmentsIntersect(new Point(85, 170),
				new Point(269, 493),
				new Point(113, 444),
				new Point(295, 266)));
		
	}

}
