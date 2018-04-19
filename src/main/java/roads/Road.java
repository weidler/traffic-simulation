package roads;

import datastructures.Intersection;
import datastructures.RoadTypes;

public class Road {

	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int length;
	private Intersection intersectionTo;
	private Intersection intersectionFrom;
	private RoadTypes type = RoadTypes.ROAD;
	private int lanes = 1;
	private int allowed_max_speed = 50;
	
	public Road(Intersection intersection_from, Intersection intersection_to) {
		this.x1 = intersection_from.getXCoord();
		this.y1 = intersection_from.getYCoord();
		this.intersectionFrom = intersection_from;
		this.x2 = intersection_to.getXCoord();
		this.y2 = intersection_to.getYCoord();
		this.intersectionTo = intersection_to;
		
		this.length = this.calcLength(x1, y1, x2, y2);
	}
	
	public Road(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		this.length = this.calcLength(x1, y1, x2, y2);	
	}

	public void setLanes(int l) {
		if(l >= 1 && l < 4) {
			lanes = l;
		} else {
			System.out.println("number of lanes is not allowed");
		}		
	}
	
	public int getLanes() {
		return lanes;
	}

	public RoadTypes getType() {
		return type;
	}
	
	public void setType(RoadTypes t) {
		type = t;
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
	
	public String toString() {
		return this.x1 + "," + this.y1 +","+ this.x2 + "," + this.y2 + ",";
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
	
	public Intersection getInterFrom()
	{
		return intersectionFrom;
	}
	public Intersection getInterTo()
	{
		return intersectionTo;
	}

}
