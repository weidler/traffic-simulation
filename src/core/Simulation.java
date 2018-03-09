package core;

import java.util.ArrayList;
import java.util.Random;

import datastructures.StreetMap;
import datastructures.Car;

public class Simulation {

	private boolean run = false;
	
	private StreetMap street_map;
	private ArrayList<Car> cars;
	
	public Simulation(StreetMap map) {
		this.street_map = map;
		this.cars = new ArrayList<Car>();
	}
	
	public void addCar(Car car) {
		this.cars.add(car);	
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
	
		this.addCar(new Car(this.street_map.getIntersection(origin), this.street_map.getIntersection(destination)));
		System.out.println("created new car, x: " + this.street_map.getIntersection(origin).getXCoord() + ", y: " + this.street_map.getIntersection(origin).getYCoord() + ", total: "+ this.getCars().size());
	}
	
	public void generateRandomCars(int n_cars) {
		for (int i = 0; i < n_cars; i++) {
			this.addRandomCar();
		}
	}
	
	public ArrayList<Car> getCars() {
		return this.cars;
	}
	
	public StreetMap getStreetMap() {
		return this.street_map;
	}
	
	public void start() {
		run = true;
		System.out.println("start");
		
		int timesteps = 1000;
		while (timesteps > 0) {

			for (Car car : this.cars) {
				car.update(this.cars);
			}
			
			timesteps--;
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