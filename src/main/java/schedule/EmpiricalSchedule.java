package schedule;

import java.io.FileReader;
import java.util.Random;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import com.google.common.primitives.Doubles;

import datastructures.StreetMap;
import road.Road;
import util.Time;

public class EmpiricalSchedule extends Schedule {

	private double mean_interarrival_time;
	private double[] rates = new double[24];

	public EmpiricalSchedule(StreetMap street_map, double mean_interarrival_time, String data_file_path) {
		super(street_map);

		try {
			Object obj = new JSONParser().parse(new FileReader(data_file_path));
			JSONObject jo = (JSONObject) obj;

			for (int i = 0; i < 24; i++) {
				rates[i] = ((Long) jo.get(Integer.toString(i))).doubleValue();
			}

		} catch (Exception e) {
			System.out.println("[WARNING] JSON DATA COULDNT BE PROCESSED. FALLBACK TO UNIFORM.");
			e.printStackTrace();
			for (int i = 0; i < 24; i++) {
				rates[i] = 1;
			}
		}

		this.mean_interarrival_time = mean_interarrival_time;
	}

	public void drawNextCarAt(Road r, double realistic_time_in_seconds) {
		arrival_times_per_road.put(r, arrival_times_per_road.get(r)
				+ this.drawInterarrivalTime(this.mean_interarrival_time, realistic_time_in_seconds));
	}

	public double drawInterarrivalTime(double mean, double realistic_time_in_seconds) {
		Random rand = new Random();
		boolean thinned = true;
		double final_interarrival_time = 0;
		double realistic_arrival_time = 0;
		// generate new arrival as long as the current one would be thinned out
		while (thinned) {
			// generate uniform random number
			double u = rand.nextDouble();
			final_interarrival_time += -mean * Math.log(rand.nextDouble());
			realistic_arrival_time = realistic_time_in_seconds + final_interarrival_time;
			while (Time.secondsToHours(realistic_arrival_time) >= 24) {
				realistic_arrival_time = realistic_arrival_time - Time.hoursToSeconds(24);
			}

			double lambda_star = Doubles.max(rates);
			double thinning_rand = rand.nextDouble();
			if (thinning_rand <= (rates[(int) Math.floor(Time.secondsToHours(realistic_arrival_time))] / lambda_star)) {
				thinned = false;
			}
		}

		return final_interarrival_time;
	}

}
