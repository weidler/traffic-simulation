package core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.swing.JTextArea;
import javax.swing.JTextPane;

import algorithms.AStar;
import car.Car;
import car.Truck;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import graphical_interface.GraphicalInterface;
import graphical_interface.Visuals;
import datastructures.Intersection;

public class Simulation {

	private Properties props;
	
	private StreetMap street_map;
	private ArrayList<Car> cars;
	private GraphicalInterface gui;

	private JTextArea carsTextPane;
	private Car lastHoveredCar;
	
	private boolean showCarInfo = true;
	private boolean is_running;
	private double current_time;
	private float slow_mo_factor = 1;
	private float visualization_frequency = 10; // 1 means each step, e.g. 10 means every 10 steps
	
	// STATISTICS
	private int measurement_interval = 100;
	private double average_velocity;
	private double real_time_utilization; // this is the time used by the simulation as a fraction of the real time for that it simulates
	
	public Simulation(StreetMap map, Properties props) {
		this.props = props;
		
		this.street_map = map;
		this.cars = new ArrayList<Car>();
		
		this.is_running = false;
		this.current_time = 0;
	}
	
	// GETTERS / SETTERS
	public void setCarInfo()
	{
		if(showCarInfo = true)
		{
			showCarInfo = false;
		}
		if(showCarInfo = false)
		{
			showCarInfo = true;
		}
	}
	
	public void setGUI(GraphicalInterface gui) {
		this.gui = gui;
	}
	
	public boolean isRunning() {
		return is_running;
	}
	
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
	
	public void addRandomCar() {
		this.street_map.getIntersections();
		this.street_map.getRoads();
		
		// generate random parameters
		Random r = new Random();
		int origin = r.nextInt(this.street_map.getIntersections().size());
		int destination;
		do {
			destination = r.nextInt(this.street_map.getIntersections().size());
		} while (destination == origin);

		Intersection origin_intersection = this.street_map.getIntersection(origin);
		Intersection destination_intersection = this.street_map.getIntersection(destination);
		ArrayList<Intersection> shortest_path = AStar.createPath(origin_intersection, destination_intersection, this.street_map);
		
		// create vehicle
		Car random_car;
		double type_rand = r.nextDouble();
		if (type_rand <= 0.9) {
			random_car = new Car(shortest_path, this.street_map, this.props);
		} else {
			random_car = new Truck(shortest_path, this.street_map, this.props);
		}
		random_car.setPositionOnRoad(r.nextDouble() * shortest_path.get(0).getRoadTo(shortest_path.get(1)).getLength());
		this.addCar(random_car);

		System.out.println("created new car, x: " + this.street_map.getIntersection(origin).getXCoord() + ", y: " + this.street_map.getIntersection(origin).getYCoord() + ", total: "+ this.getCars().size());
	}

	// SIMULATION
	
	public void start() {
		if (this.is_running) {
			System.out.println("Already Running.");
			return;
		}
		
		this.is_running = true;
		
		Thread th = new Thread(()-> {		
			
			// Initialize
			for (Intersection is : this.street_map.getIntersections()) {
				is.initializeTrafficLightSettings();
			}
					
			double delta_t = 0.01;
			this.visualization_frequency = (int) ((1 / delta_t) / Integer.parseInt(this.props.getProperty("FPS")));
			
			long total_calculation_time = 0;
			int step = 0;
			int resettable_step = 0;
			while (this.is_running) {
				long start_time = System.nanoTime();
			
				// update traffic light statuses
				this.street_map.update(delta_t);

				
				ArrayList<Car> arrived_cars = new ArrayList<Car>();
				for (Car car : this.cars) {
					// recalculate car positions
					if (car.update(this.cars, delta_t)) {
						arrived_cars.add(car);
					};
				}
				
				// remove cars that reached their destination from the list
				for (Car c : arrived_cars) {
					this.cars.remove(c);
				}
				
				//lists the cars
				carsTextPane.setText("");
				if(showCarInfo /*&& step % this.visualization_frequency == 0*/) 
				{
					for(Car car : getCars())
					{
						if(lastHoveredCar == car)
						{
							carsTextPane.setText(carsTextPane.getText()+ "current: " + car.toString() +"\n");
						}
						else
						{
							carsTextPane.setText(carsTextPane.getText()+ car.toString() +"\n");
						}
						
					}
				}
				
				// Wait for time step to be over
				double ns_to_wait = delta_t * 1000000000;
				double ns_used = (System.nanoTime() - start_time);
				total_calculation_time += ns_used;
				try {
					TimeUnit.NANOSECONDS.sleep((int) Math.max(0, ns_to_wait - ns_used));				
				} catch(InterruptedException e) {
					System.out.println("Simulation sleeping (" + ns_to_wait + "ns) got interrupted!");
				}
				
				this.current_time += delta_t;
				
				step++;
				resettable_step++; 
				if (step % this.visualization_frequency == 0) gui.redraw();
				if (step % this.measurement_interval == 0) this.calcStatistics();
				if (step % 100 == 0) {
					this.real_time_utilization = (total_calculation_time / resettable_step) / (delta_t * 1000000000);
					resettable_step = 0;
					total_calculation_time = 0;
					System.out.println(this.real_time_utilization);
				}
			}
		});
		
		th.start();
	}

	public void calcStatistics() {
		double total_velocity = 0;
		for (Car c : this.cars) total_velocity += c.getCurrentVelocity();
		this.average_velocity = total_velocity / this.cars.size();
	}
	
	public void setTextArea(JTextArea p) {
		carsTextPane = p;
	}

	public void setLastHoveredCar(Car c) {
		lastHoveredCar = c;
	}
	
	public void stop() {
		this.is_running = false;
		System.out.println("stop");
	}
	
	public void reset() {
		this.cars.clear();
	}
}