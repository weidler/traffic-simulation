package datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CarSavvas {


	private Intersection startPoint;
	private Intersection endPoint;
	private Road road;

	private double breakSpeed = -1;
	private double velocity = 0; //initial velocity is zero
	private double acceleration = 1.5;
	private double positionX;
	private double positionY;
	private Random rnd = new Random();
	public static final double REACTION_TIME = 1;
	public static final double MAX_VELOCITY = 2;
	public static final double HUMAN_ERROR = 0.2;

	/**
	 *
	 * @param startPoint
	 * @param endPoint
	 * @param streetMap this object needs to be passed as parameter to find the road the car is in!
	 */
	public CarSavvas(Intersection startPoint , Intersection endPoint, StreetMap streetMap)
	{
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		positionX = (double) startPoint.getXCoord();
		positionY = (double) startPoint.getYCoord();
		this.road = streetMap.getRoadByCoordinates(startPoint.getXCoord(), startPoint.getYCoord(), endPoint.getXCoord(), endPoint.getYCoord());
	}



	public static void main(String[] args) {

		Intersection start = new Intersection(0, 0);
		Intersection end = new Intersection(15, 15);

		Road road = new Road(start,end);

		StreetMap streetMap = new StreetMap();

		streetMap.addIntersection(start);
		streetMap.addIntersection(end);
		streetMap.addRoad(road);

		Car c1 = new Car(start,end,streetMap);
		Car c2 = new Car(start,end,streetMap);
		Car c3 = new Car(start,end,streetMap);

		List<Car> list = new ArrayList<>();
		list.add(c1);
		list.add(c2);
		list.add(c3);

		for(int i=0; i<10; i++){

			for(int j=0; j<list.size(); j++){
				list.get(j).update(list,1);
				System.out.println(list.get(j));
			}

			System.out.println();
		}



	}

	/**
	 *
	 * automatically updates the car coordinates, both x and y
	 */
	public void update(List<Car> list_of_cars, int timeStep){

		if(noCarOnTheRoadHasMoved(list_of_cars,this.road))
			accelerate(this,timeStep);
		else{
			//Get the safe velocity
			double safe_velocity = safeVelocity(list_of_cars);
			double desired_velocity = desiredVelocity(safe_velocity);
			double rangeMin = (desired_velocity - HUMAN_ERROR*acceleration);
			double rangeMax = desired_velocity;
			velocity = Math.max(0, rangeMin + (rangeMax - rangeMin)*rnd.nextDouble());
		}



		//new position of the car
		double x = (positionX +  velocity*timeStep +  0.5*acceleration*Math.pow(timeStep,2));

		positionX =  x;
		positionY = getY(); //must be used after acquiring the new x
	}

	/**
	 *
	 */
	public void reset(){

	}


	/**
	 *
	 * @param car the first car to go
	 * if no car has started moving yet, give acceleration to the leading car so that others can follow
	 */
	private void accelerate(Car car, int timeStep){

		if(car.getVelocity() == 0 && car.positionX == car.startPoint.getXCoord()){
			velocity += acceleration/timeStep + 0.5;
		}

	}

	/**
	 *
	 * @param list_of_cars
	 * @param road the road for which we are considering if cars have moed
	 * @return false if a car that has different positionX from the x of its start point
	 */
	private boolean noCarOnTheRoadHasMoved(List<Car> list_of_cars, Road road){

		for(int i=0; i<list_of_cars.size(); i++){

			//for the case it compares with car on different road, skip to next iteration
			if(!(list_of_cars.get(i).getRoad().equals(road)))
				continue;

			if(list_of_cars.get(i).getPositionX() != list_of_cars.get(i).getStartPoint().getXCoord())
				return false;
		}

		return true;
	}

	/**
	 *@param list_of_cars
	 * @return the safe velocity for the car, SUMO formula
	 * if car is leading car returns maximum allowed velocity set to 3
	 */
	public double safeVelocity(List<Car> list_of_cars){

		//if the car has reached its destination, stop moving
		if(getPositionX() == endPoint.getXCoord()){
			acceleration = 0;
			velocity = 0;
			return 0;
		}

		if(isLeader(list_of_cars,this))
			return MAX_VELOCITY;

		double leading_vel = getLeadingCarVelocity(list_of_cars, this);
		double gap = getLeadingCarDistance(list_of_cars, this);
		//double avg_vel = averageVelocity(list_of_cars);
		double deceleration = deceleration(velocity,leading_vel);

		double safe_vel = leading_vel -  ((gap - leading_vel*REACTION_TIME) / ((velocity/deceleration) + REACTION_TIME));

		return safe_vel;
	}

	private double desiredVelocity(double safe_velocity){

		return Math.min(safe_velocity,Math.min(velocity + acceleration, MAX_VELOCITY));
	}

	private double deceleration(double velocity, double leading_velocity){
		return velocity - leading_velocity;
	}

	private double averageVelocity(List<Car> list_of_cars){

		double sum_of_velocities = 0;

		for(Car c: list_of_cars)
			sum_of_velocities += c.getVelocity();

		return sum_of_velocities/list_of_cars.size();
	}

	/**
	 *
	 * @param list_of_cars
	 * @param car
	 * @return true if there is no car on the same road that has bigger x coordinate than the car passed to the method
	 */
	public boolean isLeader(List<Car> list_of_cars, Car car){

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
	 * @return the leading velocity to be used by SUMO formula
	 */
	public double getLeadingCarVelocity(List<Car> list_of_cars, Car car){

		double currentClosestPosition = Double.MAX_VALUE; //initially infinity
		int index = -1; //keeps track of the index of the closest car in the list

		for(int i=0; i<list_of_cars.size(); i++){

			//for the case it compares with itself skip to next iteration
			if(list_of_cars.get(i).equals(car))
				continue;

			//in case it compares with a car on a different road, skip to next iteration
			if(!(list_of_cars.get(i).getRoad().equals(car.getRoad())))
				continue;


			//in case it compares with a car that is behind it, skip to the next iteration
			if(list_of_cars.get(i).getPositionX() - car.getPositionX() <= 0)
				continue;


			if(list_of_cars.get(i).getPositionX() < currentClosestPosition){
				currentClosestPosition = list_of_cars.get(i).getPositionX();
				index = i;
			}
		}

		return list_of_cars.get(index).getVelocity();
	}

	public double getLeadingCarDistance(List<Car> list_of_cars, Car car){

		double currentClosestPosition = Double.MAX_VALUE; //initially infinity
		int index = -1; //keeps track of the index of the closest car in the list

		for(int i=0; i<list_of_cars.size(); i++){

			//for the case it compares with itself skip to next iteration
			if(list_of_cars.get(i).equals(car))
				continue;

			//in case it compares with a car on a different road, skip to next iteration
			if(!(list_of_cars.get(i).getRoad().equals(car.getRoad())))
				continue;


			//in case it compares with a car that is behind it, skip to the next iteration
			if(list_of_cars.get(i).getPositionX() - car.getPositionX() <= 0)
				continue;


			if(list_of_cars.get(i).getPositionX() < currentClosestPosition){
				currentClosestPosition = list_of_cars.get(i).getPositionX();
				index = i;
			}
		}

		return Math.abs(car.getPositionX() - list_of_cars.get(index).getPositionX());
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

	public double getBreakSpeed() {
		return breakSpeed;
	}

	public void setBreakSpeed(int breakSpeed) {
		this.breakSpeed = breakSpeed;
	}

	public double getAcceleration() {
		return acceleration;
	}

	public void setAccelaration(int accelaration) {
		this.acceleration = accelaration;
	}

	public double getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public double getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	public double getVelocity(){ return velocity;}

	public void setVelocity(int velocity){this.velocity = velocity;}

	public Road getRoad(){
		return road;
	}
	
	public String toString() {
		return "Car: (" + this.positionX + ", " + this.positionY + ", " + velocity + ")";
	}
}
