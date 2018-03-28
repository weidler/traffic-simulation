
package graphical_interface;


import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import core.Simulation;
import datastructures.Car;
import datastructures.Intersection;
import datastructures.Road;
import datastructures.StreetMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JSlider;
import javax.swing.KeyStroke;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;

/**
 * 
 * @author thomas
 * this class is the interface
 */

public class GraphicalInterface extends JFrame {

	private final JFileChooser fc = new JFileChooser();
	
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
	 * start and end coordinates of roads.
	 */
	private int startX;
	private int startY;
	private int endX;
	private int endY;
	
	private int counter = 0;
	
	/**
	 * main panel.
	 */
	private JPanel contentPane = new JPanel();
	
	private StreetMap streetMap;
	private Simulation simulation;
	
	/**
	 * JPanel that shows the roads etc.
	 */
	private Visuals visuals;
	
	/**
	 * create interface. including buttons and listeners
	 */
	public GraphicalInterface(Simulation simulation) {
		this.simulation = simulation;
		this.streetMap = simulation.getStreetMap();
		this.visuals = new Visuals(simulation);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 700);
		
		contentPane.setBorder(BorderFactory.createRaisedBevelBorder());
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setResizable(false);
		
		this.setFocusable(true);
		this.requestFocusInWindow();
		
		JPanel drawPanel = visuals;
		drawPanel.setBounds(10, 11, 991, 640);
		drawPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		contentPane.add(drawPanel);
		
