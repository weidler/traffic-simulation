package car;

import java.awt.Color;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;

import datastructures.Intersection;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import model.IntelligentDriverModel;
import model.MOBIL;
import road.Road;
import type.CarType;

/**
 * 
 * @author weidler
 *
 */
public class Car {

	// LOCALIZATION
	protected ArrayList<Intersection> path_over_intersections;
	protected ArrayList<Road> passed_roads;
	protected ArrayList<Road> next_roads;

	protected Road current_road;
	protected Road roadToMeasure;
	protected Intersection current_origin_intersection;
	protected Intersection current_destination_intersection;
	protected boolean reached_destination;
	private double angle;
	private boolean in_traffic;


	// DYNAMIC VALUES
	protected double current_velocity;
	protected double current_acceleration;
	protected double positionX;
	protected double positionY;
	protected double position_on_road;
	protected int lane;

	// BEHAVIOR
	protected IntelligentDriverModel model;
	protected MOBIL mobil;
	protected double desired_velocity;

	// VISUALISATION
	protected double offsetX;
	protected double offsetY;
	protected Color color;

	// TYPE VARIABLES
	protected double reaction_time;
	protected double max_acceleration;
	protected double decceleration;
	protected double sight_distance;
	protected double tl_braking_distance;
	protected double vehicle_length;
	protected double favored_velocity;
	protected CarType type = CarType.CAR;

	// TIME VARIABLES
	protected int startTime;
	protected int endTime;
	protected double startRoad;
	protected double endRoad;
	protected int roadSwitch = 1;

	protected double totalWait;
	
	protected double departure_time;
	protected double arrival_time;

	public Car(ArrayList<Intersection> path, double departure_time, Properties props) {
		this.path_over_intersections = path;
		this.next_roads = new ArrayList<Road>();
		int next= 1;
		for (Intersection inter : path_over_intersections) {
			if (next >= path_over_intersections.size()) break;
			this.next_roads.add(inter.getRoadTo(path_over_intersections.get(next)));
			next++;
		}
		this.passed_roads = new ArrayList<Road>();

		this.current_origin_intersection = path.get(0);
		this.current_destination_intersection = path.get(1);

		this.positionX = (double) current_origin_intersection.getXCoord();
		this.positionY = (double) current_origin_intersection.getYCoord();

		this.current_road = current_origin_intersection.getRoadTo(current_destination_intersection);
		this.lane = current_road.getLanes();

		this.current_velocity = 0;

		this.setTypeParameters(props);
		this.updateDesiredVelocity();

		this.model = new IntelligentDriverModel(Integer.parseInt(props.getProperty("min_headway")),
				Integer.parseInt(props.getProperty("min_spacing")), Integer.parseInt(props.getProperty("IDM_delta")));

		this.mobil = new MOBIL(this.model, 1);

		this.reached_destination = false;
		
		this.in_traffic = false;
		
		this.departure_time = departure_time;
	}

	private void updateDesiredVelocity() {
		this.desired_velocity = Math.min(this.favored_velocity, this.current_road.getAllowedMaxSpeed())
				+ (Math.random() * 2 - 1) * 0.1
						* Math.min(this.favored_velocity, this.current_road.getAllowedMaxSpeed());
	}

	protected void setTypeParameters(Properties props) {
		// DRIVING
		this.reaction_time = 1;
		this.max_acceleration = Integer.parseInt(props.getProperty("max_acceleration"))
				+ (Math.random() * 2 - 1) * Integer.parseInt(props.getProperty("max_acceleration")) * 0.1;
		this.decceleration = Integer.parseInt(props.getProperty("decceleration"));
		this.sight_distance = Integer.parseInt(props.getProperty("sight_distance"));
		this.tl_braking_distance = Integer.parseInt(props.getProperty("tl_breaking_distance"));
		this.favored_velocity = Integer.parseInt(props.getProperty("favored_velocity"));
		this.type = CarType.CAR;
		
		// PHYSICS / VIZ
		this.vehicle_length = Integer.parseInt(props.getProperty("vehicle_length"));
		this.color = Color.decode("#81bae2");
	}

	public double getPositionOnRoad() {
		return position_on_road;
	}

	public void setPositionOnRoad(double position_on_road) {
		this.position_on_road = position_on_road;
		double[] new_coordinates = this.getCoordinatesFromPosition(position_on_road);
		this.setPositionX(new_coordinates[0]);
		this.setPositionY(new_coordinates[1]);
	}

	public ArrayList<Intersection> getPath_over_intersections() {
		return path_over_intersections;
	}

