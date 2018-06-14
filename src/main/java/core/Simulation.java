package core;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import algorithms.AStar;
import algorithms.AstarAdvanced;
import algorithms.CoordinatedTrafficLights;
import car.Car;
import car.Truck;
import datastructures.StreetMap;
import experiment.Experiment;
import graphical_interface.GraphicalInterface;
import org.apache.commons.lang3.SystemUtils;
import road.Road;
import schedule.EmpiricalSchedule;
import schedule.GaussianSchedule;
import schedule.PoissonSchedule;
import schedule.Schedule;
import strategy.BasicCycling;
import strategy.Coordinated;
import strategy.InformedCycling;
import strategy.Strategy;
import strategy.WaitingCycling;
import strategy.WeightedCycling;
import type.Distribution;
import util.Statistics;
import util.Time;
import datastructures.Intersection;

public class Simulation {

	private Properties props;

	private StreetMap street_map;
	private HashMap<Road, ArrayList<Car>> cars;
	private ArrayList<Car> car_sink;
	private GraphicalInterface gui;
	private Schedule simulation_schedule;
	private Strategy strategy;
	
	private long start_time;
	private boolean showCarInfo = true;
	private boolean is_running;
	private double current_time;
	private float simulated_seconds_per_real_second = 1;
	private boolean full_speed = false;
	private int visualization_frequency;
	
	private double realistic_time_in_seconds;
	private int days_simulated;
	
	private Experiment experiment;
	
	private String name;

	// PARAMETERS
	private double truck_rate = 0.2;
	
	// STATISTICS
	private int measurement_interval_realistic_time_seconds = 60;

	private int current_run;
	private double avgTravel;
	private ArrayList<Double> travel_times = new ArrayList();

	public Simulation(StreetMap map, Properties props) {
		this.props = props;

		this.street_map = map;
		this.cars = new HashMap<Road, ArrayList<Car>>();
		this.car_sink = new ArrayList<Car>();

		this.is_running = false;
		this.current_time = 0;
		this.realistic_time_in_seconds = 0;

		this.experiment = new Experiment();
		this.applyExperimentalSettings();
	}

	// GETTERS / SETTERS
	public void setCarInfo() {
		showCarInfo = !showCarInfo;
	}

	public void setGUI(GraphicalInterface gui) {
		this.gui = gui;
	}

	public boolean isRunning() {
		return is_running;
	}

	public HashMap<Road, ArrayList<Car>> getCars() {
		return this.cars;
	}

	public StreetMap getStreetMap() {
		return this.street_map;
	}

	public void setExperiment(Experiment exp) {
		this.experiment = exp;
	}
	public long getStartTime() {
		return this.start_time;
	}

	// ACTIONS

	public float getSimulatedSecondsperRealSecond() {
		return simulated_seconds_per_real_second;
	}

	public void setSimulatedSecondsPerRealSecond(float simulated_seconds_per_real_second) {
		this.simulated_seconds_per_real_second = simulated_seconds_per_real_second;
	}

	public void addCar(Car car) {
		this.cars.get(car.getCurrentRoad()).add(car);
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
		ArrayList<Intersection> shortest_path = AstarAdvanced.createPath(origin_intersection, destination_intersection,
				this.street_map, cars, "Empirical");


		// create vehicle
		Car random_car;
		double type_rand = r.nextDouble();
		if (type_rand <= 1 - this.truck_rate) {
			random_car = new Car(shortest_path, this.current_time, this.props);
		} else {
			random_car = new Truck(shortest_path, this.current_time, this.props);
		}
		random_car.setPositionOnRoad(r.nextDouble() * shortest_path.get(0).getRoadTo(shortest_path.get(1)).getLength());
		this.addCar(random_car);
	}

	public void addCarAtRoad(Road r) {
		this.street_map.getIntersections();
		this.street_map.getRoads();

		// generate random parameters
		Random rand = new Random();
		int origin = this.street_map.getIntersectionIdByCoordinates(r.getX1(), r.getY1());
		int destination;
		do {
			destination = rand.nextInt(this.street_map.getIntersections().size());
		} while (destination == origin);

		Intersection origin_intersection = this.street_map.getIntersection(origin);
		Intersection destination_intersection = this.street_map.getIntersection(destination);
		ArrayList<Intersection> shortest_path = AstarAdvanced.createPath(origin_intersection, destination_intersection,
				this.street_map, cars, "Empirical");

		// create vehicle
		Car random_car;
		double type_rand = rand.nextDouble();
		if (type_rand <= 1 - this.truck_rate) {
			random_car = new Car(shortest_path, this.current_time, this.props);
		} else {
			random_car = new Truck(shortest_path, this.current_time, this.props);
		}

		random_car.setPositionOnRoad(
				rand.nextDouble() * shortest_path.get(0).getRoadTo(shortest_path.get(1)).getLength());
		this.addCar(random_car);
	}

