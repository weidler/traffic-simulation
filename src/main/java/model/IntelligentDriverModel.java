package model;

import car.Car;

public class IntelligentDriverModel {

	private int min_headway;
	private int min_spacing;
	private int delta;

	public IntelligentDriverModel(int min_headway, int min_spacing, int delta) {
		this.min_headway = min_headway;
		this.min_spacing = min_spacing;
		this.delta = delta;
	}

	public double getAcceleration(Car car, double dist_leading, double leading_velocity) {
		// ROAD FREE TERM (no close cars)
		double free_term = car.getMaxAcceleration()
				* (1 - Math.pow((car.getCurrentVelocity() / car.getDesiredVelocity()), delta));

		// if the car can't see any cars in front of it, just use free road term
		if (Double.isNaN(dist_leading) || Double.isNaN(leading_velocity)) {
			return free_term;
		}

		// INTERACTION TERM (adjust to the speed of the leading car
		double desired_distance = (min_spacing + car.getCurrentVelocity()) * min_headway
				+ (car.getCurrentVelocity() * (car.getCurrentVelocity() - leading_velocity))
						/ (2 * Math.sqrt(car.getMaxAcceleration() * car.getDecceleration()));
		double interaction_term = (-car.getMaxAcceleration()) * Math.pow(desired_distance / dist_leading, 2);

		return free_term + interaction_term;
	}

	// GETTER AND SETTER

	public int getMinHeadway() {
		return min_headway;
	}

	public void setMinHeadway(int minHeadway) {
		this.min_headway = min_headway;
	}

	public int getMinSpacing() {
		return min_spacing;
	}

	public void setMinSpacing(int minSpacing) {
		this.min_spacing = min_spacing;
	}

	public int getDelta() {
		return delta;
	}

	public void setDelta(int delta) {
		this.delta = delta;
	}
}