		//ARROW KEY LISTENERS
		InputMap im = drawPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0,false),"up");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0,false),"down");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,0,false),"left");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,0,false),"right");
	
		ActionMap am = drawPanel.getActionMap();
		am.put("up", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				visuals.setChangeY(-1);
				repaint();
				
			}
		});
		am.put("down", new AbstractAction() {
					
			@Override
			public void actionPerformed(ActionEvent e) {
				visuals.setChangeY(1);
				repaint();
						
			}
		});
		am.put("left", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				visuals.setChangeX(-1);
				repaint();
				
			}
		});
		am.put("right", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				visuals.setChangeX(1);
				repaint();
				
			}
		});
		
		
		JPanel menuPanel = new JPanel();
		menuPanel.setBounds(1011, 11, 167, 640);
		menuPanel.setBackground(Color.LIGHT_GRAY);
		menuPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		
		contentPane.add(menuPanel);
		menuPanel.setLayout(null);
		
		
		JButton clearButton = new JButton("clear");
		clearButton.setLocation(10, 11);
		clearButton.setBorder(BorderFactory.createRaisedBevelBorder());
		clearButton.setSize(147, 37);
		menuPanel.add(clearButton);
		clearButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				simulation.reset();
				streetMap.clearMap();
				visuals.resetZoomMultiplier();
				repaint();
				
			}
		});
		
		JButton startButton = new JButton("start");
		startButton.setBounds(10, 378, 60, 37);
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
		helpButton.setBounds(10, 426, 147, 37);
		menuPanel.add(helpButton);
		helpButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(drawPanel,"left click to create road, right click to cancel creating road."
						+ "\n"
						+ "To move the graph, use the arrow keys. \n"
						+ "When zoomed in or out or when the graph's location has changed you can't change the graph.\n"
						+ "To change the graph again press the reset button.");
			}
			
		});
		
		JButton addCar = new JButton("add car");
		addCar.setBorder(BorderFactory.createRaisedBevelBorder());
		addCar.setBounds(10, 282, 147, 37);
		menuPanel.add(addCar);
		addCar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(streetMap.getRoads().size()>0 && visuals.getZoomMultiplier() == 1.0 && visuals.getChangeX()==0 && visuals.getChangeY() == 0) {
					simulation.addRandomCar(counter);
					counter++;
					repaint();
				}				
			}
		});
		
		JSlider slider = new JSlider();
		slider.setBounds(10, 203, 147, 19);
		slider.setValue(50);
		slider.setEnabled(false);
		menuPanel.add(slider);
		
		
		
		JButton stopButton = new JButton("stop");
		stopButton.setBounds(97, 378, 60, 37);
		menuPanel.add(stopButton);
		stopButton.setBorder(BorderFactory.createRaisedBevelBorder());
		stopButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				simulation.stop();
				
			}
		});
		
		JButton zoomInButton = new JButton("+");
		zoomInButton.setBounds(97, 155, 60, 37);
		zoomInButton.setBorder(BorderFactory.createRaisedBevelBorder());
		menuPanel.add(zoomInButton);
		zoomInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(slider.getValue()<99) {
				visuals.IncreaseZoomMultiplier();
				System.out.println("zoom in");
				slider.setValue((int)(50*visuals.getZoomMultiplier()));
				repaint();
				}
			}
		});
		
		JButton zoomOutButton = new JButton("-");
		zoomOutButton.setBounds(10, 155, 60, 37);
		zoomOutButton.setBorder(BorderFactory.createRaisedBevelBorder());
		menuPanel.add(zoomOutButton);
		zoomOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(slider.getValue()>1) {
				visuals.DecreaseZoomMultiplier();
				System.out.println("zoom out");
				slider.setValue((int)(50*visuals.getZoomMultiplier()));
				repaint();
			}
			}
		});
		
		
		JButton resetPositionButton = new JButton("reset position");
		resetPositionButton.setBounds(10, 107, 147, 37);
		resetPositionButton.setBorder(BorderFactory.createRaisedBevelBorder());
		menuPanel.add(resetPositionButton);
		resetPositionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				visuals.resetPosition();
				visuals.resetZoomMultiplier();
				slider.setValue(50);
				repaint();
				
			}
		});	
		
		
		JButton saveButton = new JButton("save");
		saveButton.setBounds(10, 330, 60, 37);
		saveButton.setBorder(BorderFactory.createRaisedBevelBorder());
		menuPanel.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			int count = 0;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				count++;
		        Gson gson = new Gson();
		        String json = gson.toJson(streetMap);
		        System.out.println(json);
		        File f = new File("src\\save\\java\\save"+count+".json\\");
				try {
					while(f.exists() && !f.isDirectory()) {
						count++;
						f = new File("src\\save\\java\\save"+count+".json\\");
					}
					FileWriter file = new FileWriter("src\\save\\java\\save"+count+".json\\");
					gson.toJson(streetMap, file);
					file.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			} 
		});
		JButton loadButton = new JButton("load");
		loadButton.setBounds(97, 330, 60, 37);
		loadButton.setBorder(BorderFactory.createRaisedBevelBorder());
		menuPanel.add(loadButton);
		loadButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnVal = fc.showOpenDialog(drawPanel);
				File file = fc.getSelectedFile();
				 Gson gson = new Gson();
				if(file!=null) {
					ObjectMapper mapper = new ObjectMapper();

					//JSON from file to Object
					try {
						StreetMap map = mapper.readValue(new File(file.getPath()), StreetMap.class);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
			
		
		//ADDS MOUSE AND KEY LISTENER		
		Handlerclass handler = new Handlerclass();
		drawPanel.addMouseListener(handler);
		drawPanel.addMouseMotionListener(handler);
		drawPanel.setFocusable(true);
		drawPanel.requestFocusInWindow();		
	
	}
	
	
	
	/**
	 * 
	 * @author thomas
	 * this is the mouse/key listener. in here all the clicks and movement of the mouse are registered and roads and intersection are created.
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
			
			if(visuals.getZoomMultiplier() == 1.0 && visuals.getChangeX()==0 && visuals.getChangeY() == 0) 
			{
						
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
							// System.out.println("1 distance "+ distance+" distance 2 "+distance2);
							if (distance == -1) {
								distance = distance2;							
								nearestX = sec.getXCoord();						
								nearestY = sec.getYCoord();							
							}
							else if(distance2 < distance)
							{
								// System.out.println("2 distance "+ distance+" distance 2 "+distance2);
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
						// System.out.println("1 distance "+ distance+" distance 2 "+distance2);
						if (distance == -1) {
							distance = distance2;							
							nearestX = sec.getXCoord();						
							nearestY = sec.getYCoord();							
						}
						else if(distance2 < distance)
						{
							// System.out.println("2 distance "+ distance+" distance 2 "+distance2);
							distance = distance2;
							nearestX = sec.getXCoord();
							nearestY = sec.getYCoord();							
						}	
						
					}
					startX = nearestX;
					startY = nearestY;
					Intersection in = new Intersection(startX, startY);
					streetMap.addIntersection(in);
					
					Intersection section = streetMap.getIntersectionByCoordinates(startX, startY);
					if(!section.connectionCanBeAdded())
					{
						clickCounter=0;
					}
					
				}
				
					visuals.setStartPosX(startX);
					visuals.setStartPosY(startY);
					if(clickCounter!=0) {
						visuals.setDrawLine(true);
					}
				
			}
			else 
			{
				
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
					// System.out.println("1 distance "+ distance+" distance 2 "+distance2);
					if (distance == -1) {
						distance = distance2;							
						nearestX = sec.getXCoord();						
						nearestY = sec.getYCoord();							
					}
					else if(distance2 < distance)
					{
						// System.out.println("2 distance "+ distance+" distance 2 "+distance2);
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
			System.out.println(streetMap.getRoads());
			
			System.out.println("x coordinate: "+x);
			System.out.println("y coordinate: "+y);
			System.out.println("");
			System.out.println("changed");
			repaint();
			
			}
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
			if(streetMap.getIntersections().size()>0)
			{
				int nearestX = -1;
				int nearestY = -1;
				double distance = -1;
				for(Intersection sec : streetMap.getIntersections())
				{
					
					double distance2 = (double)(Math.sqrt(Math.pow(mouseX - sec.getXCoord(), 2) + (Math.pow(mouseY - sec.getYCoord(), 2))));
					// System.out.println("1 distance "+ distance+" distance 2 "+distance2);
					if (distance == -1) {
						distance = distance2;							
						nearestX = sec.getXCoord();						
						nearestY = sec.getYCoord();							
					}
					else if(distance2 < distance)
					{
						// System.out.println("2 distance "+ distance+" distance 2 "+distance2);
						distance = distance2;
						nearestX = sec.getXCoord();
						nearestY = sec.getYCoord();							
					}	
					
				}
				if(distance <= 20) 
				{
					Intersection colorRed = streetMap.getIntersectionByCoordinates(nearestX, nearestY);
					visuals.setDrawRed(colorRed);
				}
				else
				{
					visuals.setDrawRed(null);
					
				}
				
			}
			
			visuals.setMousePosX(mouseX);
			visuals.setMousePosY(mouseY);
			repaint();
			
		}
	
		
		
	}
	
	public void redraw()
	{
		repaint();
	}
	
}