	public void applyExperimentalSettings() {
		// Arrival Distribution
		if (this.experiment.getArrivalGenerator() == Distribution.EMPIRICAL) {
			this.simulation_schedule = new EmpiricalSchedule(this.street_map, this.experiment.getIaTime(), "data/test.json");
		} else if (this.experiment.getArrivalGenerator() == Distribution.POISSON) {
			this.simulation_schedule = new PoissonSchedule(this.street_map, 50);
		} else if (this.experiment.getArrivalGenerator() == Distribution.GAUSSIAN) {
			this.simulation_schedule = new GaussianSchedule(this.street_map, 50, 5);
		}

		// Strategy
		if (this.experiment.getControlStrategy() == type.Strategy.BENCHMARK_CYCLING) {
			this.strategy = new BasicCycling(15, street_map);
		} else if(this.experiment.getControlStrategy() == type.Strategy.WEIGHTED_CYCLING) {

			this.strategy = new WeightedCycling(5, street_map);

		}
		else if(this.experiment.getControlStrategy() == type.Strategy.COORDINATED)
		{
			this.strategy = new Coordinated(1, street_map);
		}
		else if(this.experiment.getControlStrategy() == type.Strategy.WAITING)
		{
			this.strategy = new WaitingCycling(5, street_map);
		}
		else if (this.experiment.getControlStrategy() == type.Strategy.INFORMED_CYCLING) {

			this.strategy = new InformedCycling(15, street_map);
		} else {
			this.strategy = new BasicCycling(15, street_map);
		}
	}

	// SIMULATION

	public void start() {

		this.updateCarListToMap();
		this.simulation_schedule.updateToMap();
		this.applyExperimentalSettings();
		System.out.println(this.simulation_schedule);

		if (this.is_running) {
			System.out.println("Already Running.");
			return;
		}

		this.is_running = true;

		Thread th = new Thread(() -> {
			// Initialize

			this.strategy.initializeTrafficLightSettings();

			double delta_t = 0.05; // in seconds
			this.adjustVisualizationFrequency(delta_t);
			long total_calculation_time = 0;
			int step = 0;
			int resettable_step = 0;
			
			while (this.is_running && days_simulated < this.experiment.getSimulationLengthInDays()) {
				start_time = System.nanoTime();
				street_map.setCurrentTime(current_time);

				// update realistic time
				realistic_time_in_seconds += delta_t;
				if (Time.secondsToHours(realistic_time_in_seconds) >= 24) {
					realistic_time_in_seconds = realistic_time_in_seconds - Time.hoursToSeconds(24);
					days_simulated++;
				}

				// add new cars to the roads according to the schedule
				for (Road r : this.street_map.getRoads()) {
					if (simulation_schedule.carWaitingAt(r, this.current_time)) {
						this.addCarAtRoad(r);
						simulation_schedule.drawNextCarAt(r, realistic_time_in_seconds);
					}
				}

				// update traffic light statuses
				this.strategy.configureTrafficLights(this.cars, delta_t);


				// update car positions
				ArrayList<Car> arrived_cars = new ArrayList<Car>();
				for (Road road : this.cars.keySet()) {
					ArrayList<Car> cars = this.cars.get(road);
					for (Car car : cars) {
						// recalculate car position
						if (car.update(this.cars, delta_t, current_time)) {
							arrived_cars.add(car);
						}
					}
				}

				// update carlist for road assignments
				HashMap<Road, ArrayList<Car>> new_cars = new HashMap<Road, ArrayList<Car>>();
				for (Road r : cars.keySet()) {
					new_cars.put(r, new ArrayList<Car>());
				}

				for (Road r : cars.keySet()) {
					for (Car c : cars.get(r)) {
						new_cars.get(c.getCurrentRoad()).add(c);
					}
				}
				cars = new_cars;

				// remove cars that reached their destination from the list
				for (Car c : arrived_cars) {
					this.cars.get(c.getCurrentRoad()).remove(c);
					this.car_sink.add(c);
					c.setArrivalTime(this.current_time);
				}

				// Wait for time step to be over
				if (!full_speed) {
					double ns_to_wait = Time.secondsToNanoseconds(delta_t/simulated_seconds_per_real_second);
					double ns_used = (System.nanoTime() - start_time);
					total_calculation_time += ns_used;
					try {
						TimeUnit.NANOSECONDS.sleep((int) Math.max(0, ns_to_wait - ns_used));
					} catch (InterruptedException e) {
						System.out.println("Simulation sleeping (" + ns_to_wait + "ns) got interrupted!");
					}
				}

				this.current_time += delta_t;

				// update graphics and statistics
				step++;
				resettable_step++;
				if (step % this.visualization_frequency == 0 && this.experiment.isVizualise()) gui.redraw();
				if (this.current_time % this.measurement_interval_realistic_time_seconds < delta_t) this.calcStatistics(); // hacky, but avoids double inprecision porblems
			}
			stop();
			
		});

		th.start();
	}

