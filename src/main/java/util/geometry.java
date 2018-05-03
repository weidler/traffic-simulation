package util;

import java.util.Arrays;

public class geometry {

	static double slope(double x1, double y1, double x2, double y2) {
		if (x2 - x1 == 0) return 0;
		return (y2 - y1) / (x2 - x1);
	}
	
	static double yIntercept(double x1, double y1, double x2, double y2) {
		if (x2 - x1 == 0) return 0;
		return (y1 * x2 - y2 * x1) / (x2 - x1);
	}
	
	static double[] intersection(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
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
	
	static double vectorMagnitude(double x, double y) {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	static double vectorMagnitude(double[] vec) {
		return vectorMagnitude(vec[0], vec[1]);
	}
	
	static double dotProduct(double x1, double y1, double x2, double y2) {
		return (x1 * x2) + (y1 * y2);
	}
	
	static double dotProduct(double[] vec_a, double[] vec_b) {
		return dotProduct(vec_a[0], vec_a[1], vec_b[0], vec_b[1]);
	}
	
	static double toDegrees(double radians) {
		return radians * (180 / Math.PI);
	}
	
	static double clockwiseAngle(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int common_x, int common_y) {
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
	
	static double distance() {
		return 0;
	}

	public static void main(String[] args) {
		System.out.println(clockwiseAngle(1, 1, 3, 3, 1, 5, 3, 3, 3, 3));
		System.out.println(clockwiseAngle(1, 1, 3, 3, 6, 6, 3, 3, 3, 3));
		System.out.println(clockwiseAngle(1, 1, 3, 3, 5, 1, 3, 3, 3, 3));
		System.out.println(clockwiseAngle(1, 1, 3, 3, 1, 1, 3, 3, 3, 3));
	}
	
}
