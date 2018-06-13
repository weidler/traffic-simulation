package strategy;

import car.Car;
import road.Road;

import java.util.ArrayList;
import java.util.HashMap;

public interface Strategy {

	void configureTrafficLights(HashMap<Road, ArrayList<Car>> list_of_cars, double delta_t);

	void initializeTrafficLightSettings();

}
