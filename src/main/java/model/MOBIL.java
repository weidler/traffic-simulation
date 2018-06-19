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
		boolean isSafe = this.isSafe(car, approached_car, approached_car_on_new_lane, approaching_car_on_new_lane);
		if (!isSafe) return false;
		boolean isIncentive = this.isIncentive(car, approached_car, approached_car_on_new_lane, approaching_car_on_new_lane);

		return (isIncentive && isSafe);
	}
	
	public boolean isSafe(Car car, Car approached_car, Car approached_car_on_new_lane, Car approaching_car_on_new_lane) {
		boolean isSafe = true;

		if (approaching_car_on_new_lane == null && approached_car_on_new_lane == null) return true;

		// is the lane change safe?
		if (approaching_car_on_new_lane != null) {
			double dist_to_approaching = car.getDistanceToCar(approaching_car_on_new_lane);
			double hypothetical_acceleration_of_approaching_car = this.IDM.getAcceleration(approaching_car_on_new_lane,
					dist_to_approaching, car.getCurrentVelocity());
			isSafe = hypothetical_acceleration_of_approaching_car >= - this.b_safe
					&& (dist_to_approaching > IDM.getMinSpacing())
					&& (car.getDistanceToCar(approached_car_on_new_lane) > IDM.getMinSpacing());
		}

		
		return isSafe;
	}
	
	public boolean isIncentive(Car car, Car approached_car, Car approached_car_on_new_lane, Car approaching_car_on_new_lane) {
		boolean isIncentive = false;
		if (car.getCurrentVelocity() == 0) return false;		

		// is the lane change desired?
		double current_acceleration = car.getCurrentAcceleration();
		double potential_acceleration;
		if (approached_car_on_new_lane != null) potential_acceleration = IDM.getAcceleration(car,
				car.getDistanceToCar(approached_car_on_new_lane), approached_car_on_new_lane.getCurrentVelocity());
		else potential_acceleration = IDM.getAcceleration(car, Double.NaN, Double.NaN);
		isIncentive = potential_acceleration / current_acceleration >= 1.1; // at least 10% gain, prevents flickering changes

		return isIncentive;
	}

}
