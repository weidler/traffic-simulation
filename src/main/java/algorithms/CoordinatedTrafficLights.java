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
	public Road checkRoads(Intersection i, HashMap<Road, ArrayList<Car>> cars) {
		int interX 	= i.getXCoord();
		int interY 	= i.getYCoord();
		int index	= 0;
		Road busiestRoad = null;
		
		ArrayList<Intersection> intersections = new ArrayList<Intersection>();
		
		intersections = i.getConnectedIntersections();
		ArrayList<Integer> roadCounts = new ArrayList<Integer>();
		
		for(int k = 0; k < intersections.size(); k++) {
			Road curRoad = intersections.get(k).getRoadTo(i);
			int carCount = cars.get(curRoad).size();
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
