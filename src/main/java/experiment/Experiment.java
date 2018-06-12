package experiment;

import java.util.ArrayList;

import type.Distribution;
import type.Strategy;

public class Experiment {

	/* EXPERIMENT PARAMETERS */
	private Distribution arrival_generator;
	private Strategy control_strategy;
	private int simulation_length_in_days;
	private int iaTime;


	private boolean vizualise;
	private int numb_runs;

	/* CONTAINERS FOR OVER TIME STATISTICS */
	private ArrayList<ArrayList<Double>> fractions_of_waiting_cars = new ArrayList<ArrayList<Double>>();
	private ArrayList<ArrayList<Double>> avg_speed = new ArrayList<ArrayList<Double>>();
	private ArrayList<ArrayList<Integer>> numb_cars = new ArrayList<ArrayList<Integer>>();
	private ArrayList<Double> measurement_timestamps = new ArrayList<Double>();
	
	/* CONSTRUCTORS */
		
	public Experiment(Distribution arrival_generator, Strategy control_strategy, int simulation_length_in_days,
			boolean vizualise, int meanIA) {
		this.arrival_generator = arrival_generator;
		this.control_strategy = control_strategy;
		this.simulation_length_in_days = simulation_length_in_days;
		this.vizualise = vizualise;
		this.iaTime = meanIA;
		
		this.numb_runs = 5;
		for (int i = 0; i < this.numb_runs; i++) this.addRun();
	}

	/** DEFAULT CONSTRUCTOR
	 *  Uses poisson, benchmark, one day and visualizes.
	 */
	public Experiment() {
		this(Distribution.EMPIRICAL, Strategy.BENCHMARK_CYCLING, 1, true, 30);
	}

	/* METHODS */
	
	public void addNumberOfCarsInQueue(double number, int run) {
		 if (this.validateGivenRun(run)) {
			 this.fractions_of_waiting_cars.get(run).add(number);
		 }		
	}
	
	public void addAvgSpeed(double speed, int run) {
		 if (this.validateGivenRun(run)) {
			 this.avg_speed.get(run).add(speed);
		 }		
	}
	
	public void addNumbCars(int numb_cars, int run) {
		if (this.validateGivenRun(run)) {
			 this.numb_cars.get(run).add(numb_cars);
		 }
	}
	
	public void addTimestep(double timestep) {
		 this.measurement_timestamps.add(timestep);		
	}
	
	public boolean validateGivenRun(int run) {
		if (run > this.getNumberOfRuns()) {
			System.out.println("[DEBUG] Invalid Run Index.");
			return false;
		}
		
		return true;
	}
	
	public void addRun() {
		fractions_of_waiting_cars.add(new ArrayList<Double>());
		avg_speed.add(new ArrayList<Double>());
		numb_cars.add(new ArrayList<Integer>());
	}
	
	/* SETTERS AND GETTERS */
	
	public int getNumberOfRuns() {
		return numb_runs;
	}
	
	public Distribution getArrivalGenerator() {
		return arrival_generator;
	}

	public Strategy getControlStrategy() {
		return control_strategy;
	}

	public int getSimulationLengthInDays() {
		return simulation_length_in_days;
	}

	public boolean isVizualise() {
		return vizualise;
	}

	public void setArrivalGenerator(Distribution arrival_generator) {
		this.arrival_generator = arrival_generator;
	}

	public void setControlStrategy(Strategy control_strategy) {
		this.control_strategy = control_strategy;
	}

	public void setSimulationLengthInDays(int simulation_length_in_days) {
		this.simulation_length_in_days = simulation_length_in_days;
	}

	public void setVizualise(boolean vizualise) {
		this.vizualise = vizualise;
	}
	
	public ArrayList<ArrayList<Double>> getFractionsOfWaitingCars() {
		return fractions_of_waiting_cars;
	}

	public ArrayList<ArrayList<Double>> getAvgSpeed() {
		return avg_speed;
	}

	public ArrayList<Double> getMeasurementTimestamps() {
		return measurement_timestamps;
	}
	
	public ArrayList<ArrayList<Integer>> getNumbCars() {
		return numb_cars;
	}
	public int getIaTime() {
		return iaTime;
	}

	public void setIaTime(int iaTime) {
		this.iaTime = iaTime;
	}
}
