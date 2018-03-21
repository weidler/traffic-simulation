package core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import algorithms.AStar;
import datastructures.StreetMap;
import datastructures.Car;
import datastructures.Intersection;

public class Simulation {

	private boolean run = false;
	
	private StreetMap street_map;
	private ArrayList<Car> cars;

	public Simulation(StreetMap map) {
		this.street_map = map;
		this.cars = new ArrayList<Car>();
	}
	
	// GETTERS / SETTERS
	
	public ArrayList<Car> getCars() {
		return this.cars;
	}
	
	public StreetMap getStreetMap() {
		return this.street_map;
	}
	
	// ACTIONS
	
	public void addCar(Car car) {
		this.cars.add(car);	
		for(Intersection intersection : street_map.getIntersections())
		{
			intersection.resetParent();
			intersection.resetCost();
		}
	}
	
	public void addRandomCar() {
		this.street_map.getIntersections();
		this.street_map.getRoads();
		
		Random r = new Random();
		int origin = r.nextInt(this.street_map.getIntersections().size());
		int destination;
		do {
			destination = r.nextInt(this.street_map.getIntersections().size());
		} while (destination == origin);
		Intersection origin_intersection = this.street_map.getIntersection(origin);
		Intersection destination_intersection = this.street_map.getIntersection(destination);
				
		ArrayList<Intersection> shortest_path = AStar.createPath(origin_intersection, destination_intersection);
		System.out.println("Path: " + shortest_path);
		
		this.addCar(new Car(shortest_path, this.street_map));
		System.out.println("created new car, x: " + this.street_map.getIntersection(origin).getXCoord() + ", y: " + this.street_map.getIntersection(origin).getYCoord() + ", total: "+ this.getCars().size());
	}
	
	public void generateRandomCars(int n_cars) {
		for (int i = 0; i < n_cars; i++) {
			this.addRandomCar();
		}
	}
	
	// SIMULATION
	
	public void start() {
		run = true;
		System.out.println("start");

		// Initialize
		for (Intersection is : this.street_map.getIntersections()) {
			is.initializeTrafficLightSettings();
		}
		
		double delta_t = 0.1;
		int simulated_seconds = 10;
		for (double t = 0; t <= simulated_seconds; t += delta_t) {

			System.out.println("\n--------T = " + t + "s---------");

			for (Car car : this.cars) {
				// update traffic light statuses
				this.street_map.update();
				// recalculate car positions
				car.update(this.cars, delta_t);
				
				System.out.println(car);
			}
		}
	}

	public void stop() {
		run = false;
		System.out.println("stop");
	}
	
	public void reset() {
		this.cars.clear();
	}
}