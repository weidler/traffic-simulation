package datastructures;

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
	private Intersection startPoint;
	private Intersection endPoint;
	private Road road;
	
	// CONSTANTS
	public final double REACTION_TIME = 1;
	public final double MAX_ACCELERATION = 0.8;
	public final double DECCELARATION = 1.8;
	
	// DYNAMIC VALUES
	private double current_velocity;
	private double positionX;
	private double positionY;

	// CONSTRAINTS
	private double desired_velocity;
	
	/**
	 *
	 * @param startPoint
	 * @param endPoint
	 * @param streetMap this object needs to be passed as parameter to find the road the car is in!
	 */
	public Car(Intersection startPoint , Intersection endPoint, StreetMap streetMap) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.positionX = (double) startPoint.getXCoord();
		this.positionY = (double) startPoint.getYCoord();
		this.road = streetMap.getRoadByCoordinates(startPoint.getXCoord(), startPoint.getYCoord(), endPoint.getXCoord(), endPoint.getYCoord());
		
		this.current_velocity = 0;
		this.desired_velocity = 50;
	}

	public Intersection getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Intersection startPoint) {
		this.startPoint = startPoint;
	}

	public Intersection getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Intersection endPoint) {
		this.endPoint = endPoint;
	}

	public Road getRoad() {
		return road;
	}

	public void setRoad(Road road) {
		this.road = road;
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

	public void update(List<Car> list_of_cars, double delta_t){
		double dist_leading = this.getLeadingCarDistance(list_of_cars);
		double leading_velocity = this.getLeadingCarVelocity(list_of_cars);
		double current_position_on_road = this.getPositionOnRoad();
		
		double acceleration = IntelligentDriverModel.getAcceleration(this.current_velocity, this.desired_velocity, this.MAX_ACCELERATION, dist_leading, leading_velocity, this.DECCELARATION);
		this.current_velocity += acceleration * delta_t;	
		current_position_on_road += this.current_velocity * delta_t;
		
		// update x and y based on position on road
		double[] new_coordinates = this.getCoordinatesFromPosition(current_position_on_road);
		this.positionX = new_coordinates[0];
		this.positionY = new_coordinates[1];
	}

	/**
	 *
	 * @param list_of_cars
	 * @param car
	 * @return true if there is no car on the same road that has bigger x coordinate than the car passed to the method
	 */
	public boolean isLeadingCar(List<Car> list_of_cars, Car car){

		for(int i=0; i<list_of_cars.size(); i++){

			//for the case it compares with itself skip to next iteration
			if(list_of_cars.get(i).equals(car))
				continue;

			//in case it compares with a car on a different road, skip to next iteration
			if(!(list_of_cars.get(i).getRoad().equals(car.getRoad()))){
				continue;
			}

			//if there is one car of the same road with bigger x, then this car is not a leader
			if(car.getPositionX() <= list_of_cars.get(i).getPositionX())
				return false;
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

			//in case it compares with a car on a different road, skip to next iteration
			if(!(list_of_cars.get(i).getRoad().equals(this.getRoad())))
				continue;


			//in case it compares with a car that is behind it, skip to the next iteration
			if(list_of_cars.get(i).getPositionX() - this.getPositionX() <= 0)
				continue;


			if(list_of_cars.get(i).getPositionX() < currentClosestPosition){
				currentClosestPosition = list_of_cars.get(i).getPositionX();
				index = i;
			}
		}

		return list_of_cars.get(index).getCurrentVelocity();
	}

	public double getLeadingCarDistance(List<Car> list_of_cars){

		double currentClosestPosition = Double.MAX_VALUE; //initially infinity
		int index = -1; //keeps track of the index of the closest car in the list

		for(int i=0; i<list_of_cars.size(); i++){

			//for the case it compares with itself skip to next iteration
			if(list_of_cars.get(i).equals(this))
				continue;

			//in case it compares with a car on a different road, skip to next iteration
			if(!(list_of_cars.get(i).getRoad().equals(this.getRoad())))
				continue;


			//in case it compares with a car that is behind it, skip to the next iteration
			if(list_of_cars.get(i).getPositionX() - this.getPositionX() <= 0)
				continue;


			if(list_of_cars.get(i).getPositionX() < currentClosestPosition){
				currentClosestPosition = list_of_cars.get(i).getPositionX();
				index = i;
			}
		}

		return Math.abs(this.getPositionX() - list_of_cars.get(index).getPositionX());
	}

	private double getPositionOnRoad() {
		return Math.sqrt(Math.pow((this.positionX - this.road.getX1()), 2) + Math.pow(this.positionY - this.road.getY1(), 2));
	}
	
	/**
	 * https://math.stackexchange.com/questions/2045174/how-to-find-a-point-between-two-points-with-given-distance
	 */
	private double[] getCoordinatesFromPosition(double position) {
		double x_delta = this.road.getX2() - this.road.getX1();
		double y_delta = this.road.getY2() - this.road.getY1();
				
		double[] coordinates = new double[2];
		coordinates[0] = this.road.getX1() + (position/this.road.getLength()) * x_delta;
		coordinates[1] = this.road.getY1() + (position/this.road.getLength()) * y_delta;		
		
		return coordinates;
	}
	
	/**
	 * Use after getting the x-coordinate
	 * @return the y coordinate of the new point
	 */
	private double getY(){

		//distance between start point and end point
		double road_length = Math.sqrt(Math.pow(endPoint.getXCoord() - startPoint.getXCoord(), 2) + Math.pow(endPoint.getYCoord() - startPoint.getYCoord(),2));

		double ratio_of_distances = (Math.abs(getPositionX() - startPoint.getYCoord()))/road_length;

		double y = (1 - ratio_of_distances)*startPoint.getYCoord() + ratio_of_distances*endPoint.getYCoord();

		return y;
	}
	
	public String toString() {
		return "Car: (x=" + this.positionX + ", y=" + this.positionY + ", v=" + this.current_velocity + ")";
	}
}
