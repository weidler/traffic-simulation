package datastructures;

import java.awt.Color;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.IntelligentDriverModel;

/**
 * 
 * @author weidler
 *
 */
public class Car {

	// LOCALIZATION
	private ArrayList<Intersection> path;
	private Road current_road;
	private Intersection current_origin_intersection;
	private Intersection current_destination_intersection;
	private boolean reached_destination;
	
	// CONSTANTS
	public final double REACTION_TIME = 1;
	public final double MAX_ACCELERATION = 10;
	public final double DECCELARATION = 3;
	public final double SIGHT_DISTANCE = 10;
	public final double DESIRED_VELOCITY = 400;
	
	// DYNAMIC VALUES
	private double current_velocity;
	private double positionX;
	private double positionY;
	private double position_on_road;
	private int lane = 0;

	// CONSTRAINTS
	private double desired_velocity;
	
	// A THING NEEDED
	private int offsetX;
	private int offsetY;

	private Color color;
	
	/**
	 *
	 * @param startPoint
	 * @param endPoint
	 * @param streetMap this object needs to be passed as parameter to find the road the car is in!
	 */
	public Car(ArrayList<Intersection> path, StreetMap streetMap) {
		this.path = path;

		color = Color.blue;
		
		this.current_origin_intersection = path.get(0);
		this.current_destination_intersection = path.get(1);
		
		this.positionX = (double) current_origin_intersection.getXCoord();
		this.positionY = (double) current_origin_intersection.getYCoord();

		this.current_road = current_origin_intersection.getRoadTo(current_destination_intersection);
		
		this.current_velocity = 0;
		this.desired_velocity = this.DESIRED_VELOCITY;
		
		this.reached_destination = false;
	}
	
	public double getPositionOnRoad() {
		return position_on_road;
	}

	public void setPositionOnRoad(double position_on_road) {
		this.position_on_road = position_on_road;
		double[] new_coordinates = this.getCoordinatesFromPosition(position_on_road);
		this.setPositionX(new_coordinates[0]);
		this.setPositionY(new_coordinates[1]);
	}

	public ArrayList<Intersection> getPath() {
		return path;
	}

	public void setPath(ArrayList<Intersection> path) {
		this.path = path;
	}

	public Road getCurrentRoad() {
		return current_road;
	}

	public void setCurrentRoad(Road current_road) {
		this.current_road = current_road;
	}

	public Intersection getCurrentOriginIntersection() {
		return current_origin_intersection;
	}

	public void setCurrentOriginIntersection(Intersection current_origin_intersection) {
		this.current_origin_intersection = current_origin_intersection;
	}

	public Intersection getCurrentDestinationIntersection() {
		return current_destination_intersection;
	}

	public void setCurrentDestinationIntersection(Intersection current_destination_intersection) {
		this.current_destination_intersection = current_destination_intersection;
	}

	public double getCurrentVelocity() {
		return current_velocity;
	}

	public void setCurrentVelocity(double current_velocity) {
		this.current_velocity = current_velocity;
	}

	public double getPositionX() {
		return positionX;
	}

	public void setPositionX(double positionX) {
		this.positionX = positionX;
	}

	public double getPositionY() {
		return positionY;
	}

	public void setPositionY(double positionY) {
		this.positionY = positionY;
	}

	public double getDesiredVelocity() {
		return desired_velocity;
	}

	public void setDesiredVelocity(double desired_velocity) {
		this.desired_velocity = desired_velocity;
	}
	
	public int getOffsetX(){
		
		return offsetX;
	}
	
	public int getOffsetY(){
		
		return offsetY;
	}
	
	public Color getColor()
	{
		return color;
	}
	public void setLane(int l)
	{
		if(l >= 1 && l < 4)
		{
			lane = l;
		}
		else 
		{
			System.out.println("number of lanes is not allowed");
		}		
	}
	public int getLanes()
	{
		return lane;
	}
	
	public void calculateOffset(Intersection start, Intersection end) {
		if(start.getXCoord() > end.getXCoord())
		{
			if(start.getYCoord() <end.getYCoord())
			{
				offsetX = -2*lane;
				offsetY = -2*lane;
			}
			else
			{
				offsetX = 2*lane;
				offsetY = -2*lane;
			}
		}
		else
		{
			if(start.getYCoord() <end.getYCoord())
			{
				offsetX = -2*lane;
				offsetY = 2*lane;
			}
			else
			{
				offsetX = 2*lane;
				offsetY = 2*lane;
			}
		}
	}

