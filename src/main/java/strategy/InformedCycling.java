package strategy;

import car.Car;
import datastructures.Intersection;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import road.Road;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class InformedCycling implements Strategy{

	protected double tl_phase_length;
	protected StreetMap street_map;
	protected HashMap<Intersection, Double> times_till_toggle;

	public InformedCycling(double phase_length, StreetMap street_map) {
		this.tl_phase_length = phase_length;
		this.street_map = street_map;
		this.times_till_toggle = new HashMap<Intersection, Double>();
		for (Intersection inter : street_map.getIntersections()) {
			times_till_toggle.put(inter, tl_phase_length);
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
			times_till_toggle.put(inter, tl_phase_length);
		}
	}

	private void setTrafficLightActivity(Intersection inter, HashMap<Road, ArrayList<Car>> list_of_cars) {
		int current_tl = inter.getActiveLight();
		int next_tl = current_tl;
		for (int i = 1; i <= inter.getTrafficLights().size(); i++) {
			int viewed_tl = i + current_tl;
			if (viewed_tl >= inter.getTrafficLights().size()) viewed_tl = viewed_tl - (inter.getTrafficLights().size());
			Road source_road = inter.getTrafficLights().get(viewed_tl).get(0).getRoad();
			int amount_of_cars = 0;
			for (Car c : list_of_cars.get(source_road)) if (c.getCurrentDestinationIntersection() == inter) amount_of_cars++;
			if (amount_of_cars > 0) {
				next_tl = viewed_tl;
				break;
			}
		}

		inter.setActiveLight(next_tl);

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
