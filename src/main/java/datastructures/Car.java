package datastructures;

import java.awt.Color;
import java.lang.reflect.Array;
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
	public final double MAX_ACCELERATION = 1.2;
	public final double DECCELARATION = 1.8;
	public final double SIGHT_DISTANCE = 10;
	
	// DYNAMIC VALUES
	private double current_velocity;
	private double positionX;
	private double positionY;
	private double position_on_road;

	// CONSTRAINTS
	private double desired_velocity;
	
	// A THING NEEDED
	private int offsetX;
	private int offsetY;
	
	private int counter;
	private Color color;
	
	/**
	 *
	 * @param startPoint
	 * @param endPoint
	 * @param streetMap this object needs to be passed as parameter to find the road the car is in!
	 */
	public Car(ArrayList<Intersection> path, StreetMap streetMap, int counter) {
		this.path = path;
		this.counter = counter;

		if(counter % 2 == 0)
			color = Color.RED;
		else
			color = Color.BLUE;
		
		this.current_origin_intersection = path.get(0);
		this.current_destination_intersection = path.get(1);
		
		this.positionX = (double) current_origin_intersection.getXCoord();
		this.positionY = (double) current_origin_intersection.getYCoord();

		this.current_road = current_origin_intersection.getRoadTo(current_destination_intersection);
		System.out.println(current_road);		
		
		this.current_velocity = 0;
		this.desired_velocity = 13;
		
		this.reached_destination = false;
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
	
	public void calculateOffset(Intersection start, Intersection end) {
		if(start.getXCoord() > end.getXCoord())
		{
			if(start.getYCoord() <end.getYCoord())
			{
				offsetX = -4;
				offsetY = -4;
			}
			else
			{
				offsetX = 4;
				offsetY = -4;
			}
		}
		else
		{
			if(start.getYCoord() <end.getYCoord())
			{
				offsetX = -4;
				offsetY = 4;
			}
			else
			{
				offsetX = 4;
				offsetY = 4;
			}
		}
	}

	public boolean update(List<Car> list_of_cars, double delta_t){
		double acceleration;
		if (this.isLeadingCar(list_of_cars)) {
			System.out.println("IS LEADING");
			acceleration = IntelligentDriverModel.getAcceleration(this, Double.NaN, Double.NaN);
		} else {
			double dist_leading = this.getLeadingCarDistance(list_of_cars);
			double leading_velocity = this.getLeadingCarVelocity(list_of_cars);		
			acceleration = IntelligentDriverModel.getAcceleration(this, dist_leading, leading_velocity);
		}
		
		if (this.getApproachedTrafficlight().isRed() && this.getApproachedIntersectionDistance() < this.SIGHT_DISTANCE) {
			this.current_velocity = 0;
			acceleration = 0;
		}
		
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
	public boolean isLeadingCar(List<Car> list_of_cars){

		for(int i=0; i<list_of_cars.size(); i++){

			//for the case it compares with itself skip to next iteration
			if(list_of_cars.get(i).equals(this))
				continue;

			// only compare if driving on the same road
			if (!(list_of_cars.get(i).getCurrentRoad().equals(this.getCurrentRoad()))) {
				continue;
			}
			
			// only compare if driving to the same intersection...
			if (!(list_of_cars.get(i).getCurrentDestinationIntersection().equals(this.getCurrentDestinationIntersection()))) {
				continue;
			}


			if(this.getPositionOnRoad() <= list_of_cars.get(i).getPositionOnRoad()) {
				return false;
			}
		}

		//no car in the list was found to be on the same road with bigger x coordinate than this car
		return true;
	}

	/**
	 *
	 * @param list_of_cars
	 * @param car the car for which we want to find the velocity of the car directly in front of it
	 * @return the leading cars velocity
	 */
	public double getLeadingCarVelocity(List<Car> list_of_cars){

		double currentClosestPosition = Double.MAX_VALUE; //initially infinity
		int index = -1; //keeps track of the index of the closest car in the list

		for(int i=0; i<list_of_cars.size(); i++){

			//for the case it compares with itself skip to next iteration
			if(list_of_cars.get(i).equals(this))
				continue;

			// only compare if driving on the same road
			if (!(list_of_cars.get(i).getCurrentRoad().equals(this.getCurrentRoad()))) {
				continue;
			}
			
			// only compare if driving to the same intersection...
			if (!(list_of_cars.get(i).getCurrentDestinationIntersection().equals(this.getCurrentDestinationIntersection()))) {
				continue;
			}

			// only compare to cars in front
			if(list_of_cars.get(i).getPositionOnRoad() <= this.getPositionOnRoad())
				continue;

			if(list_of_cars.get(i).getPositionOnRoad() < currentClosestPosition){
				currentClosestPosition = list_of_cars.get(i).getPositionOnRoad();
				index = i;
			}
		}

		if (index > 0) {
			return list_of_cars.get(index).getCurrentVelocity();			
		} else {
			return Double.NaN;
		}

	}

	public double getLeadingCarDistance(List<Car> list_of_cars){

		double current_closest_position = Double.NaN; //initially infinity
		int index = -1; //keeps track of the index of the closest car in the list

		for(int i = 0; i < list_of_cars.size(); i++){

			//for the case it compares with itself skip to next iteration
			if(list_of_cars.get(i).equals(this))
				continue;

			// only compare if driving on the same road
			if (!(list_of_cars.get(i).getCurrentRoad().equals(this.getCurrentRoad()))) {
				continue;
			}
			
			// only compare if driving to the same intersection...
			if (!(list_of_cars.get(i).getCurrentDestinationIntersection().equals(this.getCurrentDestinationIntersection()))) {
				continue;
			}

			// only compare to cars in front
			if(list_of_cars.get(i).getPositionOnRoad() <= this.getPositionOnRoad())
				continue;

			if(Double.isNaN(current_closest_position) || list_of_cars.get(i).getPositionOnRoad() < current_closest_position){
				current_closest_position = list_of_cars.get(i).getPositionOnRoad();
				index = i;
			}
		}

		if (index > 0) {
			return Math.abs(this.getPositionOnRoad() - list_of_cars.get(index).getPositionOnRoad());			
		} else {
			return Double.NaN;
		}
	}

	private double getPositionOnRoad() {
		return Math.sqrt(Math.pow((this.positionX - this.current_road.getX1()), 2) + Math.pow(this.positionY - this.current_road.getY1(), 2));
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
		return "Car: (x=" + this.positionX + ", y=" + this.positionY + ", v=" + this.current_velocity + ")";
	}
}
