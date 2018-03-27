package models;

import datastructures.Car;

public class IntelligentDriverModel{

	final static double MIN_HEADWAY = 2.5;
	final static double MIN_SPACING = 10;
	final static double delta = 4; // this is common practice for this model
	
	public static double getAcceleration(Car car, double dist_leading, double leading_velocity) {
		// ROAD FREE TERM (no close cars)
		double free_term = car.MAX_ACCELERATION * (1 - Math.pow((car.getCurrentVelocity() / car.getDesiredVelocity()), delta));
		
		// if the car can't see any cars in front of it, just use free road term
		if (Double.isNaN(dist_leading) || Double.isNaN(leading_velocity)) {
			return free_term;
		}
		
		// TRAFFIC LIGHT TERM (deccelerate if there is a red trafficlight in front)
		double light_term = 0;

		// INTERACTION TERM (adjust to the speed of the leading car
		double s_star =  MIN_SPACING + car.getCurrentVelocity() * MIN_HEADWAY + (car.getCurrentVelocity() * (car.getCurrentVelocity() - leading_velocity))/(2 * Math.sqrt(car.MAX_ACCELERATION * car.DECCELARATION));
		double interaction_term = (-car.MAX_ACCELERATION) * Math.pow(s_star / dist_leading, 2);
		
		double acceleration = free_term + interaction_term + light_term;
		return acceleration;
	}
}
