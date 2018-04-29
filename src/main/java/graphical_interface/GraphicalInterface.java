
package graphical_interface;


import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.XMLEncoder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import core.Simulation;
import datastructures.Intersection;
import datastructures.RoadType;
import datastructures.StreetMap;
import road.DirtRoad;
import road.Highway;
import road.Road;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JSlider;
import javax.swing.KeyStroke;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;

import car.Car;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;

/**
 * 
 * @author thomas
 * this class is the interface
 */

public class GraphicalInterface extends JFrame {

	private final JFileChooser fc = new JFileChooser();
	private final int DISTANCE_BETWEEN_INTERSECTIONS = 30;

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

	/**
	 * main panel.
	 */
	private JPanel contentPane = new JPanel();

	private StreetMap streetMap;
	private Simulation simulation;

	private String roadTypeToAdd = RoadType.ROAD + "";
	/**
	 * JPanel that shows the roads etc.
	 */

	private Visuals visuals;
	private JTextField txtMinNumberOf;
	private JTextField txtMaxNumberOf;
	private Car lastHovered;
	private JTextField lanesTextField;

	private JRadioButton lanes1;
	private JRadioButton lanes2;
	private JRadioButton lanes3;
	private boolean oneLane = true;
	private boolean twoLane = false;
	private boolean threeLane = false;

	/**
	 * create interface. including buttons and listeners
	 */
	public GraphicalInterface(Simulation simulation) {
		this.simulation = simulation;
		this.streetMap = simulation.getStreetMap();
		this.visuals = new Visuals(simulation);

		JTextArea carsTextArea = new JTextArea();
		carsTextArea.setText("");
		carsTextArea.setBounds(782, 11, 219, 640);
		carsTextArea.setBorder(BorderFactory.createRaisedBevelBorder());
		carsTextArea.setEditable(false);
		contentPane.add(carsTextArea);
		simulation.setTextArea(carsTextArea);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 700);

		contentPane.setBorder(BorderFactory.createRaisedBevelBorder());
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setResizable(false);

		this.setFocusable(true);
		this.requestFocusInWindow();

		JPanel drawPanel = visuals;
		drawPanel.setBounds(10, 11, 762, 640);
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




		JLabel roadTypeLabel = new JLabel(RoadType.ROAD + "");
		roadTypeLabel.setBounds(10, 192, 147, 19);
		menuPanel.add(roadTypeLabel);
		roadTypeLabel.setBorder(BorderFactory.createRaisedBevelBorder());

