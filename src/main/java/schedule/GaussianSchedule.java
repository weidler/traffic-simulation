package schedule;

import java.util.Random;

import datastructures.StreetMap;
import road.Road;

public class GaussianSchedule extends Schedule {

	private double mean_interarrival_time;
	private double sd_interarrival_times;

	public GaussianSchedule(StreetMap street_map, double mean_interarrival_time, double sd_interarrival_times) {
		super(street_map);
		this.mean_interarrival_time = mean_interarrival_time;
		this.sd_interarrival_times = sd_interarrival_times;
	}
	
	public void drawNextCarAt(Road r) {
		arrival_times_per_road.put(r, arrival_times_per_road.get(r) + this.drawInterarrivalTime(this.mean_interarrival_time, this.sd_interarrival_times));
	}
	
	public double drawInterarrivalTime(double mean, double sd) {
		Random rand = new Random();
		double rn = rand.nextGaussian();
		return rn * sd + mean;
	}

}
