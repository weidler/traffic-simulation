package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import algorithms.AstarAdvanced;
import car.Car;
import car.Truck;
import datastructures.StreetMap;
import experiment.Experiment;
import experiment.ExperimentWrapper;
import graphical_interface.ExperimenterPanel;
import graphical_interface.GraphicalInterface;
import graphical_interface.PopulationPanel;
import road.Road;
import schedule.EmpiricalSchedule;
import schedule.GaussianSchedule;
import schedule.PoissonSchedule;
import schedule.Schedule;
import strategy.BasicCycling;
import strategy.Coordinated;
import strategy.InformedCycling;
import strategy.Strategy;
import strategy.WeightedCycling;
import strategy.PriorityCycling;
import type.Distribution;
import type.RoadType;
import type.ZoneType;
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
	private boolean is_running;
	private double current_time;
	private long total_calculation_time;
	private float simulated_seconds_per_real_second = 1;
	private boolean full_speed = false;
	private int visualization_frequency;
	
	private double realistic_time_in_seconds;
	private int days_simulated;
	
	private Experiment current_experiment;
	private ExperimentWrapper experiment_wrapper;

	// PARAMETERS
	private double truck_rate = 0.2;
	
	// STATISTICS
	private int measurement_interval_realistic_time_seconds = 60;

	private int current_run;
	private double avgTravel;
	private ArrayList<Double> travel_times;

	public Simulation(StreetMap map, Properties props) {
		this.props = props;
		this.street_map = map;

		this.current_experiment = new Experiment();
		this.experiment_wrapper = new ExperimentWrapper();
		this.experiment_wrapper.addExperiment(current_experiment);

		initializeSimulationParameters();
	}

	private void initializeSimulationParameters() {
		this.cars = new HashMap<Road, ArrayList<Car>>();
		this.car_sink = new ArrayList<Car>();

		this.travel_times  = new ArrayList();

		this.current_run = 0;
		this.days_simulated = 0;
		this.current_time = 0;

		this.avgTravel = 0;
		this.travel_times.clear();

		this.applyExperimentalSettings();
	}

	// GETTERS / SETTERS
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

	public void setCurrentExperiment(Experiment exp) {
		this.current_experiment = exp;
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
		
		Intersection destination_intersection = null;
		Intersection[] options = null;
		Intersection destination_intersection2=null;
		Intersection destination_intersection1 =null;
		Intersection origin_intersection = null;

		double time = realistic_time_in_seconds;
		
		Random rand = new Random();
		int origin = this.street_map.getIntersectionIdByCoordinates(r.getX1(), r.getY1());
		int destination = -1;
		int roadToGet = -1;
		// generate random parameters
		while (destination_intersection == origin_intersection  || destination_intersection.isInHighway())
		{
			ArrayList<Road> targets = new ArrayList<>();
		if(time > 21*60*60 || time < 7*60*60) {
			do {
				destination = rand.nextInt(this.street_map.getRoads().size());
			} while (street_map.getRoads().get(destination).getRoadType() == RoadType.HIGHWAY);
				
		
		}
		else if(time > 7*60*60 && time < 12*60*60) {
			do {
				double ran = Math.random();
				if (ran < 0.8 && (street_map.getRoadsByZone().get(ZoneType.INDUSTRIAL).size() != 0 || street_map.getRoadsByZone().get(ZoneType.COMMERCIAL).size() != 0) ) {
					
					targets.addAll(street_map.getRoadsByZone().get(ZoneType.INDUSTRIAL));
					targets.addAll(street_map.getRoadsByZone().get(ZoneType.COMMERCIAL));
				}
				else
				{
					targets.addAll(street_map.getRoadsByZone().get(ZoneType.MIXED));
					targets.addAll(street_map.getRoadsByZone().get(ZoneType.RESIDENTIAL));
				}
				int randomest = (int) Math.floor((Math.random() * targets.size()));
				roadToGet = randomest;
			} while (targets.get(roadToGet).getRoadType() == RoadType.HIGHWAY);
			
		}
		else {
			do {
				double ran = Math.random();
				if (ran < 1 && (street_map.getRoadsByZone().get(ZoneType.MIXED).size() != 0 || street_map.getRoadsByZone().get(ZoneType.RESIDENTIAL).size() != 0) ) {
					targets.addAll(street_map.getRoadsByZone().get(ZoneType.MIXED));
					targets.addAll(street_map.getRoadsByZone().get(ZoneType.RESIDENTIAL));
				}
				else
				{
	
					targets.addAll(street_map.getRoadsByZone().get(ZoneType.INDUSTRIAL));
					targets.addAll(street_map.getRoadsByZone().get(ZoneType.COMMERCIAL));
				}
				int randomest = (int) Math.floor((Math.random() * targets.size()));
				
				roadToGet = randomest;
			} while (targets.get(roadToGet).getRoadType() == RoadType.HIGHWAY);
		}
		
		int targetIntersection = 0;
		double rands = Math.random();
		

		
		if(roadToGet != -1) {
			 options = targets.get(roadToGet).getIntersections();
		}
		else
		{
			options = street_map.getRoads().get(destination).getIntersections();
		}		
		if(rands<0.5)
			{
				targetIntersection = 1;
			}
			destination_intersection1 = options[targetIntersection];
		
		
		
		
		if(destination_intersection1 != null)
		{//System.out.println("target1");
			destination_intersection = destination_intersection1;
		}
		else
		{
			//System.out.println("target2");
			destination_intersection = destination_intersection2;
		}
		
		origin_intersection = this.street_map.getIntersection(origin);

		}
		
		//System.out.println("Origin: " + origin_intersection);
		//System.out.println("Destination: " + destination_intersection);
		//System.out.println("before");
		ArrayList<Intersection> shortest_path = AstarAdvanced.createPath(origin_intersection, destination_intersection,
				this.street_map, cars, "Empirical");
		
		if(options[0] == shortest_path.get(shortest_path.size()-1))
		{
			if(options[1] == shortest_path.get(shortest_path.size()-2))
			{
				shortest_path = shortest_path;
			}
			else
			{
				shortest_path.add(options[1]);
			}
		}
		else
		{
			if(options[0] == shortest_path.get(shortest_path.size()-2))
			{
				shortest_path = shortest_path;
			}
			else
			{
				shortest_path.add(options[0]);
			}
		}
		
		//System.out.println("after");
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
		int phaseLength = current_experiment.getPhaseLength();

		// Arrival Distribution
		if (this.current_experiment.getArrivalGenerator() == Distribution.EMPIRICAL) {
			this.simulation_schedule = new EmpiricalSchedule(this.street_map, this.current_experiment.getIaTime(), "data/test.json");
		} else if (this.current_experiment.getArrivalGenerator() == Distribution.POISSON) {
			this.simulation_schedule = new PoissonSchedule(this.street_map, 50);
		} else if (this.current_experiment.getArrivalGenerator() == Distribution.GAUSSIAN) {
			this.simulation_schedule = new GaussianSchedule(this.street_map, 50, 5);
		}

		// Strategy
		if (this.current_experiment.getControlStrategy() == type.Strategy.BASIC_CYCLING) {
			this.strategy = new BasicCycling(phaseLength, street_map);
		} else if(this.current_experiment.getControlStrategy() == type.Strategy.PRIORITY_CYCLING) {
			this.strategy = new PriorityCycling(phaseLength, street_map);
		} else if(this.current_experiment.getControlStrategy() == type.Strategy.COORDINATED) {
			this.strategy = new Coordinated(phaseLength, street_map);
		} else if(this.current_experiment.getControlStrategy() == type.Strategy.WEIGHTED_CYCLING) {
			this.strategy = new WeightedCycling(phaseLength, street_map);
		} else if (this.current_experiment.getControlStrategy() == type.Strategy.INFORMED_CYCLING) {
			this.strategy = new InformedCycling(phaseLength, street_map);
		} else {
			this.strategy = new BasicCycling(phaseLength, street_map);
		}
	}

	// SIMULATION

	public void start() {
		if (street_map.getIntersections().size() > 0)
		{
			if (this.is_running) {
				System.out.println("Already Running.");
				return;
			}

			street_map.allocateRoadsByZone();
			this.updateCarListToMap();
			this.simulation_schedule.updateToMap();
			this.current_experiment = this.experiment_wrapper.currentExperiment();
			this.applyExperimentalSettings();
	
			this.is_running = true;
	
			Thread th = new Thread(() -> {
				// Initialize
	
				this.strategy.initializeTrafficLightSettings();
	
				double delta_t = 0.05; // in seconds
				this.adjustVisualizationFrequency(delta_t);
				this.total_calculation_time = 0;
				int step = 0;
	
				while (this.is_running && days_simulated < this.current_experiment.getSimulationLengthInDays()) {
					start_time = System.nanoTime();
					street_map.setCurrentTime(current_time);
	
					// update realistic time
					realistic_time_in_seconds += delta_t;
					if (Time.secondsToHours(realistic_time_in_seconds) >= 24) {
						realistic_time_in_seconds = realistic_time_in_seconds - Time.hoursToSeconds(24);
						days_simulated++;
					}
	
					// spawn new cars to the roads according to the schedule
					for (Road r : this.street_map.getRoads()) {
						if(r.getRoadType() != RoadType.HIGHWAY) {
							if (simulation_schedule.carWaitingAt(r, this.current_time)) {
								if (r.getAvailabePopulation() > 0) {
									this.addCarAtRoad(r);
									r.decrementAvailablePopulation();
								}
								simulation_schedule.drawNextCarAt(r, realistic_time_in_seconds);
							}
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
								Road destination_road = car.getCurrentDestinationIntersection().getRoadTo(car.getCurrentOriginIntersection());
								destination_road.incrementAvailablePopulation();
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
					double ns_used = (System.nanoTime() - start_time);
					total_calculation_time += ns_used;
					if (!full_speed) {
						double ns_to_wait = Time.secondsToNanoseconds(delta_t/simulated_seconds_per_real_second);
						try {
							TimeUnit.NANOSECONDS.sleep((int) Math.max(0, ns_to_wait - ns_used));
						} catch (InterruptedException e) {
							System.out.println("Simulation sleeping (" + ns_to_wait + "ns) got interrupted!");
						}
					}
	
					this.current_time += delta_t;
	
					// update graphics and statistics
					step++;
					if (step % this.visualization_frequency == 0 && this.current_experiment.isVizualise()) {
						gui.redraw();
						((PopulationPanel) gui.getPopulationPanel()).updateCharts();
					}
					if (this.current_time % this.measurement_interval_realistic_time_seconds < delta_t) this.calcStatistics(); // hacky, but avoids double inprecision porblems
				}

				// Finish this experiment
				this.experiment_wrapper.finishExperiment(current_experiment);
				((ExperimenterPanel) this.gui.experimenterPanel).updateList();
				current_experiment = this.experiment_wrapper.currentExperiment();
				stop();

				System.out.println(experiment_wrapper.getRemainingExperiments());

				if (!experiment_wrapper.isAllFinished() && !(current_experiment == null)) {
					reset();
					start();
				} else {
					this.experiment_wrapper.createFinalReport();
					this.experiment_wrapper.saveAll();
				}
			});
	
			th.start();
		}
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
		this.current_experiment.addNumberOfCarsInQueue(cars_in_queue/this.getNumbCars(), this.current_run);
		this.current_experiment.addAvgSpeed(total_velocity/this.getNumbCars(), this.current_run);
		this.current_experiment.addNumbCars(this.getNumbCars(), this.current_run);
		if (this.current_run == 0) this.current_experiment.addTimestep(this.current_time);
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
	}
	
	public int getNumberOfCarsOutOfTraffic() {
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
		System.out.println("Calculation Time: " + Time.nanosecondsToSeconds(this.total_calculation_time) + " seconds (without visualization delay)");
		this.is_running = false;
	}

	public void reset() {
		initializeSimulationParameters();
	}

	public void fullReset() {
		this.current_experiment = new Experiment();
		this.experiment_wrapper = new ExperimentWrapper();
		this.experiment_wrapper.addExperiment(current_experiment);

		initializeSimulationParameters();
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

	public ExperimentWrapper getExperimentWrapper() {
		return this.experiment_wrapper;
	}

	public boolean getFullSpeed() {
		return this.full_speed;
	}
}