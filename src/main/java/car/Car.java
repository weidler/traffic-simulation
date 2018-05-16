package car;

import java.awt.Color;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import datastructures.CarType;
import datastructures.Intersection;
import datastructures.RoadType;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import model.IntelligentDriverModel;
import model.MOBIL;
import road.Road;

/**
 * 
 * @author weidler
 *
 */
public class Car {

	// LOCALIZATION
	protected ArrayList<Intersection> path;
	protected Road current_road;
	protected Intersection current_origin_intersection;
	protected Intersection current_destination_intersection;
	protected boolean reached_destination;
	private double angle;
	
	// DYNAMIC VALUES
	protected double current_velocity;
	protected double current_acceleration;
	protected double positionX;
	protected double positionY;
	protected double position_on_road;
	protected int lane = 1;

	// BEHAVIOR
	protected IntelligentDriverModel model;
	protected MOBIL mobil;
	protected double desired_velocity;	
	
	// VISUALISATION
	protected int offsetX;
	protected int offsetY;
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
	protected double carDistribution0_2 = 0.07;
	protected double carDistribution2_4 = 0.15;
	protected double carDistribution4_6 = 0.25;
	protected double carDistribution6_8 = 0.40;
	protected double carDistribution8_10 = 0.60;
	protected double carDistribution10_12 = 0.65;
	protected double carDistribution12_14 = 0.70;
	protected double carDistribution14_16 = 0.77;
	protected double carDistribution16_18 = 0.90;
	protected double carDistribution18_20 = 0.95;
	protected double carDistribution20_22 = 0.97;
	protected double carDistribution22_24 = 1.00;
	protected int startTime;
	protected int endTime;
	

	/**
	 *
	 * @param startPoint
	 * @param endPoint
	 * @param streetMap this object needs to be passed as parameter to find the road the car is in!
	 */

	public Car(ArrayList<Intersection> path, StreetMap streetMap, Properties props) {
		
		setStartTime();		
		this.path = path;
		
		color = Color.blue;
		
		this.current_origin_intersection = path.get(0);
		this.current_destination_intersection = path.get(1);
		
		this.positionX = (double) current_origin_intersection.getXCoord();
		this.positionY = (double) current_origin_intersection.getYCoord();

		this.current_road = current_origin_intersection.getRoadTo(current_destination_intersection);
		
		this.current_velocity = 0;
		
		this.setTypeParameters(props);
		this.updateDesiredVelocity();
	
		this.model = new IntelligentDriverModel(
				Integer.parseInt(props.getProperty("min_headway")), 
				Integer.parseInt(props.getProperty("min_spacing")), 
				Integer.parseInt(props.getProperty("IDM_delta"))
		);
		
		this.mobil = new MOBIL(this.model, 1);
		
		this.reached_destination = false;
	}
	
