package strategy;

import car.Car;
import datastructures.Intersection;
import road.Road;

import java.util.ArrayList;
import java.util.HashMap;

public class Prostata implements Strategy {

	private double alpha;
	private double delta_g;
	private double delta_r;
	private double g_min, g_max;
	private double r_min, r_max;

	@Override
	public void configureTrafficLights(HashMap<Road, ArrayList<Car>> list_of_cars, double delta_t) {

	}

	@Override
	public void initializeTrafficLightSettings() {

	}


	/* COEFFICIENT CALCULATORS */

	public double coefficientER(Intersection n, Road r, double time, double cycle) {
		return 0;
	}

	public double coefficientEG(Intersection n, Road r, double time, double cycle) {
		return 0;
	}

	public double coefficientCI(Intersection n, Road r) {
		return 0;
	}

	public double coefficientS(Road r) {
		return r.getLanes();
	}




	public static void main(String[] args) {

	}
}
