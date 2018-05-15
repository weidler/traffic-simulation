package road;

import java.util.ArrayList;

import datastructures.Intersection;
import datastructures.RoadType;
import datastructures.StreetMap;
import datastructures.TrafficLight;
import util.Geometry;

public class Road {

	protected int x1;
	protected int y1;
	protected int x2;
	protected int y2;
	protected double length;
	protected int lanes = 1;


	protected RoadType type = RoadType.ROAD;
	protected int allowed_max_speed = 50;
	protected ArrayList<Integer> offsetX = new ArrayList();
	protected ArrayList<Integer> offsetY = new ArrayList();
	
	//first one in the list is the trafficlight closest to the middle.
	protected ArrayList<TrafficLight> trafficlightsRight = new ArrayList();
	protected ArrayList<TrafficLight> trafficlightsLeft = new ArrayList();
	protected StreetMap streetmap;

	public Road(Intersection intersection_from, Intersection intersection_to) {
		this.x1 = intersection_from.getXCoord();
		this.y1 = intersection_from.getYCoord();

		this.x2 = intersection_to.getXCoord();
		this.y2 = intersection_to.getYCoord();
		
		this.length = this.calcLength(x1, y1, x2, y2);		
	}
	
	public Road(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		
		this.x2 = x2;
		this.y2 = y2;

		this.length = this.calcLength(x1, y1, x2, y2);	
		
		this.setTypeParameters();
	}

	public ArrayList<Integer> getOffsetX() {
		return offsetX;
	}
	public ArrayList<Integer> getOffsetY() {
		return offsetY;
	}
	
	public void calculateOffset(Intersection start, Intersection end) {
		double angle = Math.atan2(end.getYCoord()-start.getYCoord(), end.getXCoord()-start.getXCoord());
		if (angle<0){
			angle+=Math.PI*2;
		}
		double offsetAngle = angle+Math.PI/2;
		if (offsetAngle > Math.PI*2)
		{
			offsetAngle-= Math.PI*2;
		}	
		for(int i = 0; i < lanes;i++)
		{
			System.out.println("lane: " + i);
			offsetX.add((int) (Math.round(Math.cos(offsetAngle)*4*i)-4));
			offsetY.add((int) (Math.round(Math.sin(offsetAngle)*4*i)-4));
		}
	}
	
	protected void setTypeParameters() {
		this.allowed_max_speed = 50;
		this.type = RoadType.ROAD;
	}

	public void setStreetMap(StreetMap map)
	{
		streetmap = map;
	}

	public void setLanes(int l) {
		if(l >= 1 && l < 4) {
			lanes = l;
		} else {
			System.out.println("number of lanes is not allowed");
		}
		
		calculateOffset(streetmap.getIntersectionByCoordinates(x1, y1), streetmap.getIntersectionByCoordinates(x2, y2));
	}
	
	public RoadType getType() {
		return type;
	}
	
	public double getClockwiseAngleTo(Road that, Intersection at_int) {
		return Geometry.clockwiseAngle(this.getX1(), this.getY1(), this.getX2(), this.getY2(), that.getX1(), that.getY1(), that.getX2(), that.getY2(), at_int.getXCoord(), at_int.getYCoord());
	}
	
	public Road[] getNeighbouringRoadsAt(Intersection at_int) {
		if (at_int.numbConnections() == 0) return null;
		
		Road closest_road = null;
		Road farest_road = null;
		double closest_angle = 360;
		double farest_angle = 0;
		
		double current_angle;
		for (Road other_road : at_int.getOutgoingRoads()) {
			if (!this.equals(other_road)) {
				current_angle = this.getClockwiseAngleTo(other_road, at_int);
				
				if (current_angle < closest_angle) {
					closest_angle = current_angle;
					closest_road = other_road;
				}
				
				if (current_angle > farest_angle) {
					farest_angle = current_angle;
					farest_road = other_road;
				}
			}
		}
		
		Road[] neighbouring_roads = new Road[2];
		neighbouring_roads[0] = closest_road;
		neighbouring_roads[1] = farest_road;
		
		return neighbouring_roads;
	}
		
	public int getLanes() {
		return lanes;
	}
	
	public double getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}
	
	public ArrayList<TrafficLight> getTrafficLightsRight() {
		return trafficlightsRight;
	}
	
	public ArrayList<TrafficLight> getTrafficLightsLeft() {
		return trafficlightsLeft;
	}

	public int getAllowedMaxSpeed() {
		return allowed_max_speed;
	}

	public void setAllowedMaxSpeed(int allowed_max_speed) {
		this.allowed_max_speed = allowed_max_speed;
	}
	
	public Intersection[] getIntersections(StreetMap streetmap) {
		Intersection [] intersections = new Intersection[2];
		intersections[0] = streetmap.getIntersectionByCoordinates(this.x1, this.y1);
		intersections[1] = streetmap.getIntersectionByCoordinates(this.x2, this.y2);
		
		return intersections;
	}

	public String toString() {
		return this.getClass().getSimpleName() + "[" +this.x1 + "," + this.y1 +" -> "+ this.x2 + "," + this.y2 + "]";
	}
	
	public boolean equalCoordinatesWith(Road road) {
		if (this.x1 == road.getX1() && this.y1 == road.getY1() && this.x2 == road.getX2() && this.y2 == road.getY2()) {
			return true;
		}
		
		if (this.x1 == road.getX2() && this.y1 == road.getY2() && this.x2 == road.getX1() && this.y2 == road.getY1()) {
			return true;
		}
		
		return false;
	}
	
	private double calcLength(int x1, int y1, int x2, int y2) {
		return Geometry.distance(x1, y1, x2, y2);
	}

}
