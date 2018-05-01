package model;

import car.Car;

public class MOBIL {
	
	IntelligentDriverModel IDM;
	double b_safe;
	
	public MOBIL(IntelligentDriverModel IDM, double b_safe) {
		this.IDM = IDM;
		this.b_safe = b_safe;
	}
	
	public boolean shouldChangeLane(Car car, Car approached_car, Car approached_car_on_new_lane, Car approaching_car_on_new_lane) {
		boolean isIncentive = false;
		boolean isSafe = false;
		
		// is the lane change safe?
		if (approaching_car_on_new_lane != null) {
			double dist_to_approaching = approaching_car_on_new_lane.getDistanceToCar(car);
			double hypothetical_speed_of_approaching_car = this.IDM.getAcceleration(approaching_car_on_new_lane, dist_to_approaching, car.getCurrentVelocity());
			isSafe = hypothetical_speed_of_approaching_car >= -this.b_safe;
			if (!isSafe) return false; // shortcut for efficiency
		} else isSafe = true;
		
		// is the lane change desired?
		double current_acceleration = car.getCurrentAcceleration();
		double potential_acceleration;
		if (approached_car_on_new_lane != null) potential_acceleration = IDM.getAcceleration(car, car.getDistanceToCar(approached_car_on_new_lane), approached_car_on_new_lane.getCurrentVelocity());
		else potential_acceleration = IDM.getAcceleration(car, Double.NaN, Double.NaN);	
		isIncentive = potential_acceleration > current_acceleration;
		
		return (isIncentive && isSafe);
	}
	
}
