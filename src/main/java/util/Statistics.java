package util;

import java.util.ArrayList;

public class Statistics {

	public static double mean(double[] values) {
		double sum = 0;
		for (double v : values) {
			sum += v;
		}
		
		return sum / values.length;
	}
	
	public static double mean(ArrayList<Double> values) {
		double sum = 0;
		for (double v : values) {
			sum += v;
		}
		
		return sum / values.size();
	}
}