	public boolean update(ArrayList<Car> list_of_cars, double delta_t){
		double acceleration;
		
		// Check if leading car, else incorporate leaders speed etc.
		Car leading_car = this.getLeadingCar(list_of_cars);
		if (leading_car == null) {
			this.color = Color.red;
			acceleration = IntelligentDriverModel.getAcceleration(this, Double.NaN, Double.NaN);
		} else {
			this.color = Color.blue;
			double dist_leading = this.getDistanceToCar(leading_car);
			double leading_velocity = leading_car.getCurrentVelocity();
			acceleration = IntelligentDriverModel.getAcceleration(this, dist_leading, leading_velocity);
		}
		
		// React to traffic lights
		if (this.getApproachedTrafficlight().isRed() && this.getApproachedIntersectionDistance() < this.SIGHT_DISTANCE) {
			this.current_velocity = 0;
			acceleration = 0;
		}
		
		// Update speed and position
		this.position_on_road += Math.max(this.current_velocity * delta_t, 0);
		this.current_velocity = Math.max(this.current_velocity + acceleration * delta_t, 0);
				
		// Check if at destination
		if (this.position_on_road >= this.current_road.getLength() && this.current_destination_intersection == this.path.get(this.path.size()-1)) {
			this.reached_destination = true;
		} else {

			// Check if at new road
			if (this.position_on_road >= this.current_road.getLength()) {
				this.current_origin_intersection = this.current_destination_intersection;
				this.current_destination_intersection = this.path.get(this.path.indexOf(this.current_origin_intersection) + 1);
				this.position_on_road = this.position_on_road - this.current_road.getLength();
				this.current_road = this.current_origin_intersection.getRoadTo(this.current_destination_intersection);
			}

			// update x and y based on position on road
			double[] new_coordinates = this.getCoordinatesFromPosition(this.position_on_road);
			this.positionX = new_coordinates[0];
			this.positionY = new_coordinates[1];
		}
				
		return this.reached_destination;
	}

	public TrafficLight getApproachedTrafficlight() {
		return this.current_destination_intersection.getTrafficLightFrom(this.current_origin_intersection);
	}
	
	public double getApproachedIntersectionDistance() {
		return this.current_road.getLength() - this.position_on_road;
	}
	
	/**
	 *
	 * @param list_of_cars
	 * @param car
	 * @return true if there is no car on the same road that has bigger x coordinate than the car passed to the method
	 */
	public boolean hasLeadingCar(ArrayList<Car> list_of_cars){
		if (this.getLeadingCar(list_of_cars) == null) return false;
		return true;
	}

	public Car getLeadingCar(ArrayList<Car> list_of_cars) {
		Car current_leading_car = null;

		for(Car c : list_of_cars){
			//for the case it compares with itself skip to next iteration
			if(this.equals(c)) continue;

			// only compare if driving on cars path
			if (c.isOnPath(new ArrayList<Intersection>(this.path.subList(this.path.indexOf(this.getCurrentOriginIntersection()), this.path.size())), 1)) {
				// only compare if driving in the same direction...
				if (this.path.indexOf(c.getCurrentOriginIntersection()) < this.path.indexOf(c.getCurrentDestinationIntersection())) {
					// only compare to cars in front
					if(this.getPositionOnRoad() <= c.getPositionOnRoad() || c.getCurrentRoad() != this.getCurrentRoad())
						// If car is closer than previous then update
						if (current_leading_car == null) {
							current_leading_car = c;
						} else if(this.getDistanceToCar(c) < this.getDistanceToCar(current_leading_car)){
							current_leading_car = c;
						}
				}
			}
		}
		
		return current_leading_car;
	}

	public double getDistanceToCar(Car other_car){
		// on same road
		if (this.current_destination_intersection.equals(other_car.current_destination_intersection)) {
			return Math.abs(this.getPositionOnRoad() - other_car.getPositionOnRoad());
		} else {
			// get distance over multiple roads
			double distance = 0;
			for (int i = this.path.indexOf(this.current_destination_intersection); i <= this.path.indexOf(other_car.current_destination_intersection); i++) {
				if (i == this.path.indexOf(this.current_destination_intersection)) {
					distance += this.current_road.getLength() - this.getPositionOnRoad();
				} else if (i == this.path.indexOf(other_car.current_destination_intersection)) {
					distance += other_car.getPositionOnRoad();
				} else {
					distance += this.path.get(i).getRoadTo(this.path.get(i)).getLength();
				}
			}
			return distance;
		}
	}

//	public double getPositionOnRoad() {
//		return Math.sqrt(Math.pow((this.positionX - this.current_road.getX1()), 2) + Math.pow(this.positionY - this.current_road.getY1(), 2));
//	}
	
	// CHECKS
	
	public boolean isOnPath(ArrayList<Intersection> path, int look_ahead) {
		Intersection last_intersection = null;
		int looked_at = 0;
		for (Intersection inter : path) {
			if (last_intersection != null) {
				if (this.current_road.equals(last_intersection.getRoadTo(inter))) {
					return true;
				}
			}
			last_intersection = inter;
			if (looked_at >= look_ahead) break;
			looked_at++;
		}
		
		return false;
	}
	
	/**
	 * https://math.stackexchange.com/questions/2045174/how-to-find-a-point-between-two-points-with-given-distance
	 */
	private double[] getCoordinatesFromPosition(double position) {
		double x_delta = this.current_destination_intersection.getXCoord() - this.current_origin_intersection.getXCoord();
		double y_delta = this.current_destination_intersection.getYCoord() - this.current_origin_intersection.getYCoord();
				
		double[] coordinates = new double[2];
		coordinates[0] = this.current_origin_intersection.getXCoord() + (position/this.current_road.getLength()) * x_delta;
		coordinates[1] = this.current_origin_intersection.getYCoord() + (position/this.current_road.getLength()) * y_delta;		
		
		return coordinates;
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat(".##");
		return "Car: (x=" + (int)this.positionX + ", y=" + (int)this.positionY + ", v=" + df.format(current_velocity) + " km/h" +")";
	}
}
