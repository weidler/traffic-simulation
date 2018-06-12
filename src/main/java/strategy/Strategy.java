package strategy;

import car.Car;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import road.Road;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public interface Strategy {

	void configureTrafficLights(HashMap<Road, ArrayList<Car>> list_of_cars, double delta_t);

	void initializeTrafficLightSettings();


}
