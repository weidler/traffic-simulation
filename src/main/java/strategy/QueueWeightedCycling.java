package strategy;

import algorithms.CoordinatedTrafficLights;
import car.Car;
import datastructures.Intersection;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import road.Road;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class QueueWeightedCycling implements strategy.Strategy {

	protected static CoordinatedTrafficLights ctl = new CoordinatedTrafficLights();
	protected double tl_phase_length;
	protected double total_tl_phase_length;
	protected StreetMap street_map;
	protected HashMap<Intersection, Double> times_till_toggle;
	protected HashMap<Intersection, HashMap<Road, Double>> previous_phase_lengths;

	public QueueWeightedCycling(double phase_length, StreetMap street_map) {
		this.tl_phase_length = phase_length;
		this.street_map = street_map;
		this.times_till_toggle = new HashMap<Intersection, Double>();
		for (Intersection inter : street_map.getIntersections()) {
			times_till_toggle.put(inter, tl_phase_length);
		}

		this.previous_phase_lengths = new HashMap<>();
		for (Intersection inter : street_map.getIntersections()) {
			previous_phase_lengths.put(inter, new HashMap<>());
			for (Road rd : inter.getOutgoingRoads()) {
				previous_phase_lengths.get(inter).put(rd, tl_phase_length);
			}
		}
	}

	@Override
	public void configureTrafficLights(HashMap<Road, ArrayList<Car>> list_of_cars, double delta_t) {
		for (Intersection inter : street_map.getIntersections()) {
			updateTrafficLights(inter, list_of_cars, delta_t);
		}
	}

	@Override
	public void initializeTrafficLightSettings() {
		for (Intersection inter : street_map.getIntersections()) {
			if (inter.getTrafficLights().size() > 2) {
				inter.setActiveLight(ThreadLocalRandom.current().nextInt(0, inter.getTrafficLights().size()));
			}

			this.applySetting(inter);
		}
	}

	private void updateTrafficLights(Intersection inter, HashMap<Road, ArrayList<Car>> list_of_cars, double delta_t) {
		times_till_toggle.put(inter, times_till_toggle.get(inter) - delta_t);

		if (times_till_toggle.get(inter) <= 0) {
			this.setTrafficLightActivity(inter, list_of_cars);
		}
	}

	private void setTrafficLightActivity(Intersection inter, HashMap<Road, ArrayList<Car>> list_of_cars) {
		int current_tl = inter.getActiveLight();
		inter.setActiveLight(inter.getActiveLight() + 1);
		if (inter.getActiveLight() >= inter.getTrafficLights().size()) {
			inter.setActiveLight(0);
		}
		Road road_for_new_active = inter.getRoadForActiveLight();

		double next_phase_length;

		int total_queued = 0;
		for (Road r : inter.getOutgoingRoads()) {
			for (Car c : list_of_cars.get(r)) {
				if (c.getCurrentDestinationIntersection() == inter && c.isWaiting()) {
					total_queued++;
				}
			}
		}

		double waiting_on_next_road = 0;
		for (Car c : list_of_cars.get(road_for_new_active)) {
			if (c.getCurrentDestinationIntersection() == inter && c.isWaiting()) {
				waiting_on_next_road++;
			}
		}

		double weight = waiting_on_next_road / Math.max(1, total_queued);
		weight = Math.max(weight, 0.5);

		times_till_toggle.put(inter, weight * tl_phase_length);
		inter.getPreviousFlows().put(road_for_new_active, 0);
		this.applySetting(inter);
	}

	private void applySetting(Intersection inter) {
		if (inter.getTrafficLights().size() <= 2) {
			for (ArrayList<TrafficLight> tls : inter.getTrafficLights()) {
				for (TrafficLight t : tls) {
					t.setStatus("G");
				}
			}
		} else {
			for (int i = 0; i < inter.getTrafficLights().size(); i++) {
				if (i == inter.getActiveLight()) {
					for (TrafficLight t : inter.getTrafficLights().get(i)) {
						t.setStatus("G");
					}
				} else {
					for (TrafficLight t : inter.getTrafficLights().get(i)) {
						t.setStatus("R");
					}
				}
			}
		}
	}
}
