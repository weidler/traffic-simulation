package car;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Properties;

import datastructures.Intersection;
import datastructures.StreetMap;
import type.CarType;

public class Truck extends Car {

	public Truck(ArrayList<Intersection> path, double departure_time, Properties props) {
		super(path, departure_time, props);
	}

	protected void setTypeParameters(Properties props) {
		// DRIVING
		this.reaction_time = 1;
		this.max_acceleration = Integer.parseInt(props.getProperty("truck_max_acceleration"));
		this.decceleration = Integer.parseInt(props.getProperty("truck_decceleration"));
		this.sight_distance = Integer.parseInt(props.getProperty("truck_sight_distance"));
		this.tl_braking_distance = Integer.parseInt(props.getProperty("truck_tl_breaking_distance"));
		this.favored_velocity = Integer.parseInt(props.getProperty("truck_favored_velocity"));
		this.type = CarType.TRUCK;
		// PHYSICS
		this.vehicle_length = Integer.parseInt(props.getProperty("truck_vehicle_length"));
		this.color = Color.ORANGE;
		this.max_passengers = Integer.parseInt(props.getProperty("truck_max_passengers"));
	}

}
