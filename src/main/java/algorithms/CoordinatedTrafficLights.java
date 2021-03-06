package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import car.Car;
import core.Simulation;
import datastructures.Intersection;
import datastructures.TrafficLight;
import road.Road;


public class CoordinatedTrafficLights {

	ArrayList<ArrayList<TrafficLight>> traffic_lights = new ArrayList<ArrayList<TrafficLight>>();
	HashMap<Road, ArrayList<Car>> cars = new HashMap<Road, ArrayList<Car>>();

	Point prevPos;
	Point curPos;

	boolean isGreen = false;

	int xCoord	= 0;
	int yCoord 	= 0;
	int minusXdistance 	= 0;
	int plusXdistance 	= 0;
	int minusYdistance 	= 0;
	int plusYdistance 	= 0;

	int searchRadius 	= 10;

	//Must be called by each intersection as it is created, after traffic lights are created
	public void setDetectionRadius(Intersection i) {

		traffic_lights = i.getTrafficLights();

		xCoord = i.getXCoord();
		yCoord = i.getYCoord();

		minusXdistance 	= xCoord - searchRadius;
		plusXdistance 	= xCoord + searchRadius;
		minusYdistance 	= yCoord - searchRadius;
		plusYdistance 	= yCoord + searchRadius;
	}

	//Checks for which road is the busiest in an intersection

	public Road weightedRoads1(Intersection i, HashMap<Road,ArrayList<Car>> cars) {
		int interX 	= i.getXCoord();
		int interY 	= i.getYCoord();
		int index	= 0;
		Road busiestRoad = null;
		ArrayList<Intersection> intersections = new ArrayList<Intersection>();
		
		intersections = i.getConnectedIntersections();
		ArrayList<Integer> roadCounts = new ArrayList<Integer>();
		
		for(int k = 0; k < intersections.size(); k++) {
			int carCount = 0;
			Road curRoad = intersections.get(k).getRoadTo(i);
			for(Car car : cars.get(curRoad))
			{ 
				Intersection target = car.getCurrentDestinationIntersection();
				
				if(target == i && target != null)
				{
					carCount++;
				}
			}
			roadCounts.add(carCount);
		}
	
		for(int l = 0; l < roadCounts.size(); l++) {
			int newNumber = roadCounts.get(l);
			if((newNumber > roadCounts.get(index))) {
				index = roadCounts.indexOf(newNumber);
			}
		}
		
		busiestRoad = intersections.get(index).getRoadTo(i);
		return busiestRoad;
	}
	public Road weightedRoads2(Intersection i, HashMap<Road,ArrayList<Car>> cars) {
		int interX 	= i.getXCoord();
		int interY 	= i.getYCoord();
		int index	= 0;
		Road busiestRoad = null;
		ArrayList<Intersection> intersections = new ArrayList<Intersection>();
		
		intersections = i.getConnectedIntersections();
		ArrayList<Integer> carCountList = new ArrayList<>();
		ArrayList<Double> avgSpeed = new ArrayList<>();
		for(int k = 0; k < intersections.size(); k++) {
			int carCount = 0;
			double sumSpeed = 0;
			Road curRoad = intersections.get(k).getRoadTo(i);
			for(Car car : cars.get(curRoad))
			{ 
				Intersection target = car.getCurrentDestinationIntersection();
				
				if(target == i && target != null)
				{
					sumSpeed = sumSpeed + car.getCurrentVelocity();
					carCount++;
				}
			}
			carCountList.add(carCount);
			if(carCount == 0)
			{
				avgSpeed.add((double) (curRoad.getAllowedMaxSpeed()+20));
			}
			else 
			{
				avgSpeed.add(sumSpeed/carCount);
			}
		}
	
		for(int l = 0; l < avgSpeed.size(); l++) {
			double newNumber = avgSpeed.get(l);
			if(newNumber < (avgSpeed.get(index)-10)) {
				index = avgSpeed.indexOf(newNumber);
			}
			else if(Math.abs(newNumber - avgSpeed.get(index)) <= 10 )
			{
				if(carCountList.get(avgSpeed.indexOf(newNumber)) > carCountList.get(index))
				{
					index = avgSpeed.indexOf(newNumber);
				}
			}
		}
		
		busiestRoad = intersections.get(index).getRoadTo(i);
		return busiestRoad;
	}

	public Double weightedRoads3(Intersection i, HashMap<Road,ArrayList<Car>> cars, Road r) {
		ArrayList<Intersection> intersections = new ArrayList<Intersection>();
		
		intersections = i.getConnectedIntersections();
		ArrayList<Integer> carCountList = new ArrayList<>();
		int curRoadCount = 0;
		for(int k = 0; k < intersections.size(); k++) {
			int carCount = 0;
			
			Road curRoad = intersections.get(k).getRoadTo(i);
			for(Car car : cars.get(curRoad))
			{ 
				Intersection target = car.getCurrentDestinationIntersection();
				
				if(target == i && target != null)
				{
					
					carCount++;
				}
			}
			carCountList.add(carCount);
			if(r == curRoad)
			{
				curRoadCount = carCount;
			}
			
		}
		int sumOfCars = 0;
		for(Integer num : carCountList)
		{
			sumOfCars = sumOfCars + num;
		}
		if(sumOfCars!=0)
		{
			return (double) (curRoadCount / sumOfCars);
		}
		else 
		{
			return 1.0;
		}
			
	}
	public Intersection adjustNextTL(Intersection i) {
		return i;
		
	}

	

	//Must be called before currentPosition is called
	public Point previousPosition(Point current) {
		prevPos = new Point(current);

		return prevPos;
	}

	//Must be called when car is created
	public Point currentPosition(Car c) {
		int xPos = (int) c.getPositionX();
		int yPos = (int) c.getPositionY();
		curPos = new Point(xPos,yPos);
		
		return curPos;
	}

	public ArrayList<ArrayList<TrafficLight>> getTrafficLights(){
		return traffic_lights;
	}

	public boolean getTrafficLightStatus(TrafficLight t){
		return isGreen;
	}
}