	public void setStartTime()
	{
		Random r = new Random();
		double t = 0 + (1 - 0) * r.nextDouble();
		System.out.println("t; "+ t);
		int iteration = 1;
		if(t >= 0.0 && t <= carDistribution0_2)
		{		
			
			System.out.println("check"+2);
			startTime = (int)(r.nextDouble() * 120);	
			startTime  = startTime + iteration * 120;
		}
		else 
		{
			iteration ++;
		}
		
		if(t > carDistribution0_2 && t <= carDistribution2_4)
		{
			iteration ++;
			System.out.println("check"+iteration);
			startTime = (int)(r.nextDouble() * 120);	
			startTime  = startTime + iteration * 120;
		}
		else 
		{
			iteration ++;
		}
		if(t > carDistribution2_4 && t <= carDistribution4_6)
		{
			iteration ++;
			System.out.println("check"+iteration);
			startTime = (int)(r.nextDouble() * 120);	
			startTime  = startTime + iteration * 120;
		}
		else 
		{
			iteration ++;
		}
		if(t > carDistribution4_6 && t <= carDistribution6_8)
		{
			iteration ++;
			System.out.println("check"+iteration);
			startTime = (int)(r.nextDouble() * 120);	
			startTime  = startTime + iteration * 120;
		}
		else 
		{
			iteration ++;
		}
		if(t > carDistribution6_8 && t <= carDistribution8_10)
		{
			iteration ++;
			System.out.println("check"+iteration);
			startTime = (int)(r.nextDouble() * 120);	
			startTime  = startTime + iteration * 120;
		}
		else 
		{
			iteration ++;
		}
		if(t > carDistribution8_10 && t <= carDistribution10_12)
		{
			iteration ++;
			System.out.println("check"+iteration);
			startTime = (int)(r.nextDouble() * 120);	
			startTime  = startTime + iteration * 120;
		}
		else 
		{
			iteration ++;
		}
		if(t > carDistribution10_12 && t <= carDistribution12_14)
		{
			iteration ++;
			System.out.println("check"+iteration);
			startTime = (int)(r.nextDouble() * 120);	
			startTime  = startTime + iteration * 120;
		}
		else 
		{
			iteration ++;
		}
		if(t > carDistribution14_16 && t <= carDistribution16_18)
		{
			iteration ++;
			startTime = (int)(r.nextDouble() * 120);	
			startTime  = startTime + iteration * 120;
		}
		else 
		{
			iteration ++;
		}
		if(t > carDistribution18_20 && t <= carDistribution20_22)
		{
			iteration ++;
			System.out.println("check"+iteration);
			startTime = (int)(r.nextDouble() * 120);	
			startTime  = startTime + iteration * 120;
		}
		else 
		{
			iteration ++;
		}
		if(t > carDistribution20_22 && t <= carDistribution22_24)
		{
			iteration ++;
			System.out.println("check"+iteration);
			startTime = (int)(r.nextDouble() * 120);	
			startTime  = startTime + iteration * 120;
		}
		
	}
	
	private void updateDesiredVelocity() {
		this.desired_velocity = Math.min(this.favored_velocity, this.current_road.getAllowedMaxSpeed())+(Math.random()*2 - 1)*0.1*Math.min(this.favored_velocity, this.current_road.getAllowedMaxSpeed());
	}

