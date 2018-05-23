package experiment;

import type.Distribution;
import type.Strategy;

public class Experiment {

	private Distribution arrival_generator;
	private Strategy control_strategy;
	private int simulation_length_in_days;
	private boolean vizualise;

	public Experiment(Distribution arrival_generator, Strategy control_strategy, int simulation_length_in_days,
			boolean vizualise) {
		this.arrival_generator = arrival_generator;
		this.control_strategy = control_strategy;
		this.simulation_length_in_days = simulation_length_in_days;
		this.vizualise = vizualise;
	}

	public Experiment() {
		this.arrival_generator = Distribution.POISSON;
		this.control_strategy = Strategy.BENCHMARK_CYCLING;
		this.simulation_length_in_days = 10;
		this.vizualise = true;
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
