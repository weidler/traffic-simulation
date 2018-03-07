package datastructures;

import javax.swing.*;
import java.util.List;

public class Car {

	private Intersection startPoint;
	private Intersection endPoint;
	private int breakSpeed = 10; //assign the value you want
	private int acceleration = 10;//assign the value you want
	private long timeTraveled;
	private long firstTime;
	private int positionX;
	private int positionY;
	private List<Car> list_of_cars; //needs to get this from somewhere
	private static final int ALLOWED_DISTANCE = 15;

	public Car(Intersection startPoint , Intersection endPoint, List<Car> list_of_cars)
	{
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		positionX = startPoint.getXCoord();
		positionY = startPoint.getYCoord();
		this.list_of_cars = list_of_cars;
		this.firstTime = System.currentTimeMillis();
	}

	/**
	 *
	 * @return true if there is a car within the designated distance
	 */
	public boolean sensor(){

		for(int i=0; i<list_of_cars.size(); i++){

			if(Math.abs(positionX - list_of_cars.get(i).positionX) == 0)
				continue;

			if(Math.abs(positionX - list_of_cars.get(i).positionX) == ALLOWED_DISTANCE)
				return true;
		}

		return false;
	}

	/**
	 * brake
	 */
	public void  deaccelerate(){

		if(sensor()){
			acceleration -= 0;
		}
	}

	/**
	 * Uses physics formula to calculate the new position of the car
	 * Car first checks if it has to brake, otherwise it accelerates
	 */
	public void move(){

		while(positionX != endPoint.getXCoord() && positionY != endPoint.getYCoord())
			deaccelerate();

			positionX += 0.5*acceleration;
	}

	/**
	 *
	 * @return the time the car took to reach its final destination in ms, -1 if the car is not at its target
	 */
	public long timeTravelled(){

		if(positionX == endPoint.getXCoord() && positionY == endPoint.getYCoord())
			return System.currentTimeMillis() - firstTime;

		else return -1;
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

	public void setTimeTraveled(long timeTraveled) {
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
}
