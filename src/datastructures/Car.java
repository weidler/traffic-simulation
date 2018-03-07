package datastructures;

import java.util.Random;

public class Car {

	private Intersection startPoint;
	private Intersection endPoint;
	private int velocity = 0;
	private int breakSpeed; //assign the value you want
	private int accelaration;//assign the value you want
	private double timeTraveled;
	private int positionX;
	private int positionY;
	
	public Car(StreetMap streetMap)
	{
		Random rand = new Random();
		int  s = rand.nextInt(streetMap.getIntersections().size());
		int  e = rand.nextInt(streetMap.getIntersections().size());
		startPoint = streetMap.getIntersections().get(s);
		endPoint = streetMap.getIntersections().get(e);
		positionX = startPoint.getXCoord();
		positionY = startPoint.getYCoord();		
		
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

	public int getVelocity() {
		return velocity;
	}

	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	public int getBreakSpeed() {
		return breakSpeed;
	}

	public void setBreakSpeed(int breakSpeed) {
		this.breakSpeed = breakSpeed;
	}

	public int getAccelaration() {
		return accelaration;
	}

	public void setAccelaration(int accelaration) {
		this.accelaration = accelaration;
	}

	public double getTimeTraveled() {
		return timeTraveled;
	}

	public void setTimeTraveled(double timeTraveled) {
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
