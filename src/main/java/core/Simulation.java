package core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import a_star_stuff.AStarSearchContext;
import a_star_stuff.Astar2;
import a_star_stuff.Cost;
import a_star_stuff.Estimation;
import a_star_stuff.Expansion;
import a_star_stuff.SearchContext;
import datastructures.StreetMap;
import datastructures.Car;
import datastructures.Intersection;

public class Simulation {

	private boolean run = false;
	
	private StreetMap street_map;
	private ArrayList<Car> cars;
	private Astar2 aStar;
	
	public Simulation(StreetMap map) {
		this.street_map = map;
		this.cars = new ArrayList<Car>();
		this.aStar = new Astar2(map);
	}
	
	// GETTERS / SETTERS
	
	public ArrayList<Car> getCars() {
		return this.cars;
	}
	
	public StreetMap getStreetMap() {
		return this.street_map;
	}
	
	// ACTIONS
	
	public void addCar(Car car) {
		this.cars.add(car);	
		for(Intersection intersection : street_map.getIntersections())
		{
			intersection.resetParent();
			intersection.resetCost();
		}
	}
	
	public void addRandomCar() {
		this.street_map.getIntersections();
		this.street_map.getRoads();
		
		Random r = new Random();
		int origin = r.nextInt(this.street_map.getIntersections().size());
		int destination;
		do {
			destination = r.nextInt(this.street_map.getIntersections().size());
		} while (destination == origin);
		Intersection origin_intersection = this.street_map.getIntersection(origin);
		Intersection destination_intersection = this.street_map.getIntersection(destination);
		
		this.aStar.setStart(origin_intersection);
		this.aStar.setEnd(destination_intersection);
		
		ArrayList<Intersection> shortest_path = this.aStar.createPath();
		System.out.println("Path: " + shortest_path);
		
		this.addCar(new Car(origin_intersection, destination_intersection, this.street_map));
		System.out.println("created new car, x: " + this.street_map.getIntersection(origin).getXCoord() + ", y: " + this.street_map.getIntersection(origin).getYCoord() + ", total: "+ this.getCars().size());
	}
	
	public void generateRandomCars(int n_cars) {
		for (int i = 0; i < n_cars; i++) {
			this.addRandomCar();
		}
	}
	
	// SIMULATION
	
	public void start() {
		run = true;
		System.out.println("start");

		// Initialize
		for (Intersection is : this.street_map.getIntersections()) {
			is.initializeTrafficLightSettings();
		}
		
		int t = 1;
		int timesteps = 1000;
		while (t < timesteps) { // ultimately, have some sort of "simulation finished" function here checking if everybody arrived

			for (Car car : this.cars) {
				// update traffic light statuses
				this.street_map.update();
				// recalculate car positions
				car.update(this.cars, t);
				
				System.out.println(car);
			}
			
			t++;
		}

		////////////
		Expansion<SearchContext<StreetMap, Intersection>, Intersection> expansionFcn =
				new Expansion<SearchContext<StreetMap, Intersection>, Intersection>() {

					@Override
					public Iterable<Intersection> expand(SearchContext<StreetMap, Intersection> c, Intersection p) {
						// as an example:
						//  c.domain() returns you the StreetMap you will pass it
						//  c.start()/c.target() returns you the endpoints so you can also use them (you would use the target position in the estimation probably to get some distance)
						StreetMap ourMap = c.domain();
						
						List<Intersection> neighboringIntersections = 
								new ArrayList<>();
						
						for (Integer neighbourId : p.getConnectedIntersectionIds()) {
							neighboringIntersections.add(ourMap.getIntersection(neighbourId));
						}
						
						return neighboringIntersections;
					}
			
		};
		
		Cost<SearchContext<StreetMap, Intersection>, Intersection> costFcn = 
				new Cost<SearchContext<StreetMap, Intersection>, Intersection>() {
			
					@Override
					public float compute(SearchContext<StreetMap, Intersection> c, Intersection from, Intersection to) {
						// TODO Auto-generated method stub
						return 0;
					}
					
		};
		
		Estimation<SearchContext<StreetMap, Intersection>, Intersection> estimationFcn =
				new Estimation<SearchContext<StreetMap, Intersection>, Intersection>() {

					@Override
					public float estimate(SearchContext<StreetMap, Intersection> c, Intersection from, Intersection to) {
						// TODO Auto-generated method stub
						return 0;
					}
			
		};
		/////////////////
		// Search for first car
		
		Car bestCar = getCars().get(0);
		
		SearchContext<StreetMap, Intersection> searchAlgo = new AStarSearchContext<StreetMap, Intersection>();
		
		searchAlgo.expansion(expansionFcn);
		searchAlgo.cost(costFcn);
		searchAlgo.estimation(estimationFcn);
		
		searchAlgo.setDomain(street_map);
		searchAlgo.endpoints(bestCar.getStartPoint(), bestCar.getEndPoint());
		
		searchAlgo.execute();
		
		List<Intersection> pathToVICTORY = searchAlgo.solution();
		
		searchAlgo.clear();
		
		// Search for second car
		
		bestCar = getCars().get(1);
		
		searchAlgo.expansion(expansionFcn);
		searchAlgo.cost(costFcn);
		searchAlgo.estimation(estimationFcn);
		
		searchAlgo.setDomain(street_map);
		searchAlgo.endpoints(bestCar.getStartPoint(), bestCar.getEndPoint());
		
		searchAlgo.execute();
		
		pathToVICTORY = searchAlgo.solution();
		
		// TO INFINITY, AND BEYOND!
		
		//	.
		//	.
		//	.
	}

	public void stop() {
		run = false;
		System.out.println("stop");
	}
	
	public void reset() {
		this.cars.clear();
	}
}