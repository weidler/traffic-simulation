package core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import algorithms.AStar;
import datastructures.StreetMap;
import graphical_interface.GraphicalInterface;
import datastructures.Car;
import datastructures.Intersection;

public class Simulation {

	private StreetMap street_map;
	private ArrayList<Car> cars;
	private GraphicalInterface gui;
	
	private boolean is_running;
	private double current_time;
	private int slow_mo_factor = 1;

	public Simulation(StreetMap map) {
		this.street_map = map;
		this.cars = new ArrayList<Car>();
		
		this.is_running = false;
		this.current_time = 0;
	}
	public void setGUI(GraphicalInterface gui)
	{
		this.gui = gui;
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
		for ( Car c : this.cars) {
			if (c.getPositionX() == car.getPositionX() && c.getPositionY() == car.getPositionY()) {
				System.out.println("Already a car standing at that position and I ain't stackin' them!");
				return;
			}
		}
		
		this.cars.add(car);					
	}
	
	public void addRandomCar(int counter) {
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
		System.out.println(origin_intersection);
		System.out.println(destination_intersection);
				
		ArrayList<Intersection> shortest_path = AStar.createPath(origin_intersection, destination_intersection, this.street_map);
		System.out.println("Path: " + shortest_path);
		
		this.addCar(new Car(shortest_path, this.street_map,counter));
		System.out.println("created new car, x: " + this.street_map.getIntersection(origin).getXCoord() + ", y: " + this.street_map.getIntersection(origin).getYCoord() + ", total: "+ this.getCars().size());
	}
	
/*	public void generateRandomCars(int n_cars) {
		for (int i = 0; i < n_cars; i++) {
			this.addRandomCar(counter);
		}
	}
*/	
	// SIMULATION
	
	public void start() {
		this.is_running = true;
		
		Thread th = new Thread(()-> {
			System.out.println("start");

			// Initialize
			for (Intersection is : this.street_map.getIntersections()) {
				is.initializeTrafficLightSettings();
			}
			
			double delta_t = 0.01;
			while (this.is_running) {

				System.out.println("\n--------T = " + this.current_time + "s---------");

				ArrayList<Car> arrived_cars = new ArrayList<Car>();
				for (Car car : this.cars) {
					// update traffic light statuses
					this.street_map.update();
					// recalculate car positions
					if (car.update(this.cars, delta_t)) {
						arrived_cars.add(car);
					};
					
					System.out.println(car);
				}
				
				// remove cars that reached their destination from the list
				for (Car c : arrived_cars) {
					this.cars.remove(c);
				}
				
				// Wait for time step to be over
				int ms_to_wait = (int) (delta_t * 1000 * this.slow_mo_factor);
				try {
					TimeUnit.MILLISECONDS.sleep(ms_to_wait);				
				} catch(InterruptedException e) {
					System.out.println("Simulation sleeping (" + ms_to_wait + "ms) got interrupted!");
				}
				
				gui.redraw();
				this.current_time += delta_t;				
			}
		});
		
		th.start();
	}

	public void stop() {
		this.is_running = false;
		System.out.println("stop");
	}
	
	public void reset() {
		this.cars.clear();
	}
}