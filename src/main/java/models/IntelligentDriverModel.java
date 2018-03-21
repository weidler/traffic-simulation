package models;

import datastructures.Car;

public class IntelligentDriverModel{

	final static double MIN_HEADWAY = 1.5;
	final static double MIN_SPACING = 2;
	final static double delta = 4; // this is common practice for this model
	
	public static double getAcceleration(Car car, double dist_leading, double leading_velocity) {
		double acceleration_free = car.MAX_ACCELERATION * ( 1 - Math.pow((car.getCurrentVelocity() / car.getDesiredVelocity()), delta));

		// if the car can't see any cars in front of it, just use free road term
		if (dist_leading == Double.NaN || leading_velocity == Double.NaN) {
			return acceleration_free;
		}

		double s_star =  MIN_SPACING + car.getCurrentVelocity() * MIN_HEADWAY + (car.getCurrentVelocity() * (car.getCurrentVelocity() - leading_velocity))/(2 * Math.sqrt(car.MAX_ACCELERATION * car.DECCELARATION));
		double acceleration_interaction = (-car.MAX_ACCELERATION) * Math.pow(s_star / dist_leading, 2);
		
		return acceleration_free + acceleration_interaction;
	}
}
