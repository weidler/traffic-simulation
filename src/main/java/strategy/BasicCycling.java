package strategy;

import car.Car;
import datastructures.Intersection;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import road.Road;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class BasicCycling implements Strategy {

	private double tl_phase_length;
	private StreetMap street_map;
	private HashMap<Intersection, Double> times_till_toggle;

	public BasicCycling(double phase_length, StreetMap street_map) {
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
			updateTrafficLights(inter, delta_t);
		}
	}

	@Override
	public void initializeTrafficLightSettings() {
		for (Intersection inter : street_map.getIntersections()) {
			if (inter.getTrafficLights().size() > 2) {
				inter.setActiveLight(ThreadLocalRandom.current().nextInt(0, inter.getTrafficLights().size()));
			}

			this.setTrafficLightActivity(inter);
		}

		System.out.println(this.times_till_toggle);
	}


	public void updateTrafficLights(Intersection inter, double delta_t) {
		times_till_toggle.put(inter, times_till_toggle.get(inter) - delta_t);

		if (times_till_toggle.get(inter) <= 0) {
			this.setTrafficLightActivity(inter);
			times_till_toggle.put(inter, tl_phase_length);
		}
	}

	public void setTrafficLightActivity(Intersection inter) {
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

		inter.setActiveLight(inter.getActiveLight() + 1);
		if (inter.getActiveLight() >= inter.getTrafficLights().size()) {
			inter.setActiveLight(0);
		}
	}
}
