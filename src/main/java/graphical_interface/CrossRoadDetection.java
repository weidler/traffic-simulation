package graphical_interface;

import datastructures.Intersection;
import datastructures.Road;
import datastructures.StreetMap;

public class CrossRoadDetection {

	private StreetMap streetMap;
	
	public CrossRoadDetection(StreetMap streetMap)
	{
		boolean recheck = false;		
		this.streetMap = streetMap;		
		
			for(int i = 0 ; i < streetMap.getRoads().size()-1 ; i++)
			{
				if(!recheck) 
				{
					int maxLanes = 0;
					if(streetMap.getRoads().get(streetMap.getRoads().size()-1).getLanes()<streetMap.getRoads().get(i).getLanes())
					{
						maxLanes = streetMap.getRoads().get(i).getLanes();
					}
					recheck = lineIntersect(streetMap.getRoads().get(streetMap.getRoads().size()-1).getX1(), streetMap.getRoads().get(streetMap.getRoads().size()-1).getY1(), streetMap.getRoads().get(streetMap.getRoads().size()-1).getX2(), streetMap.getRoads().get(streetMap.getRoads().size()-1).getY2(), streetMap.getRoads().get(i).getX1(), streetMap.getRoads().get(i).getY1(), streetMap.getRoads().get(i).getX2(), streetMap.getRoads().get(i).getY2(),i,streetMap.getRoads().size()-1, maxLanes);
				}
			}
			
			while(recheck) 
			{
				recheck = false;
				
					for(int i = 0 ; i < streetMap.getRoads().size()-1 ; i++)
					{
						int maxLanes = 0;
						if(streetMap.getRoads().get(streetMap.getRoads().size()-1).getLanes()<streetMap.getRoads().get(i).getLanes())
						{
							maxLanes = streetMap.getRoads().get(i).getLanes();
						}
						recheck = lineIntersect(streetMap.getRoads().get(streetMap.getRoads().size()-1).getX1(), streetMap.getRoads().get(streetMap.getRoads().size()-1).getY1(), streetMap.getRoads().get(streetMap.getRoads().size()-1).getX2(), streetMap.getRoads().get(streetMap.getRoads().size()-1).getY2(), streetMap.getRoads().get(i).getX1(), streetMap.getRoads().get(i).getY1(), streetMap.getRoads().get(i).getX2(), streetMap.getRoads().get(i).getY2(),i,streetMap.getRoads().size()-1, maxLanes);
						
					}
				
				
					for(int i = 0 ; i < streetMap.getRoads().size()-1 ; i++)
					{
						int maxLanes = 0;
						if(streetMap.getRoads().get(streetMap.getRoads().size()-2).getLanes()<streetMap.getRoads().get(i).getLanes())
						{
							maxLanes = streetMap.getRoads().get(i).getLanes();
						}
						recheck = lineIntersect(streetMap.getRoads().get(streetMap.getRoads().size()-2).getX1(), streetMap.getRoads().get(streetMap.getRoads().size()-2).getY1(), streetMap.getRoads().get(streetMap.getRoads().size()-2).getX2(), streetMap.getRoads().get(streetMap.getRoads().size()-2).getY2(), streetMap.getRoads().get(i).getX1(), streetMap.getRoads().get(i).getY1(), streetMap.getRoads().get(i).getX2(), streetMap.getRoads().get(i).getY2(),i,streetMap.getRoads().size()-2, maxLanes);
						
					}
				
				
					for(int i = 0 ; i < streetMap.getRoads().size()-1 ; i++)
					{
						int maxLanes = 0;
						if(streetMap.getRoads().get(streetMap.getRoads().size()-3).getLanes()<streetMap.getRoads().get(i).getLanes())
						{
							maxLanes = streetMap.getRoads().get(i).getLanes();
						}
						recheck = lineIntersect(streetMap.getRoads().get(streetMap.getRoads().size()-3).getX1(), streetMap.getRoads().get(streetMap.getRoads().size()-3).getY1(), streetMap.getRoads().get(streetMap.getRoads().size()-3).getX2(), streetMap.getRoads().get(streetMap.getRoads().size()-3).getY2(), streetMap.getRoads().get(i).getX1(), streetMap.getRoads().get(i).getY1(), streetMap.getRoads().get(i).getX2(), streetMap.getRoads().get(i).getY2(),i,streetMap.getRoads().size()-3, maxLanes);
						
					}
				
			
					for(int i = 0 ; i < streetMap.getRoads().size()-1 ; i++)
					{
						int maxLanes = 0;
						if(streetMap.getRoads().get(streetMap.getRoads().size()-4).getLanes()<streetMap.getRoads().get(i).getLanes())
						{
							maxLanes = streetMap.getRoads().get(i).getLanes();
						}
						recheck = lineIntersect(streetMap.getRoads().get(streetMap.getRoads().size()-4).getX1(), streetMap.getRoads().get(streetMap.getRoads().size()-4).getY1(), streetMap.getRoads().get(streetMap.getRoads().size()-4).getX2(), streetMap.getRoads().get(streetMap.getRoads().size()-4).getY2(), streetMap.getRoads().get(i).getX1(), streetMap.getRoads().get(i).getY1(), streetMap.getRoads().get(i).getX2(), streetMap.getRoads().get(i).getY2(),i,streetMap.getRoads().size()-4, maxLanes);
						
					}
				
			}	
		
	}
	
	public boolean lineIntersect(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int index, int index2, int lanes) {
		
	
		  double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
		  
		  if (denom == 0.0)
		  { // Lines are parallel.
		     return false;
		  }
		  
		  double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))/denom;
		  double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))/denom;
		  
		  if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) 
		  {
		      if(y1 != y2 && y1 != y3 && y1 != y4 && y2 != y3 && y2 != y4 && y3 != y4 ) 
		      {  
				  Intersection newIntersection = new Intersection((int) (x1 + ua*(x2 - x1)), (int) (y1 + ua*(y2 - y1)));
				  streetMap.addIntersection(newIntersection);
				  streetMap.removeRoadBetweenCoordinates(x1, y1, x2, y2);
				  streetMap.removeRoadBetweenCoordinates(x3, y3, x4, y4);
				  Road r1 = new Road(x3, y3, newIntersection.getXCoord() , newIntersection.getYCoord());
				  r1.setLanes(lanes);
				  Road r2 = new Road(x4, y4, newIntersection.getXCoord() , newIntersection.getYCoord());
				  r2.setLanes(lanes);
				  Road r3 = new Road(x1, y1, newIntersection.getXCoord() , newIntersection.getYCoord());
				  r3.setLanes(lanes);
				  Road r4 = new Road(x2, y2, newIntersection.getXCoord() , newIntersection.getYCoord());
				  r4.setLanes(lanes);
				  streetMap.addRoad(r1);
				  streetMap.addRoad(r2);
				  streetMap.addRoad(r3);
				  streetMap.addRoad(r4);					  
				  
				  return true;
		      }
		  }

		  return false;
	}
}
