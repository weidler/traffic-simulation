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

		System.out.println(this.times_till_toggle);
	}

	public void updateTrafficLights(Intersection inter, HashMap<Road, ArrayList<Car>> list_of_cars, double delta_t) {
		times_till_toggle.put(inter, times_till_toggle.get(inter) - delta_t);

		if (times_till_toggle.get(inter) <= 0) {
			this.setTrafficLightActivity(inter, list_of_cars);
			times_till_toggle.put(inter, tl_phase_length);
		}
	}

	public void setTrafficLightActivity(Intersection inter, HashMap<Road, ArrayList<Car>> list_of_cars) {
		int current_tl = inter.getActiveLight();
		int next_tl_increment = 0;
		for (int i = 1; i < inter.getTrafficLights().size(); i++) {
			int viewed_tl = i + current_tl;
			if (viewed_tl > inter.getTrafficLights().size()) viewed_tl = viewed_tl - inter.getTrafficLights().size();
			Road source_road = inter.getTrafficLights().get(viewed_tl).get(0).getRoad();
			if (list_of_cars.get(source_road).size() > 0) {
				next_tl_increment = i;
				break;
			}
		}

		inter.setActiveLight(inter.getActiveLight() + next_tl_increment);
		if (inter.getActiveLight() >= inter.getTrafficLights().size()) {
			inter.setActiveLight((inter.getActiveLight() + next_tl_increment) - inter.getTrafficLights().size());
		}

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
