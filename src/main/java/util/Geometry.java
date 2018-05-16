package util;

import java.util.Arrays;

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
	
	public static double[] intersection(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
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
	
	public static double clockwiseAngle(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int common_x, int common_y) {
		double[] intersection_point = intersection(x1, y1, x2, y2, x3, y3, x4, y4);
		
		// move intersection to origin
		double centered_x1 = x1 - common_x;
		double centered_x2 = x2 - common_x;
		double centered_x3 = x3 - common_x;
		double centered_x4 = x4 - common_x;
		double centered_y1 = y1 - common_y;
		double centered_y2 = y2 - common_y;
		double centered_y3 = y3 - common_y;
		double centered_y4 = y4 - common_y;
		
		double[] vector_a = new double[2];
		double[] vector_b = new double[2];

		vector_a[0] = centered_x1 - centered_x2;
		vector_a[1] = centered_y1 - centered_y2;
		vector_b[0] = centered_x3 - centered_x4;
		vector_b[1] = centered_y3 - centered_y4;
		
		double angle = toDegrees(Math.atan2(vector_a[0] * vector_b[1] - vector_a[1] * vector_b[0], vector_a[0] * vector_b[0] + vector_a[1] * vector_b[1]));
		return (360 - angle) % 360;
	}
	
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow(y2 - y1, 2));
	}
	
	public static double[] offset(Road road, int lane_size) {
		double[] offsets = new double[5];
		
		double angle = Math.atan2(road.getY2() - road.getY1(), road.getX1() - road.getX2());
		if (angle<0) angle+=Math.PI*2;
		double offsetAngle = angle+(Math.PI/2);	
		if (offsetAngle > Math.PI*2) offsetAngle-= Math.PI*2;
		
		offsets[0] = (int) (Math.round(Math.cos(offsetAngle) * lane_size));
		offsets[1] = (int) (Math.round(Math.sin(offsetAngle) * lane_size));
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
	
	public static void main(String[] args) {
		
		double Ax = -30;
		double Ay = 10; // A(-30, 10)
		double Bx = 29;
		double By = -15; // B(29, -15)
		double Px = 15;
		double Py = 28; // P(15, 28)
		
		System.out.println(Geometry.liesLeft(Ax, Ay, Bx, By, Px, Py));
		System.out.println(Geometry.liesLeft(Bx, By, Ax, Ay, Px, Py));
	
	}
	
}