	private void adjustVisualizationFrequency(double delta_t) {
		this.visualization_frequency = Math.max(1, (int) ((1 / delta_t) / Integer.parseInt(this.props.getProperty("FPS"))));
	}

	public void calcStatistics() {
		double total_velocity = 0;
		double cars_in_queue = 0;

		for (ArrayList<Car> cars : this.cars.values()) {
			for (Car c : cars) {
				total_velocity += c.getCurrentVelocity();
				if (c.isWaiting()) cars_in_queue++;
			}
		}

		// ADD STATISTICS TO EXPERIMENT
		this.experiment.addNumberOfCarsInQueue(cars_in_queue/this.cars.size(), this.current_run);
		this.experiment.addAvgSpeed(total_velocity/this.cars.size(), this.current_run);
		this.experiment.addNumbCars(this.getNumbCars(), this.current_run);
		if (this.current_run == 0) this.experiment.addTimestep(this.current_time);
	}
	
	private void reportStatistics() {
		for (int i = 0; i < street_map.getRoads().size(); i++) {
			System.out.println("Road " + i + " has an avg speed of: " + street_map.getRoads().get(i).getAverageSpeed());
		}
		
		travel_times = new ArrayList<Double>();
		ArrayList<Double> fractional_waiting_times = new ArrayList<Double>();
		for (Car c : this.car_sink) {
			Double travel_time =  (c.getArrivalTime() - c.getDepartureTime());
			travel_times.add(travel_time);
			fractional_waiting_times.add(c.getTotalWaitingTime() / travel_time);
		}
		avgTravel = Statistics.mean(travel_times);
		System.out.println("Average Travel Time: " + avgTravel);
		System.out.println("Average Fractional Waiting Time: " + Statistics.mean(fractional_waiting_times));

		// Write Data
		PrintWriter report_writer;
		try {
			report_writer = new PrintWriter("./simulation-reports/csv-data/" + name + ".csv", "UTF-8");
			
			String sep = ";";
			report_writer.println("time" + sep + "avg_velo" + sep + "frac_wait" + sep + "numb_cars");
			for (int i = 0; i < this.experiment.getMeasurementTimestamps().size(); i++) {
				report_writer.println(
						this.experiment.getMeasurementTimestamps().get(i) + sep + 
						this.experiment.getAvgSpeed().get(0).get(i) + sep + 
						this.experiment.getFractionsOfWaitingCars().get(0).get(i) + sep +
						this.experiment.getNumbCars().get(0).get(i)
				);
			}
			
			report_writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create Graphical Report
		String command = "simulation-reports/create_report.sh " + name;
		Process p;

		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();

/*
			if (SystemUtils.IS_OS_LINUX) {
				Runtime.getRuntime().exec("xdg-open simulation-reports/" + name + ".html");
			} else if (SystemUtils.IS_OS_WINDOWS) {
				Runtime.getRuntime().exec("start simulation-reports/" + name + ".html");
			}*/

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public int getNumberCarsOutOfTraffic() {
		int sum = 0;
		for (ArrayList<Car> cars : this.cars.values()) {
			for (Car c : cars) {
				if (!c.inTraffic()) sum++;
			}
		}

		return sum;
	}

	public void updateCarListToMap() {
		for (Road road : this.street_map.getRoads()) {
			if (!this.cars.containsKey(road)) this.cars.put(road, new ArrayList<>());
		}
	}

	public void stop() {
		JPanel thisPanel = new JPanel();
		JTextField name = new JTextField(5);
		thisPanel.add(new JLabel("File name:"));
		thisPanel.add(name);
		
		int result = JOptionPane.showConfirmDialog(null, thisPanel, "Please Enter data",
				JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			setFileName(name.getText());
		}
		this.is_running = false;
		this.reportStatistics();
	}

	public void reset() {
		this.cars.clear();
		this.car_sink.clear();
	}

	public double getRealisticTime() {
		return this.realistic_time_in_seconds;
	}

	public int getCurrentDay() {
		return this.days_simulated;
	}

	public int getNumbCars() {
		int total = 0;
		for (ArrayList<Car> al : cars.values()) total += al.size();
		return total;
	}

	public boolean isFullSpeed() {
		return full_speed;
	}

	public void setFullSpeed(boolean full_speed) {
		this.full_speed = full_speed;
	}
	public void setFileName(String name) {
	this.name = name;
	}
}