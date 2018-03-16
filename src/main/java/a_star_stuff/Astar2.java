package a_star_stuff;

import java.util.ArrayList;

import datastructures.Intersection;
import datastructures.StreetMap;

public class Astar2 {

	private Intersection start;
	private Intersection end;
	private StreetMap streetMap;

	public Astar2(StreetMap map)
	{		
		streetMap = map;
	}
	
	public Intersection getStart() {
		return start;
	}

	public void setStart(Intersection start) {
		this.start = start;
	}

	public Intersection getEnd() {
		return end;
	}

	public void setEnd(Intersection end) {
		this.end = end;
	}
	
	public ArrayList<Intersection> createPath()
	{
	
		int counter = 1;
		ArrayList<Intersection> openList = new ArrayList<>();
		ArrayList<Intersection> closedList = new ArrayList<>();
		closedList.add(start);
		start.setParent(start);
		start.setG(0);
		start.setH(Math.sqrt(Math.pow(start.getXCoord() - end.getXCoord(), 2) + Math.pow(start.getYCoord() - end.getYCoord(), 2)));
		boolean foundTarget = false;
		
		while(!foundTarget)
		{
			Intersection currentParent = null;
			currentParent = closedList.get(closedList.size()-1);
			for(int i = 0; i < currentParent.getConnections().size(); i++)
			{
				Intersection currentConnected = currentParent.getConnections().get(i).getDestination();
				if (!closedList.contains(currentConnected))
				{
					double g = currentParent.getConnections().get(i).getRoad().getLength() + currentParent.getG();
					double h =  Math.sqrt(Math.pow(currentConnected.getXCoord() - end.getXCoord(), 2) + Math.pow(currentConnected.getYCoord() - end.getYCoord(), 2)) ;
					double distance = h + g;
					
					openList.add(currentConnected);
					
					if(currentConnected.getParent() == null || currentConnected.getG() > g)
					{
						currentConnected.setParent(currentParent);
						currentConnected.setCost(distance);
						currentConnected.setG(g);
						currentConnected.setH(h);
					}
				}
			}
			
			
			for(int i = 0; i < openList.size(); i++)
			{
				System.out.println("Open " + counter +" x: " + openList.get(i).getXCoord() + " y: " + openList.get(i).getYCoord());
			}
			counter++;	
			
			Intersection lowestOpen = null;
			
			for(int i = 0; i < openList.size()-1; i++)
			{
				if(lowestOpen == null || lowestOpen.getCost() > openList.get(i).getCost())
				{
					lowestOpen = openList.get(i);
				}
			}
			if(openList.size()==1)
				lowestOpen = openList.get(0);
			

			if(lowestOpen == null && openList.isEmpty())
			{
				System.out.println("God has failed us");
			}
			
			if(!(lowestOpen == null))
			{
			closedList.add(lowestOpen);
			openList.remove(lowestOpen);
			}
			
			if(!(lowestOpen == null) && lowestOpen.equals(end))
			{
				foundTarget = true;
			}
		}
		ArrayList<Intersection> path = new ArrayList<Intersection>();
		path.add(end);
		while(!(path.get(path.size()-1).getParent() == path.get(path.size()-1)))
		{
			path.add(path.get(path.size()-1).getParent());
		}
			
		for(int i = 0; i< path.size() ; i++)
		{
			System.out.println("path: x: "+path.get(i).getXCoord()+", y: "+ path.get(i).getYCoord());
		}
		return path;
		
	}
	

/*	public ArrayList<Intersection> createPath()
	{
		System.out.println("start search");
		boolean targetFound = false;
		ArrayList<Intersection> openList = new ArrayList<>();
		ArrayList<Intersection> closedList = new ArrayList<>();
		closedList.add(start);
		start.setParent(start);
		
		while(!targetFound || openList.isEmpty())
		{
			System.out.println("entered while "+ targetFound);
			Intersection working = null;
			for(int i = 0; i<closedList.size();i++)
			{
				if(working == null)
				{
					working = closedList.get(i);
				}
				else if(working.getCost() > closedList.get(i).getCost())
				{
					working = closedList.get(i);
				}
			}
			
			for(int i = 0; i < working.getConnections().size(); i++)
			{
				
				System.out.println("checking connections");
				Intersection checking = streetMap.getIntersection(working.getConnections().get(i).getDestination());
				
				boolean isChecked = false;
				
				for (int j = 0; j < closedList.size(); j++)
				{
					if(closedList.get(j).equals(checking))
					{
						isChecked = true;
						 break;
					}
				}				
				if(!isChecked) {
					double h = streetMap.getRoads().get(working.getConnections().get(i).getRoad()).getLength();
					double g =  Math.sqrt(Math.pow(checking.getXCoord() - end.getXCoord(), 2) + Math.pow(checking.getYCoord() - end.getYCoord(), 2)) ;
					double distance = h + g;
					openList.add(checking);
					if(checking.getParent() == null)
					{
						System.out.println("parent was null");
						checking.setParent(working);					
						checking.setCost(distance);
						checking.setG(g);
					}
					else if(checking.getCost() > distance)
					{
						System.out.println("parent was not null");
						checking.setParent(working);					
						checking.setCost(distance);
						checking.setG(g);
					}
				}
				
			}
			System.out.println("checking which intersection should be closedList"+ " start: " +start.getXCoord()+", "+start.getYCoord()+ " end: " +end.getXCoord()+", "+end.getYCoord());
			Intersection lowestCost = null;
			
			
			System.out.println("Size of list: " + openList.size());
			for(int i = 0 ; i < openList.size() ; i++)
			{
				if(lowestCost == null )
				{
					lowestCost = openList.get(i);
					System.out.println(openList.get(i).getCost());
				}
				else if( lowestCost.getCost() > openList.get(i).getCost())
				{
					lowestCost = openList.get(i);
					System.out.println(openList.get(i).getCost());
				}
			}
			System.out.println(lowestCost == null);
			
			closedList.add(lowestCost);
			openList.remove(lowestCost);
			
			for(int i = 0; i < closedList.size(); i++)
			{
				if(closedList.get(i).getG() == 0)
				{
					targetFound = true;
					System.out.println("Found target");
				}
			}
			
			System.out.println("added to closedList");
			System.out.println(lowestCost == null);
			if(lowestCost.equals(end))
			{
				System.out.println("targetFound");
				targetFound = true;
			}
		
		}
		ArrayList<Intersection> path = new ArrayList<Intersection>();
		path.add(end);
		boolean endReached = false;
		while(!endReached)
		{
			Intersection toAdd = path.get(path.size()-1).getParent();
			if(toAdd.equals(start))
			{
				System.out.println("Start is found");
				endReached = true;
				break;
			}
			else
			{
				System.out.println("Added parent to path");
				path.add(toAdd);
			}
		}
		for(int i = 0; i< path.size() ; i++)
		{
			System.out.println("path: x: "+path.get(i).getXCoord()+", y: "+ path.get(i).getYCoord());
		}
		return path;
	}*/
}