	protected void setTypeParameters(Properties props) {
		// DRIVING
		this.reaction_time = 1;
		this.max_acceleration = Integer.parseInt(props.getProperty("max_acceleration")) + (Math.random()*2 - 1)*Integer.parseInt(props.getProperty("max_acceleration"))*0.1 ;
		this.decceleration = Integer.parseInt(props.getProperty("decceleration"));
		this.sight_distance = Integer.parseInt(props.getProperty("sight_distance"));
		this.tl_braking_distance = Integer.parseInt(props.getProperty("tl_breaking_distance"));
		this.favored_velocity = Integer.parseInt(props.getProperty("favored_velocity"));
		this.type = CarType.CAR;
		// PHYSICS / VIZ
		this.vehicle_length = Integer.parseInt(props.getProperty("vehicle_length"));
		this.color = Color.BLUE;
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

	public ArrayList<Intersection> getPath() {
		return path;
	}

	public void setPath(ArrayList<Intersection> path) {
		this.path = path;
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
	public void setOffsetX(int x)
	{
		offsetX = x;
	}
	public void setOffsetY(int y)
	{
		offsetX = y;
	}
	public int getOffsetX(){
		
		return offsetX;
	}
	
	public int getOffsetY(){
		
		return offsetY;
	}
	
	public Color getColor()
	{
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
	
	public void setLane(int l)
	{
		if(l >= 1 && l < 4)
		{
			lane = l;
		}
		else 
		{
			System.out.println("number of lanes is not allowed");
		}		
	}
	public int getLanes()
	{
		return lane;
	}
	public double getAngle() {
		return angle;
	}
	
	public void calculateOffset(Intersection start, Intersection end) {
		angle = Math.atan2(end.getYCoord()-start.getYCoord(), end.getXCoord()-start.getXCoord());
		if (angle<0){
			angle+=Math.PI*2;
		}
		double offsetAngle = angle+Math.PI/2;
		if (offsetAngle > Math.PI*2)
			offsetAngle-= Math.PI*2;
		offsetX = (int) (Math.round(Math.cos(offsetAngle)*4*lane)-4);
		offsetY = (int) (Math.round(Math.sin(offsetAngle)*4*lane)-4);
	}

	public boolean update(ArrayList<Car> list_of_cars, double delta_t){
		double acceleration;
		
		// Check if leading car, else incorporate leaders speed etc.
		Car leading_car = this.getLeadingCar(list_of_cars, this.lane);
		if (leading_car == null) {
			this.color = Color.PINK;
			if(this.type != CarType.CAR)
				this.color = Color.ORANGE;
			acceleration = model.getAcceleration(this, Double.NaN, Double.NaN);
		} else {
			this.color = Color.blue;
			if(this.type != CarType.CAR)
				this.color = Color.YELLOW;
			double dist_leading = this.getDistanceToCar(leading_car);
			double leading_velocity = leading_car.getCurrentVelocity();
			acceleration = model.getAcceleration(this, dist_leading, leading_velocity);
		}
		
		// React to traffic lights
		if (this.getApproachedTrafficlight().isRed() && this.getApproachedIntersectionDistance() < this.tl_braking_distance) {
			this.current_velocity = 0;
			acceleration = 0;
		}
		
		// Update speed and position
		this.current_acceleration = acceleration; // this is needed to prevent redundant calcs in MOBIL
		this.position_on_road += Math.max(this.current_velocity * delta_t, 0);
		this.current_velocity = Math.max(this.current_velocity + acceleration * delta_t, 0);
		
		// Check if lane change is a good idea
		for (int lane = 1; lane <= this.current_road.getLanes(); lane++) {
			if (lane != this.lane) {
				if (this.mobil.shouldChangeLane(this, leading_car, this.getLeadingCar(list_of_cars, lane), this.getFollowingCar(list_of_cars, lane))) {
					this.lane = lane;
					break;
				}
			}
		}
				
		// Check if at destination
		if (this.position_on_road >= this.current_road.getLength() && this.current_destination_intersection == this.path.get(this.path.size()-1)) {
			this.reached_destination = true;
		} else {

			// Check if at new road
			if (this.position_on_road >= this.current_road.getLength()) {
				this.current_origin_intersection = this.current_destination_intersection;
				this.current_destination_intersection = this.path.get(this.path.indexOf(this.current_origin_intersection) + 1);
				
				// change road
				this.position_on_road = this.position_on_road - this.current_road.getLength();
				this.current_road = this.current_origin_intersection.getRoadTo(this.current_destination_intersection);
				this.lane = Math.min(this.lane, current_road.getLanes());
				this.updateDesiredVelocity();
			}

			// update x and y based on position on road
			double[] new_coordinates = this.getCoordinatesFromPosition(this.position_on_road);
			this.positionX = new_coordinates[0];
			this.positionY = new_coordinates[1];
		}
				
		return this.reached_destination;
	}

	public TrafficLight getApproachedTrafficlight() {
		return this.current_destination_intersection.getTrafficLightsApproachingFrom(this.current_origin_intersection, this.lane);
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
	 * @param car
	 * @return true if there is no car on the same road that has bigger x coordinate than the car passed to the method
	 */
	public boolean hasLeadingCar(ArrayList<Car> list_of_cars){
		if (this.getLeadingCar(list_of_cars, this.lane) == null) return false;
		return true;
	}

	public Car getLeadingCar(ArrayList<Car> list_of_cars, int lane) {
		Car current_leading_car = null;

		for(Car c : list_of_cars){
			//for the case it compares with itself skip to next iteration
			if(this.equals(c)) continue;

			// only compare if on required lane
			if (c.lane == lane) {
				// only compare if driving on cars path
				if (c.isOnPath(new ArrayList<Intersection>(this.path.subList(this.path.indexOf(this.getCurrentOriginIntersection()), this.path.size())))) {
					// only compare if driving in the same direction...
					if (this.path.indexOf(c.getCurrentOriginIntersection()) < this.path.indexOf(c.getCurrentDestinationIntersection())) {
						// only compare to cars in front
						if(this.getPositionOnRoad() <= c.getPositionOnRoad() || c.getCurrentRoad() != this.getCurrentRoad()) {
							double dist_to_c = this.getDistanceToCar(c);
							// only if car is in distance of sight
							if (dist_to_c <= this.sight_distance) {
								// If car is closer than previous then update
								if (current_leading_car == null) {
									current_leading_car = c;
								} else if(dist_to_c < this.getDistanceToCar(current_leading_car)){
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

	public Car getFollowingCar(ArrayList<Car> list_of_cars, int lane) {
		Car current_following_car = null;

		for(Car c : list_of_cars){
			//for the case it compares with itself skip to next iteration
			if(this.equals(c)) continue;

			// only compare if on required lane
			if (c.lane == lane) {
				// only compare if driving on cars path
				if (c.isOnPath(new ArrayList<Intersection>(this.path.subList(this.path.indexOf(this.getCurrentOriginIntersection()), this.path.size())))) {
					// only compare if driving in the same direction...
					if (this.path.indexOf(c.getCurrentOriginIntersection()) < this.path.indexOf(c.getCurrentDestinationIntersection())) {
						// only compare to cars behind
						if(this.getPositionOnRoad() >= c.getPositionOnRoad() || c.getCurrentRoad() != this.getCurrentRoad()) {
							double dist_to_c = this.getDistanceToCar(c);
							// only if car is in distance of sight
							if (dist_to_c <= this.sight_distance) {
								// If car is closer than previous then update
								if (current_following_car == null) {
									current_following_car = c;
								} else if(dist_to_c < this.getDistanceToCar(current_following_car)){
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

	
	public double getDistanceToCar(Car other_car){
		// on same road
		if (this.current_destination_intersection.equals(other_car.current_destination_intersection)) {
			return Math.abs(this.getPositionOnRoad() - other_car.getPositionOnRoad());
		} else {
			// get distance over multiple roads
			double distance = 0;
			for (int i = this.path.indexOf(this.current_destination_intersection); i <= this.path.indexOf(other_car.current_destination_intersection); i++) {
				if (i == this.path.indexOf(this.current_destination_intersection)) {
					distance += this.current_road.getLength() - this.getPositionOnRoad();
				} else if (i == this.path.indexOf(other_car.current_destination_intersection)) {
					distance += other_car.getPositionOnRoad();
				} else {
					distance += this.path.get(i).getRoadTo(this.path.get(i-1)).getLength();
				}
			}
			return distance;
		}
	}

//	public double getPositionOnRoad() {
//		return Math.sqrt(Math.pow((this.positionX - this.current_road.getX1()), 2) + Math.pow(this.positionY - this.current_road.getY1(), 2));
//	}
	
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
	
	/**
	 * https://math.stackexchange.com/questions/2045174/how-to-find-a-point-between-two-points-with-given-distance
	 */
	private double[] getCoordinatesFromPosition(double position) {
		double x_delta = this.current_destination_intersection.getXCoord() - this.current_origin_intersection.getXCoord();
		double y_delta = this.current_destination_intersection.getYCoord() - this.current_origin_intersection.getYCoord();
				
		double[] coordinates = new double[2];
		coordinates[0] = this.current_origin_intersection.getXCoord() + (position/this.current_road.getLength()) * x_delta;
		coordinates[1] = this.current_origin_intersection.getYCoord() + (position/this.current_road.getLength()) * y_delta;		
		
		return coordinates;
	}

	public String toString() {
		DecimalFormat df = new DecimalFormat(".##");
		return this.getClass().getSimpleName() + ": (x=" + (int)this.positionX + ", y=" + (int)this.positionY + ", v=" + df.format(current_velocity) + " km/h" +")";
	}
}
