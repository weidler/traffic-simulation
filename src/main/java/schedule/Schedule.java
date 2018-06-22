package schedule;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import datastructures.StreetMap;
import road.Road;
import type.RoadType;

public class Schedule {

	protected StreetMap street_map;
	protected HashMap<Road, Double> arrival_times_per_road;
	protected int population_per_meter;

	public Schedule(StreetMap street_map) {
		this.street_map = street_map;
		this.arrival_times_per_road = new HashMap<Road, Double>();
		initializeIATs();
	}

	public Schedule() {}

	protected void initializeIATs() {
		for (Road r : this.street_map.getRoads()) {
			if(r.getRoadType() != RoadType.HIGHWAY) {
				arrival_times_per_road.put(r, 0.0);
				this.drawNextCarAt(r, 0);
			}
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
		double road_factor = Math.max(0.2, ((double) r.getAvailabePopulation()) / r.getDefaultPopulation());
		stat = stat * (1000 / r.getLength());  // Adjust to Roads Length
		stat = stat / road_factor; // Adjust to available population
		return stat;
	}

	/**
	 * NOT IMPLEMENTED FOR THIS SCHEDULE
	 */
	public void drawNextCarAt(Road r, double current_time) {
		this.drawNextCarAt(r);
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
