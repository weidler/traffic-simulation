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

		return sum / (values.size() - 1);
	}

	public static double variance(ArrayList<Double> values) {
		double sum = 0;
		double data_mean = mean(values);
		for (double v : values) {
			sum += Math.pow(v - data_mean, 2);
		}

		return sum / values.size();
	}

	public static double[] confidence_interval(ArrayList<Double> values) {
		double data_mean = mean(values);
		double data_variance = variance(values);

		double critical_value = 1.6;
		double half_length = critical_value * (Math.sqrt(data_variance / values.size()));

		double[] interval = new double[2];
		interval[0] = data_mean - half_length;
		interval[1] = data_mean + half_length;

		return interval;
	}
}