	public void setPath_over_intersections(ArrayList<Intersection> path_over_intersections) {
		this.path_over_intersections = path_over_intersections;
	}

	public Road getCurrentRoad() {
		return current_road;
	}

	public void setCurrentRoad(Road current_road) {
		this.current_road = current_road;

	}

	public Intersection getCurrentOriginIntersection() {
		return current_origin_intersection;
	}

	public void setCurrentOriginIntersection(Intersection current_origin_intersection) {
		this.current_origin_intersection = current_origin_intersection;
	}

	public Intersection getCurrentDestinationIntersection() {
		return current_destination_intersection;
	}

	public void setCurrentDestinationIntersection(Intersection current_destination_intersection) {
		this.current_destination_intersection = current_destination_intersection;

	}

	public double getCurrentVelocity() {
		return current_velocity;
	}

	public void setCurrentVelocity(double current_velocity) {
		this.current_velocity = current_velocity;
	}

	public double getVehicleLength() {
		return vehicle_length;
	}

	public void setVehicle_length(double vehicle_length) {
		this.vehicle_length = vehicle_length;
	}

	public double getCurrentAcceleration() {
		return current_acceleration;
	}

	public void setCurrent_acceleration(double current_acceleration) {
		this.current_acceleration = current_acceleration;
	}

	public double getPositionX() {
		return positionX;
	}

	public void setPositionX(double positionX) {
		this.positionX = positionX;
	}

	public double getPositionY() {
		return positionY;
	}

	public void setPositionY(double positionY) {
		this.positionY = positionY;
	}

	public double getFavoredVelocity() {
		return favored_velocity;
	}

	public void setFavoredVelocity(double favored_velocity) {
		this.favored_velocity = favored_velocity;
	}

	public double getMaxAcceleration() {
		return max_acceleration;
	}

	public void setMaxAcceleration(double max_acceleration) {
		this.max_acceleration = max_acceleration;
	}

	public double getDecceleration() {
		return decceleration;
	}

	public void setDecceleration(double decceleration) {
		this.decceleration = decceleration;
	}

	public double getDesiredVelocity() {
		return this.desired_velocity;
	}

	public void setOffsetX(int x) {
		offsetX = x;
	}

	public void setOffsetY(int y) {
		offsetX = y;
	}

	public double getOffsetX() {

		return offsetX;
	}

	public double getOffsetY() {

		return offsetY;
	}

