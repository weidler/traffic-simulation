package models;

public class IntelligentDriverModel{

	final static double MIN_HEADWAY = 1.5;
	final static double MIN_SPACING = 2;
	final static double delta = 4; // this is common practice for this model
	
	public static double getAcceleration(double current_velocity, double desired_velocity, double max_acceleration, double dist_leading, double leading_velocity, double deceleration) {
		double acceleration_free = max_acceleration * ( 1 - Math.pow((current_velocity / desired_velocity), delta));
		double s_star =  MIN_SPACING + current_velocity * MIN_HEADWAY + (current_velocity * (current_velocity - leading_velocity))/(2 * Math.sqrt(max_acceleration * deceleration));
		double acceleration_interaction = (-max_acceleration) * Math.pow(s_star / dist_leading, 2);
		
		return acceleration_free + acceleration_interaction;
	}
}
