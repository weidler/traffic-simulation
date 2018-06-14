package schedule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import datastructures.StreetMap;
import road.Road;

public class Schedule {

	protected StreetMap street_map;
	protected HashMap<Road, Double> arrival_times_per_road;
	protected int population_per_meter;

	public Schedule(StreetMap street_map) {
		this.street_map = street_map;
		this.arrival_times_per_road = new HashMap<Road, Double>();
		for (Road r : this.street_map.getRoads()) {
			arrival_times_per_road.put(r, this.drawInterarrivalTime());
		}
	}

	public boolean carWaitingAt(Road r, double current_time) {
		if (current_time >= arrival_times_per_road.get(r)) {
			return true;
		}

		return false;
	}

	public void drawNextCarAt(Road r) {
		arrival_times_per_road.put(r, arrival_times_per_road.get(r) + this.drawInterarrivalTime());
	}

	public double adjustStatisticToRoad(Road r, double stat) {
		stat = stat * (r.getLength() / 500);
		switch (r.getZoneType()) {

			case RESIDENTIAL:
				stat *= 0.5;
				break;
			case MIXED:
				break;
			case COMMERCIAL:
				stat *= 1.4;
			case INDUSTRIAL:
				stat *= 2;
		}

		return stat;
	}

	/**
	 * NOT IMPLEMENTED FOR THIS SCHEDULE
	 */
	public void drawNextCarAt(Road r, double current_time) {
		System.out.println("NOT IMPLEMENTED FOR THIS SCHEDULE.");
	}

	public double drawInterarrivalTime() {
		Random rand = new Random();
		return rand.nextDouble() * 10;
	}

	public void logSchedule() {
		System.out.println(this.arrival_times_per_road);
	}

	public void updateToMap() {
		for (Road r : this.street_map.getRoads()) {
			if (!this.arrival_times_per_road.containsKey(r)) {
				this.arrival_times_per_road.put(r, this.drawInterarrivalTime());
			}
		}
	}

}
