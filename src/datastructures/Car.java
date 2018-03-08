package datastructures;



import javax.swing.*;
import java.util.List;

public class Car {

	private Intersection startPoint;
	private Intersection endPoint;
	private int breakSpeed = 1; //assign the value you want
	private double velocity = 0; //initial velocity is zero
	private int acceleration = 1;//assign the value you want
	private int timeTraveled;
	private int positionX;
	private int positionY;
	private List<Car> list_of_cars; //needs to get this from somewhere
	public static final int REACTION_TIME = 1;

	public Car(Intersection startPoint , Intersection endPoint, List<Car> list_of_cars)
	{
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		positionX = startPoint.getXCoord();
		positionY = startPoint.getYCoord();
		this.list_of_cars = list_of_cars;
	}

	/**
	 *
	 * @return the safe velocity for the car, SUMO formula
	 */
	public double safeVelocity(){

		double safe_velocity = 0;
		int index = -1; //keeps track of the index of the leading vehicle in the list
		double tracker = Double.MAX_VALUE; //keeps track of the position of the car with the smallest distance

		for(int i=0; i<list_of_cars.size(); i++){

			//For the case it compares with itself
			//Skip to the next iteration
			if(positionX == list_of_cars.get(i).positionX)
				continue;

			if(tracker > list_of_cars.get(i).positionX){
				tracker = list_of_cars.get(i).positionX;
				index = i;
			}
		}

		double leading_velocity = list_of_cars.get(index).velocity; //speed of leading vehicle

		double gap = Math.abs(positionX - list_of_cars.get(index).positionX); //distance from leading vehicle

		safe_velocity = leading_velocity + ((gap - leading_velocity*REACTION_TIME)/((velocity/breakSpeed)+REACTION_TIME));

		return safe_velocity;
	}

	/**
	 * Changes the position of the car
	 */
	public void update(){

		move();
	}

	/**
	 * Gets the safe velocity for the car to have and applies it
	 * I am assuming 0 acceleration right now, constant speed
	 * Will change that depending how it fits the simulation
	 * @return the new position of the car
	 */
	public double move(){

		double v = safeVelocity();

		positionX += v;

		return v;
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

	public double getTimeTraveled() {
		return timeTraveled;
	}

	public void setTimeTraveled(int timeTraveled) {
		this.timeTraveled = timeTraveled;
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
