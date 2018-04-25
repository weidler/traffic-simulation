package road;

import java.util.ArrayList;

import datastructures.Intersection;
import datastructures.RoadType;
import datastructures.StreetMap;
import datastructures.TrafficLight;

public class Road {

	protected int x1;
	protected int y1;
	protected int x2;
	protected int y2;
	protected int length;
	protected int lanes = 1;

	protected RoadType type = RoadType.ROAD;
	protected int allowed_max_speed = 50;
	//first one in the list is the trafficlight closest to the middle.
	protected ArrayList<TrafficLight> trafficlightsRight = new ArrayList();
	protected ArrayList<TrafficLight> trafficlightsLeft = new ArrayList();
	protected StreetMap streetmap ;
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
	}

	public void setStreetMap(StreetMap map)
	{
		streetmap = map;
	}
	public void setLanes(int l) {
		if(l >= 1 && l < 4) {
			lanes = l;
			for(int i = 0; i < l; i++)
			{
				trafficlightsRight.add(new TrafficLight(this, streetmap.getIntersectionByCoordinates(x1, y1), 0));
				trafficlightsLeft.add(new TrafficLight(this, streetmap.getIntersectionByCoordinates(x2, y2), 0));
				
			}
		} else {
			System.out.println("number of lanes is not allowed");
		}		
	}
	
	public RoadType getType() {
		return type;
	}
	
	public int getLanes() {
		return lanes;
	}
	
	public int getLength() {
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
	public ArrayList<TrafficLight> getTrafficLightsRight()
	{
		return trafficlightsRight;
	}
	public ArrayList<TrafficLight> getTrafficLightsLeft()
	{
		return trafficlightsLeft;
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
	
	private int calcLength(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow(y2 - y1, 2));
	}

}
