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
	private ArrayList<Intersection> path;
	private Road current_road;
	
	// CONSTANTS
	public final double REACTION_TIME = 1;
	public final double MAX_ACCELERATION = 0.8;
	public final double DECCELARATION = 1.8;
	
	// DYNAMIC VALUES
	private double current_velocity;
	private double positionX;
	private double positionY;
	private double position_on_road;

	// CONSTRAINTS
	private double desired_velocity;
	
	/**
	 *
	 * @param startPoint
	 * @param endPoint
	 * @param streetMap this object needs to be passed as parameter to find the road the car is in!
	 */
	public Car(ArrayList<Intersection> path, StreetMap streetMap) {
		this.path = path;
		this.positionX = (double) path.get(0).getXCoord();
		this.positionY = (double) path.get(0).getYCoord();
		System.out.println(path.get(0).getXCoord() + " " + path.get(0).getYCoord() + " " + path.get(1).getXCoord() + " " + path.get(1).getYCoord());
		this.current_road = streetMap.getRoadByCoordinates(path.get(0).getXCoord(), path.get(0).getYCoord(), path.get(1).getXCoord(), path.get(1).getYCoord());
		System.out.println(current_road);
		
		
		this.current_velocity = 0;
		this.desired_velocity = 50;
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

		double acceleration;
		if (this.isLeadingCar(list_of_cars, this)) {
			acceleration = IntelligentDriverModel.getAcceleration(this, Double.NaN, Double.NaN);
		} else {
			double dist_leading = this.getLeadingCarDistance(list_of_cars);
			double leading_velocity = this.getLeadingCarVelocity(list_of_cars);		
			acceleration = IntelligentDriverModel.getAcceleration(this, dist_leading, leading_velocity);
		}

		this.current_velocity += acceleration * delta_t;	
		this.position_on_road += this.current_velocity * delta_t;
		
		// update x and y based on position on road
		double[] new_coordinates = this.getCoordinatesFromPosition(this.position_on_road);
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
			if(!(list_of_cars.get(i).getCurrentRoad().equals(car.getCurrentRoad()))){
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
			if(!(list_of_cars.get(i).getCurrentRoad().equals(this.getCurrentRoad())))
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
			if(!(list_of_cars.get(i).getCurrentRoad().equals(this.getCurrentRoad())))
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
		System.out.println(this.positionX);
		System.out.println(this.positionY);
		System.out.println(this.current_road);
		return Math.sqrt(Math.pow((this.positionX - this.current_road.getX1()), 2) + Math.pow(this.positionY - this.current_road.getY1(), 2));
	}
	
	/**
	 * https://math.stackexchange.com/questions/2045174/how-to-find-a-point-between-two-points-with-given-distance
	 */
	private double[] getCoordinatesFromPosition(double position) {
		double x_delta = this.current_road.getX2() - this.current_road.getX1();
		double y_delta = this.current_road.getY2() - this.current_road.getY1();
				
		double[] coordinates = new double[2];
		coordinates[0] = this.current_road.getX1() + (position/this.current_road.getLength()) * x_delta;
		coordinates[1] = this.current_road.getY1() + (position/this.current_road.getLength()) * y_delta;		
		
		return coordinates;
	}
	
	public String toString() {
		return "Car: (x=" + this.positionX + ", y=" + this.positionY + ", v=" + this.current_velocity + ")";
	}
}
