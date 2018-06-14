package strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import algorithms.CoordinatedTrafficLights;
import car.Car;
import datastructures.Connection;
import datastructures.Intersection;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import road.Road;

public class Coordinated implements Strategy {
//try
	private static CoordinatedTrafficLights ctl = new CoordinatedTrafficLights();
	private ArrayList<Intersection> intersections;
	private double tl_phase_length;
	private StreetMap street_map;
	private HashMap<Intersection, Double> times_till_toggle;
	private HashMap<ArrayList<TrafficLight>, Double> last_turned_green;
	 
	
	public Coordinated(double phase_length, StreetMap street_map) {
		this.tl_phase_length = phase_length;
		this.street_map = street_map;
		intersections = street_map.getIntersections();
		this.times_till_toggle = new HashMap<Intersection, Double>();
		for (Intersection inter : street_map.getIntersections()) {
			times_till_toggle.put(inter, tl_phase_length);
		}
		last_turned_green = new HashMap<ArrayList<TrafficLight>, Double>();
		for (Intersection inter : street_map.getIntersections())
		{
			for (ArrayList<TrafficLight> tl : inter.getTrafficLights()) {
				last_turned_green.put(tl, tl_phase_length);
			}
		}
	}
	
	@Override
	public void configureTrafficLights(HashMap<Road, ArrayList<Car>> cars, double delta_t) {
		
		
		
		for (Intersection intersection : this.intersections) {
			
			if (intersection.getTrafficLights().size() <= 2) {
				for (ArrayList<TrafficLight> tls : intersection.getTrafficLights()) {
					for (TrafficLight t : tls) {
						t.setStatus("G");
					}
				}
			}
			else
			{			
				times_till_toggle.put(intersection, times_till_toggle.get(intersection) - delta_t);
				if(times_till_toggle.get(intersection) <= 0)
				{
					boolean switched = false;
					
					for(ArrayList<TrafficLight> tl : intersection.getTrafficLights())
					{
						last_turned_green.put(tl, last_turned_green.get(tl) - delta_t);
						if(last_turned_green.get(tl) <= 0)
						{
							for (TrafficLight t : tl) {
								t.setStatus("G");
								
							}
							last_turned_green.put(tl, tl_phase_length);
							switched = true;	
						}
						else
						{
							for (TrafficLight t : tl) {
								t.setStatus("R");							
							}
						}					
					}				
					if(!switched)
					{
						Road busiest = ctl.weightedRoads2(intersection, cars);
						setTrafficLightActivity2(busiest, intersection);
						times_till_toggle.put(intersection, tl_phase_length);
					}					
				}	
			}
		}		
	}
	
	public void setTrafficLightActivity2(Road busiest, Intersection intersection ) {
		 
		
			Intersection target;
			Intersection[] busiestIntersections = busiest.getIntersections();
			if(busiestIntersections[0].getXCoord() == intersection.getXCoord() && busiestIntersections[0].getYCoord() == intersection.getYCoord())
			{
				target = busiestIntersections[1];
			}
			else
			{
				target = busiestIntersections[0];
			}
			for (int i = 0; i < intersection.getTrafficLights().size(); i++) {
				ArrayList<Connection> connections = intersection.getConnections();
				for(int j = 0 ; j< connections.size(); j++)
				{
					if(connections.get(i).getDestination() == target)
					{
						for(TrafficLight t : connections.get(i).getTrafficlights())
						{
							t.setStatus("G");
							last_turned_green.put(connections.get(i).getTrafficlights(), tl_phase_length);
						}
						
					}
					else 
					{
						for(TrafficLight t : connections.get(i).getTrafficlights())
						{
							t.setStatus("R");
						}
					}
				}
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
