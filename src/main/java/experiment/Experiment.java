package experiment;

import java.util.ArrayList;

import type.Distribution;
import type.Strategy;

public class Experiment {

	/* EXPERIMENT PARAMETERS */
	private Distribution arrival_generator;
	private Strategy control_strategy;
	private int simulation_length_in_days;
	private boolean vizualise;
	private int numb_runs;

	/* CONTAINERS FOR OVER TIME STATISTICS */
	private ArrayList<ArrayList<Integer>> cars_in_queue = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Double>> avg_speed = new ArrayList<ArrayList<Double>>();
	private ArrayList<Double> measurement_timestamps = new ArrayList<Double>();
	
	/* CONSTRUCTORS */
		
	public Experiment(Distribution arrival_generator, Strategy control_strategy, int simulation_length_in_days,
			boolean vizualise) {
		this.arrival_generator = arrival_generator;
		this.control_strategy = control_strategy;
		this.simulation_length_in_days = simulation_length_in_days;
		this.vizualise = vizualise;
		
		this.numb_runs = 5;
	}

	/** DEFAULT CONSTRUCTOR
	 *  Uses poisson, benchmark, one day and visualizes.
	 */
	public Experiment() {
		this(Distribution.POISSON, Strategy.BENCHMARK_CYCLING, 1, true);
	}

	/* METHODS */
	
	public void addNumberOfCarsInQueue(int number, int run) {
		 if (this.validateGivenRun(run)) {
			 this.cars_in_queue.get(run).add(number);
		 }		
	}
	
	public void addAvgSpeed(double speed, int run) {
		 if (this.validateGivenRun(run)) {
			 this.avg_speed.get(run).add(speed);
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

}
