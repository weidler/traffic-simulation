package core;

import java.util.ArrayList;

import datastructures.StreetMap;
import datastructures.Car;

public class Simulation {

	private boolean run = false;
	
	private StreetMap street_map;
	private ArrayList<Car> cars;
	
	public Simulation() {
		
	}
	
	public void start() {
		run = true;
		System.out.println("start");
	}

	public void stop() {
		run = false;
		System.out.println("stop");
	}
}