package graphical_interface;

import java.util.ArrayList;

import datastructures.Intersection;
import datastructures.Road;
import datastructures.StreetMap;

public class CrossRoadDetection {

	private ArrayList<Intersection> intersections;
	private ArrayList<Road> roads;
	private StreetMap streetMap;
	public CrossRoadDetection(ArrayList<Intersection> intersections, ArrayList<Road> roads, StreetMap streetMap)
	{
		System.out.println("check for intersection");
		boolean recheck = false;
		int size = roads.size();
		this.intersections = intersections;
		this.roads = roads;
		this.streetMap = streetMap;
		
		
			for(int i = 0 ; i < size-2 ; i++)
			{
				if(!recheck) 
				{
					recheck = lineIntersect(roads.get(size-1).getX1(), roads.get(size-1).getY1(), roads.get(size-1).getX2(), roads.get(size-1).getY2(), roads.get(i).getX1(), roads.get(i).getY1(), roads.get(i).getX2(), roads.get(i).getY2());
					System.out.println("found an intersection "+ intersections.get(intersections.size()-1).getX_coord()+", "+intersections.get(intersections.size()-1).getY_coord()+" "+recheck);
				}
			}
		
		if (recheck) 
		{
			// do the rest
		}
		
	}
	
	public boolean lineIntersect(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
		
		  double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
		  
		  if (denom == 0.0)
		  { // Lines are parallel.
		     return false;
		  }
		  
		  double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))/denom;
		  double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))/denom;
		  
		  if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) 
		  {
		        // Get the intersection point.
			  Intersection intersection = new Intersection((int) (x1 + ua*(x2 - x1)), (int) (y1 + ua*(y2 - y1)));
			  System.out.println(intersection.getX_coord()+" "+intersection.getY_coord());
			  streetMap.addIntersection(intersection);
			  //this visualize found intersection. remove when continue coding!!!!
			  streetMap.addRoad(new Road(intersection.getX_coord(), intersection.getY_coord(), intersection.getX_coord(), intersection.getY_coord()));
			 
			  return true;
		  }

		  return false;
	}
}
