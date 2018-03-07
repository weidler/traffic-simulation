package graphical_interface;


import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import datastructures.Car;
import datastructures.Intersection;
import datastructures.Road;
import datastructures.StreetMap;
import javax.swing.JButton;
import javax.swing.BorderFactory;

/**
 * 
 * @author thomas
 * this class is the interface
 */

public class GraphicalInterface extends JFrame {

	private Simulation simulation = new Simulation();
	
	/**
	 * represent to position of the mouse at all times.
	 */
	
	private int mouseX;
	private int mouseY;

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
	
	/**
	 * JPanel that shows the roads etc.
	 */
	Visuals visuals = new Visuals(streetMap);
	
	/**
	 * create interface. including buttons and listeners
	 */
	public GraphicalInterface() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 848, 534);
		
		contentPane.setBorder(BorderFactory.createRaisedBevelBorder());
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setResizable(false);
		
		JPanel drawPanel = visuals;
		drawPanel.setBounds(10, 11, 639, 474);
		drawPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		contentPane.add(drawPanel);
		
		JPanel menuPanel = new JPanel();
		menuPanel.setBounds(659, 11, 167, 474);
		menuPanel.setBackground(Color.LIGHT_GRAY);
		menuPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		
		contentPane.add(menuPanel);
		menuPanel.setLayout(null);
		
		JButton undoButton = new JButton("undo");
		undoButton.setBounds(10, 59, 147, 37);
		undoButton.setBorder(BorderFactory.createRaisedBevelBorder());
		menuPanel.add(undoButton);
		undoButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(streetMap.getRoads().size()>0)
				{
					streetMap.getIntersections().remove(streetMap.getIntersections().size()-1);
					streetMap.getRoads().remove(streetMap.getRoads().size()-1);
					repaint();
				}
				else
				{
					streetMap.clearMap();
				}
				repaint();
				
			}
		});
		
		JButton clearButton = new JButton("clear");
		clearButton.setLocation(10, 11);
		clearButton.setBorder(BorderFactory.createRaisedBevelBorder());
		clearButton.setSize(147, 37);
		menuPanel.add(clearButton);
		clearButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				streetMap.getIntersections().clear();
				streetMap.getRoads().clear();
				streetMap.getCarsList().clear();
				repaint();
				
			}
		});
		
		JButton startButton = new JButton("start");
		startButton.setBounds(10, 426, 60, 37);
		startButton.setBorder(BorderFactory.createRaisedBevelBorder());
		menuPanel.add(startButton);
		startButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				simulation.start();

				
			}
		});
		
		JButton helpButton = new JButton("help");
		helpButton.setBorder(BorderFactory.createRaisedBevelBorder());
		helpButton.setBounds(10, 378, 147, 37);
		menuPanel.add(helpButton);
		helpButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(drawPanel,"left click to create road, right click to cancel creating road");
			}
			
		});
		
		JButton addCar = new JButton("add car");
		addCar.setBorder(BorderFactory.createRaisedBevelBorder());
		addCar.setBounds(10, 330, 147, 37);
		menuPanel.add(addCar);
		addCar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(streetMap.getRoads().size()>0) {
					Random r = new Random();
					int s = r.nextInt(streetMap.getIntersections().size());
					int e = r.nextInt(streetMap.getIntersections().size());
					while(s == e)
					{
						e = r.nextInt(streetMap.getIntersections().size());
					}
					streetMap.addCar(new Car(streetMap.getIntersection(s), streetMap.getIntersection(e),streetMap.getCarsList()));
					System.out.println("created new car, x: " + streetMap.getIntersection(s).getXCoord()+", y: "+streetMap.getIntersection(s).getYCoord()+", total: "+streetMap.getCarsList().size());
					repaint();
				}
				
			}
		});
		
		JButton stopButton = new JButton("stop");
		stopButton.setBounds(97, 426, 60, 37);
		menuPanel.add(stopButton);
		stopButton.setBorder(BorderFactory.createRaisedBevelBorder());
		stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				simulation.stop();
				
			}
		});
		
		JButton zoomInButton = new JButton("zoom in");
		zoomInButton.setBounds(10, 150, 147, 37);
		menuPanel.add(zoomInButton);
		zoomInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO
			}
		});
		
		JButton zoomOutButton = new JButton("zoom out");
		zoomOutButton.setBounds(10, 198, 147, 37);
		menuPanel.add(zoomOutButton);
		zoomOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO
			}
		});
		
		
		
		Handlerclass handler = new Handlerclass();
		drawPanel.addMouseListener(handler);
		drawPanel.addMouseMotionListener(handler);
			
	
	}
	
	
	
	/**
	 * 
	 * @author thomas
	 * this is the mouse listener. in here all the clickes and movement of the mouse are registered and roads and intersection are created.
	 */
	private class Handlerclass implements MouseListener,MouseMotionListener {

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
			
			visuals.setDrawLine(true);
			
			clickCounter++;
			
			int x = e.getX();
			int y = e.getY();
						
			
			if(clickCounter == 1) {
				
				if (e.getButton() == MouseEvent.BUTTON3)
			    {
					if(streetMap.getRoads().size() > 0) 
					{
						int nearestX = -1;
						int nearestY = -1;
						double distance = -1;
						for(Intersection sec : streetMap.getIntersections())
						{
							
							double distance2 = (double)(Math.sqrt(Math.pow(x - sec.getXCoord(), 2) + (Math.pow(y - sec.getYCoord(), 2))));
							System.out.println("1 distance "+ distance+" distance 2 "+distance2);
							if (distance == -1) {
								distance = distance2;							
								nearestX = sec.getXCoord();						
								nearestY = sec.getYCoord();							
							}
							else if(distance2 < distance)
							{
								System.out.println("2 distance "+ distance+" distance 2 "+distance2);
								distance = distance2;
								nearestX = sec.getXCoord();
								nearestY = sec.getYCoord();							
							}	
							
						}
						
					}
			    }
				
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
					for(Intersection sec : streetMap.getIntersections())
					{
						
						double distance2 = (double)(Math.sqrt(Math.pow(x - sec.getXCoord(), 2) + (Math.pow(y - sec.getYCoord(), 2))));
						System.out.println("1 distance "+ distance+" distance 2 "+distance2);
						if (distance == -1) {
							distance = distance2;							
							nearestX = sec.getXCoord();						
							nearestY = sec.getYCoord();							
						}
						else if(distance2 < distance)
						{
							System.out.println("2 distance "+ distance+" distance 2 "+distance2);
							distance = distance2;
							nearestX = sec.getXCoord();
							nearestY = sec.getYCoord();							
						}	
						
					}
					startX = nearestX;
					startY = nearestY;
					Intersection in = new Intersection(startX, startY);
					streetMap.addIntersection(in);
					
				}
				visuals.setStartPosX(startX);
				visuals.setStartPosY(startY);
				visuals.setDrawLine(true);
			}
			else {
				
				if (e.getButton() == MouseEvent.BUTTON3)
			    {
					visuals.setDrawLine(false);
					clickCounter = 0;
			    }
				else
				{
				
				int nearestX = -1;
				int nearestY = -1;
				double distance = -1;
				for(Intersection sec : streetMap.getIntersections())
				{
					
					double distance2 = (double)(Math.sqrt(Math.pow(x - sec.getXCoord(), 2) + (Math.pow(y - sec.getYCoord(), 2))));
					System.out.println("1 distance "+ distance+" distance 2 "+distance2);
					if (distance == -1) {
						distance = distance2;							
						nearestX = sec.getXCoord();						
						nearestY = sec.getYCoord();							
					}
					else if(distance2 < distance)
					{
						System.out.println("2 distance "+ distance+" distance 2 "+distance2);
						distance = distance2;
						nearestX = sec.getXCoord();
						nearestY = sec.getYCoord();							
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
				
				Intersection in = new Intersection(endX, endY);
				streetMap.addIntersection(in);
				streetMap.addRoad(new Road(startX,startY,endX,endY));
				new CrossRoadDetection(streetMap);
				clickCounter = 0;
				
				visuals.setDrawLine(false);
			}
			System.out.println(streetMap.getIntersections());
			
			
			System.out.println("x coordinate: "+x);
			System.out.println("y coordinate: "+y);
			System.out.println("");
			System.out.println("changed");
			repaint();
			
			}
			
		}

		@Override
		public void mouseReleased(java.awt.event.MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseDragged(java.awt.event.MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			visuals.setMousePosX(mouseX);
			visuals.setMousePosY(mouseY);
			repaint();
			
		}

		@Override
		public void mouseMoved(java.awt.event.MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			visuals.setMousePosX(mouseX);
			visuals.setMousePosY(mouseY);
			repaint();
			
		}	
		
	}
}
