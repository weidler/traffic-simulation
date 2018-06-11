package road;

import java.util.ArrayList;

import datastructures.Intersection;
import datastructures.StreetMap;
import datatype.Point;
import type.RoadType;
import util.Geometry;

public class Road {

	protected int x1;
	protected int y1;
	protected int x2;
	protected int y2;
	protected double length;
	protected int lanes = 1;
	protected double avergeSpeed = 0;
	protected double avg;
	protected ArrayList<Double> averageSpeeds = new ArrayList<Double>();// in kmh

	protected RoadType type = RoadType.ROAD;
	protected int allowed_max_speed = 50;
	protected ArrayList<Integer> offsetX = new ArrayList<Integer>();
	protected ArrayList<Integer> offsetY = new ArrayList<Integer>();
	protected double angle;
	protected boolean directed = false;

	protected StreetMap streetmap;

	public Road(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;

		this.x2 = x2;
		this.y2 = y2;

		this.length = this.calcLength();

		this.setTypeParameters();
	}

	public Road(Intersection intersection_from, Intersection intersection_to) {
		this(intersection_from.getXCoord(), intersection_from.getYCoord(), intersection_to.getXCoord(), intersection_to.getYCoord());
	}

	public ArrayList<Integer> getOffsetX() {
		return this.offsetX;
	}

	public ArrayList<Integer> getOffsetY() {
		return this.offsetY;
	}
	
	public double getAngle() {
		return this.angle;
	}

	public void calculateOffset(Intersection start, Intersection end) {
		double angle = Math.atan2(end.getYCoord() - start.getYCoord(), end.getXCoord() - start.getXCoord());
		if (angle < 0) angle += Math.PI * 2;
		double offsetAngle = angle + Math.PI / 2;
		if (offsetAngle > Math.PI * 2) offsetAngle -= Math.PI * 2;
		for (int i = 0; i < this.lanes; i++) {
			System.out.println("lane: " + i);
			this.offsetX.add((int) (Math.round(Math.cos(offsetAngle) * 4 * i) - 4));
			this.offsetY.add((int) (Math.round(Math.sin(offsetAngle) * 4 * i) - 4));
		}
		
		this.angle = offsetAngle;
	}

	protected void setTypeParameters() {
		this.allowed_max_speed = 50;
		this.type = RoadType.ROAD;
	}
	public void setRoadType(String t)
	{
		if(t.equals("ROAD"))
		{
			//nothing
		}
		else if(t.equals("DIRT_ROAD"))
		{
			this.type = RoadType.DIRT_ROAD;
		}
		else if(t.equals("HIGHWAY"))
		{
			this.type = RoadType.HIGHWAY;
		}
	}

	public void setStreetMap(StreetMap map) {
		this.streetmap = map;
	}

	public void setLanes(int l) {
		if (l >= 1 && l < 4) this.lanes = l;
		else System.out.println("number of lanes is not allowed");

		this.calculateOffset(this.streetmap.getIntersectionByCoordinates(this.x1, this.y1),
				this.streetmap.getIntersectionByCoordinates(this.x2, this.y2));
	}
	
	public void toggleDirected() {
		if(!this.directed)
			this.directed=true;
		else
			this.directed=false;
	}
	
	public Intersection getDirection() {
		if (!this.directed)
			return null;
		else
			return streetmap.getIntersectionByCoordinates(x2, y2);
		
	}

	public RoadType getType() {
		return this.type;
	}

	public void computeAverageSpeed(double timeSpent) {
		this.averageSpeeds.add((double)((this.length / (timeSpent))));
		double totAvg = 0;
		for (int i = 0; i < this.averageSpeeds.size(); i++) {
			//System.out.println("speed of car " + i + " " + this.averageSpeeds.get(i));

			totAvg += this.averageSpeeds.get(i);
		}

		//System.out.println("Average speed for road: "+ totAvg / this.averageSpeeds.size());
		avg = totAvg / this.averageSpeeds.size();
	}
	
	public double getAverageSpeed() {
		return avg;
	}

	public double getClockwiseAngleTo(Road that, Intersection at_int) {
		int this_x = this.getX1();
		int this_y = this.getY1();
		if (this_x == at_int.getXCoord() && this_y == at_int.getYCoord()) {
			this_x = this.getX2();
			this_y = this.getY2();
		}

		int that_x = that.getX1();
		int that_y = that.getY1();
		if (that_x == at_int.getXCoord() && that_y == at_int.getYCoord()) {
			that_x = that.getX2();
			that_y = that.getY2();
		}

		return Geometry.clockwiseAngle(this_x, this_y, that_x, that_y, at_int.getXCoord(), at_int.getYCoord());
	}

	public Road[] getNeighbouringRoadsAt(Intersection at_int) {
		if (at_int.numbConnections() == 0) return null;

		Road closest_road = null;
		Road farest_road = null;
		double closest_angle = 360;
		double farest_angle = 0;

		double current_angle;
		for (Road other_road : at_int.getOutgoingRoads())
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

		Road[] neighbouring_roads = new Road[2];
		neighbouring_roads[0] = closest_road;
		neighbouring_roads[1] = farest_road;

		return neighbouring_roads;
	}
	public RoadType getRoadType()
	{
		return type;
	}
	public int getLanes() {
		return this.lanes;
	}

	public double getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getX1() {
		return this.x1;
	}

	public int getY1() {
		return this.y1;
	}

	public int getX2() {
		return this.x2;
	}

	public int getY2() {
		return this.y2;
	}
	
	public Point getPointA() {
		return new Point(x1, y1);
	}
	
	public Point getPointB() {
		return new Point(x2, y2);
	}

	public int getAllowedMaxSpeed() {
		return this.allowed_max_speed;
	}

	public void setAllowedMaxSpeed(int allowed_max_speed) {
		this.allowed_max_speed = allowed_max_speed;
	}

	public Intersection[] getIntersections(StreetMap streetmap) {
		Intersection[] intersections = new Intersection[2];
		intersections[0] = streetmap.getIntersectionByCoordinates(this.x1, this.y1);
		intersections[1] = streetmap.getIntersectionByCoordinates(this.x2, this.y2);

		return intersections;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[" + this.x1 + "," + this.y1 + " -> " + this.x2 + "," + this.y2 + "]";
	}

	public boolean equalCoordinatesWith(Road road) {
		if (this.x1 == road.getX1() && this.y1 == road.getY1() && this.x2 == road.getX2() && this.y2 == road.getY2())
			return true;

		if (this.x1 == road.getX2() && this.y1 == road.getY2() && this.x2 == road.getX1() && this.y2 == road.getY1())
			return true;

		return false;
	}

	private double calcLength() {
		return Geometry.distance(getPointA(), getPointB());
	}

}
