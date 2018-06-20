package experiment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.commons.lang3.SystemUtils;
import type.Distribution;
import type.Strategy;

import javax.swing.*;

public class Experiment {

	/* EXPERIMENT PARAMETERS */
	private Distribution arrival_generator;
	private Strategy control_strategy;
	private int simulation_length_in_days;
	private int iaTime;
	private int phaseLength;

	private boolean vizualise;
	private int numb_runs;

	private String name;

	/* CONTAINERS FOR OVER TIME STATISTICS */
	private ArrayList<ArrayList<Double>> fractions_of_waiting_cars = new ArrayList<ArrayList<Double>>();
	private ArrayList<ArrayList<Double>> avg_speed = new ArrayList<ArrayList<Double>>();
	private ArrayList<ArrayList<Integer>> numb_cars = new ArrayList<ArrayList<Integer>>();
	private ArrayList<Double> measurement_timestamps = new ArrayList<Double>();
	
	/* CONSTRUCTORS */
	public Experiment(Distribution arrival_generator, Strategy control_strategy, int simulation_length_in_days,
			boolean vizualise, int meanIA, int phaseLength) {
		this.arrival_generator = arrival_generator;
		this.control_strategy = control_strategy;
		this.simulation_length_in_days = simulation_length_in_days;
		this.vizualise = vizualise;
		this.iaTime = meanIA;
		this.phaseLength = phaseLength;
		this.name = "";
		
		this.numb_runs = 5;
		for (int i = 0; i < this.numb_runs; i++) this.addRun();
	}

	public Experiment() {
		this(Distribution.EMPIRICAL, Strategy.WEIGHTED_CYCLING, 1, true, 30, 60);
		this.name = "Default";
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

	/* ACTIONS */
	public void save() {
		System.out.println(name);
		if (this.name == null || this.name.isEmpty()) {
			JPanel thisPanel = new JPanel();
			JTextField name_field = new JTextField(5);
			thisPanel.add(new JLabel("File name:"));
			thisPanel.add(name_field);

			int result = JOptionPane.showConfirmDialog(null, thisPanel, "Please Enter a File Name", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				this.name = name_field.getText();
			}
		}

		// WRITE CSV
		PrintWriter report_writer;
		try {
			report_writer = new PrintWriter("./simulation-reports/csv-data/" + name + ".csv", "UTF-8");

			String sep = ";";
			report_writer.println("time" + sep + "avg_velo" + sep + "frac_wait" + sep + "numb_cars");
			for (int i = 0; i < this.getMeasurementTimestamps().size(); i++) {
				report_writer.println(
						this.getMeasurementTimestamps().get(i) + sep +
								this.getAvgSpeed().get(0).get(i) + sep +
								this.getFractionsOfWaitingCars().get(0).get(i) + sep +
								this.getNumbCars().get(0).get(i)
				);
			}

			report_writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Create Graphical Report
		String command = "simulation-reports/create_report.sh " + name;
		Process p, q;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();


			if (SystemUtils.IS_OS_LINUX) {
				System.out.println("LINUX IS LIFE");
				q = Runtime.getRuntime().exec("xdg-open simulation-reports/html-reports/" + name + ".html");
				q.waitFor();
			} else if (SystemUtils.IS_OS_WINDOWS) {
				q = Runtime.getRuntime().exec("start simulation-reports/html-reports/" + name + ".html");
				q.waitFor();
			} else {
				System.out.println("OS NOT SUPPORTED");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/* SETTERS AND GETTERS */
	public int getPhaseLength()
	{
		return phaseLength;
	}
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
