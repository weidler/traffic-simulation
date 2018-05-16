package schedule;

import java.util.Random;

import datastructures.StreetMap;
import road.Road;

public class PoissonSchedule extends Schedule {

	private double mean_interarrival_time;

	public PoissonSchedule(StreetMap street_map, double mean_interarrival_time) {
		super(street_map);
		this.mean_interarrival_time = mean_interarrival_time;
		// TODO Auto-generated constructor stub
	}
	
	public void drawNextCarAt(Road r) {
		arrival_times_per_road.put(r, arrival_times_per_road.get(r) + this.drawInterarrivalTime(this.mean_interarrival_time));
	}
	
	public double drawInterarrivalTime(double mean) {
		Random rand = new Random();
		double ia = - mean * Math.log(rand.nextDouble());
		return ia;
	}

}