		JButton roadTypeButton = new JButton("Road Type");
		roadTypeButton.setBounds(10, 162, 147, 19);
		menuPanel.add(roadTypeButton);
		roadTypeButton.setBorder(BorderFactory.createRaisedBevelBorder());
		roadTypeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				int typeCounter =0;
				for(int i = 0; i<RoadType.values().length;i++)
				{
					if((RoadType.values()[i]+"").equals(roadTypeLabel.getText()))
					{
						if(i == RoadType.values().length-1)
						{
							typeCounter = 0;
						}
						else 
						{
							typeCounter = i+1;
						}

					}
				}
				roadTypeLabel.setText(RoadType.values()[typeCounter]+"");
				roadTypeToAdd = RoadType.values()[typeCounter]+"";
				if(roadTypeLabel.getText().equals(RoadType.ROAD+""))
				{
					roadTypeLabel.setForeground (Color.black);
				}
				else if(roadTypeLabel.getText().equals(RoadType.DIRT_ROAD+""))
				{
					roadTypeLabel.setForeground (Color.ORANGE);
				}
				else if(roadTypeLabel.getText().equals(RoadType.HIGHWAY+""))
				{
					roadTypeLabel.setForeground (Color.BLUE);
				}

			}
		});

		JButton clearButton = new JButton("clear");
		clearButton.setLocation(10, 11);
		clearButton.setBorder(BorderFactory.createRaisedBevelBorder());
		clearButton.setSize(147, 20);
		menuPanel.add(clearButton);
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearMap();	
				simulation.stop();
			}
		});

		JButton startButton = new JButton("start");
		startButton.setBounds(10, 466, 60, 20);
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
		helpButton.setBounds(10, 497, 147, 20);
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

		JRadioButton disableCarInfoRadio = new JRadioButton("disable car info");
		disableCarInfoRadio.setBorder(BorderFactory.createRaisedBevelBorder());
		disableCarInfoRadio.setBounds(10, 405, 147, 23);
		menuPanel.add(disableCarInfoRadio);
		disableCarInfoRadio.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				boolean selected = disableCarInfoRadio.isSelected();
				simulation.setCarInfo();
				System.out.println("selected "+selected);
				if(selected)
				{
					if(!carsTextArea.getText().equals(""))
					{
						carsTextArea.setText("");
					
					}
				}
			}
		});
		
		JButton addCar = new JButton("add car");
		addCar.setBorder(BorderFactory.createRaisedBevelBorder());
		addCar.setBounds(10, 131, 147, 20);
		menuPanel.add(addCar);
		addCar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean selected = disableCarInfoRadio.isSelected();
				System.out.println("selected "+selected);
				if(selected)
				{
					if(!carsTextArea.getText().equals(""))
					{
						carsTextArea.setText("");
					}
				}
				if(!simulation.isRunning()&&!selected) 
				{
					simulation.addRandomCar();
					carsTextArea.setText("");
					for(Car car : simulation.getCars())
					{
						carsTextArea.append(car.toString()+"\n");
					}
					repaint();
				}							
			}
		});

		JSlider slider = new JSlider();
		slider.setBounds(10, 101, 147, 19);
		slider.setValue(50);
		slider.setEnabled(false);
		menuPanel.add(slider);



		JButton stopButton = new JButton("stop");
		stopButton.setBounds(97, 466, 60, 20);
		menuPanel.add(stopButton);
		stopButton.setBorder(BorderFactory.createRaisedBevelBorder());
		stopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				simulation.stop();

			}
		});

		JButton zoomInButton = new JButton("+");
		zoomInButton.setBounds(97, 70, 60, 20);
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
		zoomOutButton.setBounds(10, 70, 60, 20);
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
		resetPositionButton.setBounds(10, 39, 147, 20);
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
		saveButton.setBounds(10, 435, 60, 20);
		saveButton.setBorder(BorderFactory.createRaisedBevelBorder());
		menuPanel.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			int count = 0;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				count++;
				streetMap.toString();
				/*
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
				 */
				BufferedWriter bw = null;
				FileWriter fw = null;
				File f = new File("./savefiles/streetmap"+count+".txt");
				try 
				{
					while(f.exists() && !f.isDirectory()) 
					{
						count++;
						f = new File("./savefiles/streetmap"+count+".txt");
					}

					fw = new FileWriter(f);
					bw = new BufferedWriter(fw);
					bw.write(streetMap.toString());	
					bw.close();
					fw.close();
					System.out.println("saved: "+"./savefiles/streetmap"+count+".txt");
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
			} 
		});
		JButton loadButton = new JButton("load");
		loadButton.setBounds(97, 435, 60, 20);
		loadButton.setBorder(BorderFactory.createRaisedBevelBorder());
		menuPanel.add(loadButton);
		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				fc.setCurrentDirectory(new File ("./savefiles/"));
				int returnVal = fc.showOpenDialog(drawPanel);
				File file = fc.getSelectedFile();
				if(file!=null) {

					//JSON from file to Object
					try {
						clearMap();
						Scanner sc = new Scanner(file);						 
						sc.useDelimiter(",");
						String next = sc.next();
						Boolean change = false;
						while(next != null)
						{

							if(next.equals("#"))
							{
								change = true;
								next = sc.next();
							}
							else if(!change)
							{
								int x = Integer.parseInt(next);
								next = sc.next();
								int y = Integer.parseInt(next);					    		
								streetMap.addIntersection(new Intersection(x, y));
								next = sc.next();
							}	
							else 
							{
								int x1 = Integer.parseInt(next);
								next = sc.next();
								int y1 = Integer.parseInt(next);	
								next = sc.next();
								int x2 = Integer.parseInt(next);
								next = sc.next();
								int y2 = Integer.parseInt(next);	
								Intersection start = streetMap.getIntersectionByCoordinates(x1, y1);
								Intersection end = streetMap.getIntersectionByCoordinates(x2, y2);
								streetMap.addRoad(start, end);
								next = sc.next();
							}
							System.out.println("loading..."+ next);
						}
						System.out.println("loaded");
						repaint();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		txtMinNumberOf = new JTextField();
		txtMinNumberOf.setText("0");
		txtMinNumberOf.setBounds(61, 559, 96, 20);
		menuPanel.add(txtMinNumberOf);
		txtMinNumberOf.setColumns(10);

		txtMaxNumberOf = new JTextField();
		txtMaxNumberOf.setBounds(61, 590, 96, 20);
		txtMaxNumberOf.setText("0");
		menuPanel.add(txtMaxNumberOf);
		txtMaxNumberOf.setColumns(10);

		JLabel lblMin = new JLabel("Min");
		lblMin.setBounds(10, 562, 28, 14);
		menuPanel.add(lblMin);

		JLabel lblMax = new JLabel("Max");
		lblMax.setBounds(10, 596, 28, 14);
		menuPanel.add(lblMax);

		JButton randomGraphButton = new JButton("random graph");
		Random rnd = new Random();
		int maxX = drawPanel.getX() + drawPanel.getBounds().width-10;
		int minX = drawPanel.getX();
		int maxY = drawPanel.getY() + drawPanel.getBounds().height-10;
		int minY = drawPanel.getY();

		randomGraphButton.setBounds(10, 528, 147, 20);
		menuPanel.add(randomGraphButton);
		randomGraphButton.setBorder(BorderFactory.createRaisedBevelBorder());
		randomGraphButton.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {

				clearMap();
				int maxNumberOfRoads = Integer.parseInt(txtMaxNumberOf.getText());
				int minNumberOfRoads = Integer.parseInt(txtMinNumberOf.getText());

				int numberOfRoads = rnd.nextInt(maxNumberOfRoads - minNumberOfRoads + 1) + minNumberOfRoads;


				for(int i = 0; i < numberOfRoads; i++)
				{
					if(streetMap.getIntersections().size()==0) 
					{
						int coordinateX1 = rnd.nextInt(maxX - minX + 1) + minX;
						int coordinateY1 = rnd.nextInt(maxY - minY + 1) + minY;
						int coordinateX2 = rnd.nextInt(maxX - minX + 1) + minX;
						int coordinateY2 = rnd.nextInt(maxY - minY + 1) + minY;
						boolean oke = false;
						while(!oke)
						{
							double distance2 = (double)(Math.sqrt(Math.pow(coordinateX1 - coordinateX2, 2) + (Math.pow(coordinateY1 - coordinateY2, 2))));
							if(distance2 > DISTANCE_BETWEEN_INTERSECTIONS)
							{
								oke = true;
							}
							else
							{
								coordinateX2 = rnd.nextInt(maxX - minX + 1) + minX;
								coordinateY2 = rnd.nextInt(maxY - minY + 1) + minY;
							}
						}

						streetMap.addIntersection(new Intersection(coordinateX1, coordinateY1));
						streetMap.addIntersection(new Intersection(coordinateX2, coordinateY2));
						Road r = new Road(streetMap.getIntersections().get(streetMap.getIntersections().size()-1), streetMap.getIntersections().get(streetMap.getIntersections().size()-2));
						r.setStreetMap(streetMap);
						streetMap.addRoad(r);
					}
					else 
					{
						Intersection startIntersection = streetMap.getIntersections().get(rnd.nextInt((streetMap.getIntersections().size()-1) - 0 + 1) + 0);
						int coordinateX1 = rnd.nextInt(maxX - minX + 1) + minX;
						int coordinateY1 = rnd.nextInt(maxY - minY + 1) + minY;

						boolean oke = false;
						while(!oke)
						{
							double distance2 = (double)(Math.sqrt(Math.pow(coordinateX1 - startIntersection.getXCoord(), 2) + (Math.pow(coordinateY1 - startIntersection.getYCoord(), 2))));
							if(distance2 > DISTANCE_BETWEEN_INTERSECTIONS)
							{
								oke = true;
							}
							else
							{
								coordinateX1 = rnd.nextInt(maxX - minX + 1) + minX;
								coordinateY1 = rnd.nextInt(maxY - minY + 1) + minY;
							}
						}

						streetMap.addIntersection(new Intersection(coordinateX1, coordinateY1));
						Road r = new Road(streetMap.getIntersections().get(streetMap.getIntersections().size()-1), startIntersection);
						r.setStreetMap(streetMap);
						streetMap.addRoad(r);
					}	
					new CrossRoadDetection(streetMap);
				}

				repaint();
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		buttonPanel.setBounds(10, 222, 125, 100);
		buttonPanel.setBackground(Color.LIGHT_GRAY);

		lanes1 = new JRadioButton("One Lane",true);
		lanes2 = new JRadioButton("Two Lanes",false);
		lanes3 = new JRadioButton("Three Lanes",false);
		lanes1.setBackground(Color.LIGHT_GRAY);
		lanes2.setBackground(Color.LIGHT_GRAY);
		lanes3.setBackground(Color.LIGHT_GRAY);

		ButtonGroup group = new ButtonGroup();

		group.add(lanes1);
		group.add(lanes2);
		group.add(lanes3);

		buttonPanel.add(lanes1);
		buttonPanel.add(lanes2);
		buttonPanel.add(lanes3);

		menuPanel.add(buttonPanel);
		
		JLabel lanesLabel = new JLabel("lanes:");
		lanesLabel.setBounds(10, 225, 49, 14);
		menuPanel.add(lanesLabel);
		
		




		//ADDS MOUSE AND KEY LISTENER		
		Handlerclass handler = new Handlerclass();
		drawPanel.addMouseListener(handler);
		drawPanel.addMouseMotionListener(handler);
		drawPanel.setFocusable(true);


		drawPanel.requestFocusInWindow();		

	}

	public void clearMap()
	{
		simulation.reset();
		streetMap.clearMap();
		visuals.resetZoomMultiplier();
		repaint();
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



			clickCounter++;

			int x = (int) (e.getX()/visuals.getZoomMultiplier()-visuals.getChangeX());
			int y = (int) (e.getY()/visuals.getZoomMultiplier()-visuals.getChangeY());


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
					if(distance < visuals.getMaxIntersectionSize()) {

						endX = nearestX;
						endY = nearestY;
					}
					else {
						endX = x;
						endY = y;
					}

					Intersection in = new Intersection(endX, endY);
					streetMap.addIntersection(in);
					int typeCounter =0;
					for(int i = 0; i<RoadType.values().length;i++) {
						if((RoadType.values()[i]+"").equals(roadTypeToAdd)) {
							typeCounter = i;
						}
					}
					
//					Road r = new Road(startX,startY,endX,endY);		
//					r.setType(RoadType.values()[typeCounter]);
					Road r;
					switch (roadTypeToAdd) {
						case "DIRT_ROAD":
							r = new DirtRoad(startX,startY,endX,endY);
							break;
					
						case "HIGHWAY":
							r = new Highway(startX,startY,endX,endY);
							break;
						
						default:
							// collect all unknown road types and standard road under default
							r = new Road(startX,startY,endX,endY);
							break;
					}
					r.setStreetMap(streetMap);
					
					oneLane = lanes1.isSelected();
					twoLane = lanes2.isSelected();
					threeLane = lanes3.isSelected();
					int l = 1;
					if(oneLane) {l=1;}
					if(twoLane) {l=2;}
					if(threeLane) {l=3;}

					r.setLanes(l);
					if((int)(r.getLength() / visuals.getDivider()) >=2)
					{
						streetMap.addRoad(r);
						new CrossRoadDetection(streetMap);
					}
					else
					{
						streetMap.removeIntersection(in);
					}
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


			mouseX = (int) (e.getX()/visuals.getZoomMultiplier()-visuals.getChangeX());
			mouseY = (int) (e.getY()/visuals.getZoomMultiplier()-visuals.getChangeY());

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
				if(distance <= visuals.getMaxIntersectionSize()+10) 
				{
					Intersection colorRed = streetMap.getIntersectionByCoordinates(nearestX, nearestY);
					visuals.setDrawRed(colorRed);
				}
				else
				{
					visuals.setDrawRed(null);

				}

			}

			if(simulation.getCars().size()>0)
			{
				int nearestX = -1;
				int nearestY = -1;
				double distance = -1;
				for(Car car : simulation.getCars())
				{

					double distance2 = (double)(Math.sqrt(Math.pow(mouseX - car.getPositionX(), 2) + (Math.pow(mouseY - car.getPositionY(), 2))));
					// System.out.println("1 distance "+ distance+" distance 2 "+distance2);
					if (distance == -1) {
						distance = distance2;							
						nearestX = (int)car.getPositionX();						
						nearestY = (int)car.getPositionY();							
					}
					else if(distance2 < distance)
					{
						// System.out.println("2 distance "+ distance+" distance 2 "+distance2);
						distance = distance2;
						nearestX = (int)car.getPositionX();						
						nearestY = (int)car.getPositionY();								
					}	

				}
				if(distance <= 20) 
				{
					Car nearestCar = null;
					for(Car car: simulation.getCars())
					{
						if((int)car.getPositionX() == nearestX && (int)car.getPositionY() == nearestY)
						{
							nearestCar = car;
							break;
						}
					}
					simulation.setLastHoveredCar(nearestCar);
				}
				else
				{

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
