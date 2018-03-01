package graphical_interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.w3c.dom.events.MouseEvent;

import datastructures.Connection;
import datastructures.Intersection;
import datastructures.Road;
import datastructures.StreetMap;

import java.awt.Graphics2D;

import javax.swing.JButton;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
/**
 * 
 * @author thomas
 * this class is the interface
 */

public class GraphicalInterface extends JFrame {

	/**
	 * checks if click is start or end of road.
	 */
	private int clickCounter = 0;
	
	/**
	 * start and end coordinates of raods.
	 */
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	/**
	 * main panel.
	 */
	private JPanel contentPane = new JPanel();
	/**
	 * streetmap
	 */
	StreetMap streetMap = new StreetMap();
	
	
	private final boolean TWO_WAY = true;
	
	/**
	 * create interface.
	 */
	public GraphicalInterface() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 848, 534);
		
		contentPane.setBorder(BorderFactory.createRaisedBevelBorder());
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setResizable(false);
		
		JPanel drawPanel = new Visuals(streetMap);
		drawPanel.setBounds(10, 11, 639, 474);
		
		drawPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		contentPane.add(drawPanel);
		
		JPanel menuPanel = new JPanel();
		menuPanel.setBounds(659, 11, 167, 474);
		menuPanel.setBackground(Color.LIGHT_GRAY);
		menuPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		contentPane.add(menuPanel);
		
		
		JButton startButton = new JButton("start");
		startButton.setBounds(34, 21, 89, 23);
		menuPanel.add(startButton);
		startButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("start");
			}
			
		});
		
		Handlerclass handler = new Handlerclass();
		drawPanel.addMouseListener(handler);
			
	
	}
	/**
	 * 
	 * @author thomas
	 * this is the mouselistener
	 */
	private class Handlerclass implements MouseListener{

		@Override
		public void mouseClicked(java.awt.event.MouseEvent e) {
			
			
		}

		@Override
		public void mouseEntered(java.awt.event.MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(java.awt.event.MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		/**
		 *  this is the only used method and registers the clicks.
		 */
		@Override
		public void mousePressed(java.awt.event.MouseEvent e) {
			
			clickCounter++;
			
			int x = e.getX();
			int y = e.getY();
						
			
			if(clickCounter == 1) {
				
				if(streetMap.getRoads().isEmpty()) {
					startX = x;
					startY = y;
					Intersection in = new Intersection(startX, startY);
					streetMap.addIntersection(in);
				}
				else
				{
					int nearestX = -1;
					int nearestY = -1;
					double distance = -1;
					for(Intersection sec : streetMap.getAllIntersections())
					{
						
						double distance2 = (double)(Math.sqrt(Math.pow(x - sec.getX_coord(), 2) + (Math.pow(y - sec.getY_coord(), 2))));
						System.out.println("1 distance "+ distance+" distance 2 "+distance2);
						if (distance == -1) {
							distance = distance2;							
							nearestX = sec.getX_coord();						
							nearestY = sec.getY_coord();							
						}
						else if(distance2 < distance)
						{
							System.out.println("2 distance "+ distance+" distance 2 "+distance2);
							distance = distance2;
							nearestX = sec.getX_coord();
							nearestY = sec.getY_coord();							
						}	
						
					}
					startX = nearestX;
					startY = nearestY;
					Intersection in = new Intersection(startX, startY);
					streetMap.addIntersection(in);
				}
				
				
			}
			else {
				
				int nearestX = -1;
				int nearestY = -1;
				double distance = -1;
				for(Intersection sec : streetMap.getAllIntersections())
				{
					
					double distance2 = (double)(Math.sqrt(Math.pow(x - sec.getX_coord(), 2) + (Math.pow(y - sec.getY_coord(), 2))));
					System.out.println("1 distance "+ distance+" distance 2 "+distance2);
					if (distance == -1) {
						distance = distance2;							
						nearestX = sec.getX_coord();						
						nearestY = sec.getY_coord();							
					}
					else if(distance2 < distance)
					{
						System.out.println("2 distance "+ distance+" distance 2 "+distance2);
						distance = distance2;
						nearestX = sec.getX_coord();
						nearestY = sec.getY_coord();							
					}	
					
				}
				if(distance < 20) {
					
					endX = nearestX;
					endY = nearestY;
				}
				else {
					endX = x;
					endY = y;
				}
				
				
				streetMap.addRoad(new Road(startX,startY,endX,endY));
				Intersection in = new Intersection(endX, endY);
				streetMap.addIntersection(in);
				new CrossRoadDetection(streetMap.getAllIntersections(), streetMap.getRoads(),streetMap);
				clickCounter = 0;
				repaint();
			}
			System.out.println(streetMap.getAllIntersections());
			
			
			System.out.println("x coordinate: "+x);
			System.out.println("y coordinate: "+y);
			System.out.println("");
			System.out.println("changed");
			
			
		}

		@Override
		public void mouseReleased(java.awt.event.MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
		
	}
	
	
	
}
