package util;

import java.util.ArrayList;
import java.util.Arrays;

import datatype.Point;
import road.Road;

public class Geometry {

	public static double slope(double x1, double y1, double x2, double y2) {
		if (x2 - x1 == 0) return 0;
		return (y2 - y1) / (x2 - x1);
	}

	public static double yIntercept(double x1, double y1, double x2, double y2) {
		if (x2 - x1 == 0) return 0;
		return (y1 * x2 - y2 * x1) / (x2 - x1);
	}

	public static double[] intersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4,
			double y4) {
		double m_a = slope(x1, y1, x2, y2);
		double m_b = slope(x3, y3, x4, y4);
		double b_a = yIntercept(x1, y1, x2, y2);
		double b_b = yIntercept(x3, y3, x4, y4);

		if (m_a == 0 && m_b == 0) return null;

		double[] intersection = new double[2];
		intersection[0] = (b_b - b_a) / (m_a - m_b);
		intersection[1] = m_a * intersection[0] + b_a;

		return intersection;
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

	public static double toDegrees(double radians) {
		return radians * (180 / Math.PI);
	}
	
	public static double toRadians(double degrees) {
		return degrees * (Math.PI / 180);
	}

	public static double clockwiseAngle(int x1, int y1, int x2, int y2, int common_x, int common_y) {
		// move intersection to origin
		double centered_x1 = x1 - common_x;
		double centered_y1 = y1 - common_y;

		double centered_x2 = x2 - common_x;
		double centered_y2 = y2 - common_y;

		double dot = centered_x1 * centered_x2 + centered_y1 * centered_y2;
		double det = centered_x1 * centered_y2 - centered_y1 * centered_x2;

		double angle = toDegrees(Math.atan2(det, dot));
		if (angle > 0) {
			return angle;
		} else {
			return 360 + angle;
		}
	}

	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow(y2 - y1, 2));
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
	
	public static double[] getPointBetween(double d, double x1, double y1, double x2, double y2) {
		double distance_between = distance(x1, y1, x2, y2);
		
		double[] new_point = new double[2];
		new_point[0] = x1 + (d/distance_between) * (x2 - x1);
		new_point[1] = y1 + (d/distance_between) * (y2 - y1);
		
		return new_point;
	}

	public static double[] rotateByAngle(double x, double y, double angle) {
		angle = toRadians(angle);
		double[] rotated = new double[2];
		rotated[0] = Math.cos(angle) * x - Math.sin(angle) * y;
		rotated[1] = Math.sin(angle) * x + Math.cos(angle) * y;
		
		return rotated;
	}
	
	public static double[] midpoint(double x1, double y1, double x2, double y2) {
		double[] mid = new double[2];
		mid[0] = (x1 + x2) / 2;
		mid[1] = (y1 + y2) / 2;
		return mid;
	}
	
	public static double[] rectangleCenter(double[] coordinates) {
		double[] center = midpoint(coordinates[0], coordinates[1], coordinates[4], coordinates[5]);
		return center;
	}
	
	public static double[] rotateRectangleAroundCenter(double[] coordinates, double angle) {
		double[] new_coordinates = new double[8];
		double[] center = rectangleCenter(coordinates);
		
		for (int i = 0; i < 8; i = i + 2) {
			// move to origin
			new_coordinates[i] = coordinates[i] - center[0];
			new_coordinates[i + 1] = coordinates[i + 1] - center[1];
			
			double[] rotated = rotateByAngle(new_coordinates[i], new_coordinates[i + 1], angle);
			new_coordinates[i] = rotated[0];
			new_coordinates[i + 1] = rotated[1];
			
			// move back
			new_coordinates[i] = new_coordinates[i] + center[0];
			new_coordinates[i + 1] = new_coordinates[i + 1] + center[1];
		}
		
		return new_coordinates;
	}
	
	public static int orientation(Point p, Point q, Point r) {
        double val = (q.y - p.y) * (r.x - q.x) -
                  (q.x - p.x) * (r.y - q.y);
      
        if (val == 0) return 0;  // collinear
        return (val > 0)? 1: 2; // clock or counterclock wise
    }
     
    /**
     * https://www.geeksforgeeks.org/convex-hull-set-1-jarviss-algorithm-or-wrapping/
     */
    public static ArrayList<Point> convexHull(ArrayList<Point> points) {
      
    	if (points.size() < 3) {
    		return points;
    	}
    	
    	// Initialize Result
        ArrayList<Point> hull = new ArrayList<Point>();
      
        // Find the leftmost point
        int l = 0;
        for (int i = 1; i < points.size(); i++) {
        	if (points.get(i).x < points.get(l).x) l = i;
        }
      
        // find hull
        int p = l, q;
        do {
            hull.add(points.get(p));
            q = (p + 1) % points.size();
            for (int i = 0; i < points.size(); i++) {
               if (orientation(points.get(p), points.get(i), points.get(q)) == 2) q = i;
            }
            p = q;
        } while (p != l);
      
        return hull;
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

		System.out.println(Arrays.toString(Geometry.rotateByAngle(4, 0, 180)));
		
	}

}