	public Color getColor() {
		return color;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public double getTotalWaitingTime(){
		return totalWait;
	}
	
	
	public double getDepartureTime() {
		return departure_time;
	}

	public double getArrivalTime() {
		return arrival_time;
	}

	public void setDeparture_time(double departure_time) {
		this.departure_time = departure_time;
	}

	public void setArrivalTime(double arrival_time) {
		this.arrival_time = arrival_time;
	}

	public void setLane(int l) {
		if (l >= 1 && l < 4) {
			lane = l;
		} else {
			System.out.println("number of lanes is not allowed");
		}
	}

	public int getLane() {
		return lane;
	}

	public double getAngle() {
		return angle;
	}

	public void calculateOffset(Intersection start, Intersection end, int width) {
		angle = Math.atan2(end.getYCoord() - start.getYCoord(), end.getXCoord() - start.getXCoord());
		if (angle < 0) angle += Math.PI * 2;
		double offsetAngle = angle + Math.PI / 2;

		if (offsetAngle > Math.PI * 2) offsetAngle -= Math.PI * 2;
		offsetX = Math.cos(offsetAngle) * (width * lane - width / 2);
		offsetY = Math.sin(offsetAngle) * (width * lane - width / 2);
	}

	public boolean update(HashMap<Road, ArrayList<Car>> list_of_cars, double delta_t, double current_time) {
		if (!this.in_traffic) {
			this.lane = this.current_road.getLanes();
			if(this.mobil.isSafe(this, null, this.getLeadingCar(list_of_cars, lane), this.getFollowingCar(list_of_cars, lane))) {
				this.in_traffic = true;
			}
		} else {
			// Check if leading car, else incorporate leaders speed etc.
			Car leading_car = this.getLeadingCar(list_of_cars, this.lane);
			double acceleration;
			if (leading_car == null) {
				acceleration = model.getAcceleration(this, Double.NaN, Double.NaN);
			} else {
				double dist_leading = this.getDistanceToCar(leading_car);
				double leading_velocity = leading_car.getCurrentVelocity();
				acceleration = model.getAcceleration(this, dist_leading, leading_velocity);
			}

			// React to traffic lights
			if (this.getApproachedTrafficlight().isRed() 
					&& this.getApproachedIntersectionDistance() < this.tl_braking_distance + 5
					&& this.getApproachedIntersectionDistance() > this.tl_braking_distance) { // do not break if already over tl line
				this.current_velocity = 0;
				acceleration = 0;
			}

			// Update speed and position
			this.current_acceleration = acceleration; // this is needed to prevent redundant calcs in MOBIL
			this.position_on_road += Math.max(this.current_velocity * delta_t, 0);
			this.current_velocity = Math.max(this.current_velocity + acceleration * delta_t, 0);
			
			
			// Calculate wait time
			if (this.isWaiting()) totalWait += delta_t;
			
			// Check if lane change is a good idea
			for (int lane = 1; lane <= this.current_road.getLanes(); lane++) {
				if (Math.abs(lane - this.lane) == 1) {
					if (this.mobil.shouldChangeLane(this, leading_car, this.getLeadingCar(list_of_cars, lane),
							this.getFollowingCar(list_of_cars, lane))) {
						this.lane = lane;
						break;
					}
				}
			}

			// Check if at destination
			if (this.position_on_road >= this.current_road.getLength()
					&& this.current_destination_intersection == this.path_over_intersections.get(this.path_over_intersections.size() - 1)) {
				this.reached_destination = true;
				timeMeasure();
			} else {

				// Check if at new road
				if (this.position_on_road >= this.current_road.getLength()) {
					this.current_origin_intersection = this.current_destination_intersection;
					this.current_destination_intersection = this.path_over_intersections
							.get(this.path_over_intersections.indexOf(this.current_origin_intersection) + 1);
					
					timeMeasure();
					// change road
					this.position_on_road = this.position_on_road - this.current_road.getLength();
					this.current_road = next_roads.get(0);
					this.lane = Math.min(this.lane, current_road.getLanes());
					this.updateDesiredVelocity();
					this.passed_roads.add(this.next_roads.remove(0));
				}

				// update x and y based on position on road
				double[] new_coordinates = this.getCoordinatesFromPosition(this.position_on_road);
				this.positionX = new_coordinates[0];
				this.positionY = new_coordinates[1];
			}

			return this.reached_destination;
		}
		
		return false;
	}

	public TrafficLight getApproachedTrafficlight() {
		return this.current_destination_intersection.getTrafficLightsApproachingFrom(this.current_origin_intersection,
				this.lane);
	}

	public double getApproachedIntersectionDistance() {
		return this.current_road.getLength() - this.position_on_road;
	}

	public CarType getType() {
		return this.type;
	}

	/**
	 *
	 * @param list_of_cars
	 * @return true if there is no car on the same road that has bigger x coordinate
	 *         than the car passed to the method
	 */
	public boolean hasLeadingCar(HashMap<Road, ArrayList<Car>> list_of_cars) {
		if (this.getLeadingCar(list_of_cars, this.lane) == null) return false;
		return true;
	}

	public Car getLeadingCar(HashMap<Road, ArrayList<Car>> list_of_cars, int lane) {
		Car current_leading_car = null;

		ArrayList<Road> roads_of_interest = new ArrayList<Road>(this.next_roads.subList(0, Math.min(2, next_roads.size())));

		for (Road r : list_of_cars.keySet()) {
			if (!roads_of_interest.contains(r)) continue;

			// only compare if on required lane
			for (Car c : list_of_cars.get(r)) {
				if (this.equals(c) || !c.in_traffic) continue;

				if (c.lane == lane) {
					// only compare if driving in the same direction...
					if (this.path_over_intersections.indexOf(c.getCurrentOriginIntersection()) < this.path_over_intersections
							.indexOf(c.getCurrentDestinationIntersection())) {
						// only compare to cars in front
						if (this.getPositionOnRoad() <= c.getPositionOnRoad()
								|| c.getCurrentRoad() != this.getCurrentRoad()) {
							double dist_to_c = this.getDistanceToCar(c);
							// only if car is in distance of sight
							if (dist_to_c <= this.sight_distance) {
								// If car is closer than previous then update
								if (current_leading_car == null) {
									current_leading_car = c;
								} else if (dist_to_c < this.getDistanceToCar(current_leading_car)) {
									current_leading_car = c;
								}
							}
						}
					}
				}
			}
		}

		return current_leading_car;
	}

	public void timeMeasure() {

		if (roadSwitch == 1) {
			startRoad = StreetMap.getCurrentTime();
			roadSwitch++;
			roadToMeasure = current_road;
		} else {
			endRoad = StreetMap.getCurrentTime();
			roadSwitch = 1;
			double spent = endRoad - startRoad;
			roadToMeasure.computeAverageSpeed(spent);
			// System.out.println("spent time: " + spent + "  start time: " + startRoad + "  end time: " + endRoad);
		}

	}

	public Car getFollowingCar(HashMap<Road, ArrayList<Car>> list_of_cars, int lane) {
		Car current_following_car = null;

		ArrayList<Road> roads_of_interest = new ArrayList<Road>(this.passed_roads.subList(
				passed_roads.size() - Math.min(passed_roads.size(), 3),
				passed_roads.size()
		));

		for (Road r : list_of_cars.keySet()) {
			if (!roads_of_interest.contains(r)) continue;

			for (Car c : list_of_cars.get(r)) {
				if (this.equals(c) || !c.in_traffic) continue;

				// only compare if on required lane
				if (c.lane == lane) {
					// only compare if driving in the same direction...
					if (this.path_over_intersections.indexOf(c.getCurrentOriginIntersection()) < this.path_over_intersections
							.indexOf(c.getCurrentDestinationIntersection())) {
						// only compare to cars behind
						if (this.getPositionOnRoad() >= c.getPositionOnRoad()
								|| c.getCurrentRoad() != this.getCurrentRoad()) {
							double dist_to_c = this.getDistanceToCar(c);
							// only if car is in distance of sight
							if (dist_to_c <= this.sight_distance) {
								// If car is closer than previous then update
								if (current_following_car == null) {
									current_following_car = c;
								} else if (dist_to_c < this.getDistanceToCar(current_following_car)) {
									current_following_car = c;
								}
							}
						}
					}
				}
			}
		}

		return current_following_car;
	}

	public double getDistanceToCar(Car other_car) {
		double distance;
		// on same road
		if (other_car == null) return 100000000;
		if (this.current_destination_intersection.equals(other_car.current_destination_intersection)) {
			distance = Math.abs(this.getPositionOnRoad() - other_car.getPositionOnRoad());
		} else {
			// get distance over multiple roads
			distance = 0;
			for (int i = this.path_over_intersections.indexOf(this.current_destination_intersection); i <= this.path_over_intersections
					.indexOf(other_car.current_destination_intersection); i++) {
				if (i == this.path_over_intersections.indexOf(this.current_destination_intersection)) {
					distance += this.current_road.getLength() - this.getPositionOnRoad();
				} else if (i == this.path_over_intersections.indexOf(other_car.current_destination_intersection)) {
					distance += other_car.getPositionOnRoad();
				} else {
					distance += this.path_over_intersections.get(i).getRoadTo(this.path_over_intersections.get(i - 1)).getLength();
				}
			}
		}
		
		return Math.max(0, distance - (this.getVehicleLength()/2) - (other_car.getVehicleLength()/2));
	}

	// CHECKS

	public boolean isOnPath(ArrayList<Intersection> path) {
		Intersection last_intersection = null;
		for (Intersection inter : path) {
			if (last_intersection != null) {
				if (this.current_road.equals(last_intersection.getRoadTo(inter))) {
					return true;
				}
			}
			last_intersection = inter;
		}

		return false;
	}
	
	public Road getNextRoad() {
		int destind = this.path_over_intersections.indexOf(current_destination_intersection);
		if (destind != this.path_over_intersections.size() - 1) {
			return current_destination_intersection.getRoadTo(this.path_over_intersections.get(destind + 1));
		} else return null;
	}

	/**
	 * https://math.stackexchange.com/questions/2045174/how-to-find-a-point-between-two-points-with-given-distance
	 */
	private double[] getCoordinatesFromPosition(double position) {
		double x_delta = this.current_destination_intersection.getXCoord()
				- this.current_origin_intersection.getXCoord();
		double y_delta = this.current_destination_intersection.getYCoord()
				- this.current_origin_intersection.getYCoord();

		double[] coordinates = new double[2];
		coordinates[0] = this.current_origin_intersection.getXCoord()
				+ (position / this.current_road.getLength()) * x_delta;
		coordinates[1] = this.current_origin_intersection.getYCoord()
				+ (position / this.current_road.getLength()) * y_delta;

		return coordinates;
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat(".##");
		return this.getClass().getSimpleName() + ": (x=" + (int) this.positionX + ", y=" + (int) this.positionY + ", v="
				+ df.format(current_velocity) + " km/h" + ")";
	}

	public boolean inTraffic() {
		return this.in_traffic;
	}

	public boolean isWaiting() {
		return (this.current_velocity < 5 && this.current_acceleration < 0);
	}
	
	public boolean reachedDestination() {
		return reached_destination;
	}
}
