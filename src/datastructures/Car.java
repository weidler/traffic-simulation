package datastructures;



import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Car {

	private Intersection startPoint;
	private Intersection endPoint;
	private int breakSpeed = 1; //assign the value you want
	private double velocity = 0; //initial velocity is zero
	private int acceleration = 1;//assign the value you want
	private int positionX;
	private int positionY;
	protected double timeStep = 1;
	public static final int REACTION_TIME = 1;
	public static final int MAX_VELOCITY = 5;

	public Car(Intersection startPoint , Intersection endPoint)
	{
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		positionX = startPoint.getXCoord();
		positionY = startPoint.getYCoord();
	}

	/**
	 *
	 * @param list_of_cars
	 * @return the new position of the car
	 * it does NOT automatically update the position!
	 */
	public int update(List<Car> list_of_cars){

		velocity = safeVelocity(list_of_cars);
		positionX = (int)  (positionX +  velocity*timeStep +  0.5*acceleration*Math.pow(timeStep,2));

		return positionX;
	}

	/**
	 *@param list_of_cars
	 * @return the safe velocity for the car, SUMO formula
	 * if car is leading car returns maximum allowed velocity set to 5
	 */
	public double safeVelocity(List<Car> list_of_cars){

		double safe_velocity = 0;
		int index = -1; //keeps track of the index of the leading vehicle in the list
		double tracker = Double.MAX_VALUE; //keeps track of the position of the car with the smallest distance
		boolean leader = true; //keeps track if the vehicle is the leading vehicle

		for(int i=0; i<list_of_cars.size(); i++){

			//For the case it compares with itself
			//Skip to the next iteration
			if(positionX == list_of_cars.get(i).positionX)
				continue;

			if(tracker > list_of_cars.get(i).positionX){
				tracker = list_of_cars.get(i).positionX;
				index = i;
			}

			if(positionX - list_of_cars.get(i).positionX < 0)
				leader = false;
		}

		if(leader)
			return MAX_VELOCITY;

		double leading_velocity = list_of_cars.get(index).velocity; //speed of leading vehicle

		double gap = Math.abs(positionX - list_of_cars.get(index).positionX); //distance from leading vehicle

		safe_velocity = leading_velocity + ((gap - leading_velocity*REACTION_TIME)/((velocity/breakSpeed)+REACTION_TIME));

		return safe_velocity;
	}



	/**
	 *
	 * @return the time the car took to reach its final destination in ms, -1 if the car is not at its target
	 */
	public int timeTravelled(){

		return -1;
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

	public int getBreakSpeed() {
		return breakSpeed;
	}

	public void setBreakSpeed(int breakSpeed) {
		this.breakSpeed = breakSpeed;
	}

	public int getAcceleration() {
		return acceleration;
	}

	public void setAccelaration(int accelaration) {
		this.acceleration = accelaration;
	}

	public int getPositionX() {
		return positionX;
	}

	public void setPositionX(int positionX) {
		this.positionX = positionX;
	}

	public int getPositionY() {
		return positionY;
	}

	public void setPositionY(int positionY) {
		this.positionY = positionY;
	}

	public double getVelocity(){ return velocity;}

	public void setVelocity(double velocity){this.velocity = velocity;}
}
