
package graphical_interface;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import core.Simulation;
import datastructures.Intersection;
import datastructures.StreetMap;
import datatype.Point;
import experiment.Experiment;
import road.DirtRoad;
import road.Highway;
import road.Road;
import type.Distribution;
import type.RoadType;
import type.Strategy;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.RepaintManager;
import javax.swing.border.Border;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;

import car.Car;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;

/**
 * 
 * @author thomas this class is the interface
 */

public class GraphicalInterface extends JFrame implements ComponentListener{

	private JCheckBox visualize = new JCheckBox("visualize?");
	private JTextField duration = new JTextField(5); // in days
	private JTextField density = new JTextField(5); // number of cars
	private JComboBox<String> strategy;
	private JComboBox<String> schedule;

	private final JFileChooser fc = new JFileChooser();
	private final int DISTANCE_BETWEEN_INTERSECTIONS = 30;

	/**
	 * LAYOUT PARAMETERS
	 */
	private final int menu_width = 200;
	private int menu_height;
	private Point menu_origin;
	
	private int map_width;
	private int map_height;
	private Point map_origin;
	
	private int info_width;
	private final int info_height = 70;
	private final Point info_origin = new Point(0, 0);
	
	private final int button_width = 150;
	private final int button_height = 30;
	private final int button_x = (this.menu_width - this.button_width) / 2;
	private final int button_y_diff = 10;
	private int initial_button_offset = this.info_height;
	private int button_x_diff = 10;
	private int round_button_diameter = 50;
	private Border button_border = BorderFactory.createEmptyBorder();
	
	private final Color menu_bg = Color.decode("#3a3a3a");
	private final Color map_bg = Color.decode("#57af6b");
	private final Color info_bg = Color.decode("#3a3a3a");
	
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

	/**
	 * JPanel that shows the roads etc.
	 */

	private Visuals visuals;
	private JTextField txtMinNumberOf;
	private JTextField txtMaxNumberOf;
	private Car lastHovered;
	private JTextField lanesTextField;
	private JLabel addLabel = new JLabel("add");
	private ArrayList<Intersection> toRemove = new ArrayList<Intersection>();

	private int numb_lanes = 1;
	protected RoadType road_type = RoadType.ROAD;
	private Color contrast_font_color = Color.WHITE;




	/**
	 * create interface. including buttons and listeners
	 */
	public GraphicalInterface(Simulation simulation) {
		String[] strateyList = { "circulating lights" };
		String[] scheduleList = { "poisson", "gaussian" };
		strategy = new JComboBox<>(strateyList);
		schedule = new JComboBox<>(scheduleList);

		this.simulation = simulation;
		this.streetMap = simulation.getStreetMap();
		this.visuals = new Visuals(simulation);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1200, 700);
		this.adjustPanelSizes();
		setResizable(false);
		
		contentPane.setBorder(BorderFactory.createEmptyBorder());
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		this.setFocusable(true);
		this.requestFocusInWindow();

		JPanel drawPanel = visuals;
		drawPanel.setBounds((int) map_origin.x, (int) map_origin.y, map_width, map_height);
		drawPanel.setBorder(BorderFactory.createEmptyBorder());
		drawPanel.setBackground(this.map_bg);
		contentPane.add(drawPanel);
		
		JPanel infoPanel = new InfoPanel(simulation);
		infoPanel.setBounds((int) info_origin.x, (int) info_origin.y, info_width, info_height);
		infoPanel.setBorder(BorderFactory.createEmptyBorder());
		infoPanel.setBackground(this.info_bg);
		contentPane.add(infoPanel);

		// ARROW KEY LISTENERS
		InputMap im = drawPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "down");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right");

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

		/**
		 * MENU ELEMENTs
		 */
		
		
		// PANEL
		JPanel menuPanel = new MenuPanel(this.info_height);
		menuPanel.setBounds((int) menu_origin.x, (int) menu_origin.y, menu_width, menu_height);
		menuPanel.setBackground(this.menu_bg);
		menuPanel.setBorder(BorderFactory.createEmptyBorder());
		contentPane.add(menuPanel);
		menuPanel.setLayout(null);
		
		menuPanel.setForeground(this.contrast_font_color );
		
		// TITLE
		
		
		// BUTTONS
		JButton clearButton = new JButton("clear");
		clearButton.setLocation(button_x, this.calculateInMenuYPosition(0));
		clearButton.setUI(new CriticalButtonUI());
		clearButton.setBorder(this.button_border);
		clearButton.setSize(button_width, button_height);
		menuPanel.add(clearButton);
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearMap();
				simulation.stop();
			}
		});

		JButton addCar = new JButton("add car");
		addCar.setBorder(this.button_border);
		addCar.setUI(new DefaultButtonUI());
		addCar.setBounds(button_x, this.calculateInMenuYPosition(4), button_width, button_height);
		menuPanel.add(addCar);
		addCar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!simulation.isRunning() ) {// && !selected) {
					simulation.addRandomCar();
					repaint();
				}
			}
		});
		
		JSlider slider = new JSlider();
		slider.setBounds(button_x, this.calculateInMenuYPosition(3), button_width, button_height);
		slider.setValue(50);
		slider.setEnabled(false);
		slider.setBackground(null);
		menuPanel.add(slider);

		JButton startButton = new JButton("Start");
		startButton.setBounds(button_x, this.calculateInMenuYPosition(11), button_width/2 - button_x_diff, button_height);
		startButton.setUI(new ImportantButtonUI());
		startButton.setBorder(this.button_border);
		menuPanel.add(startButton);
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				simulation.start();

			}
		});
		
		JButton stopButton = new JButton("Pause");
		stopButton.setBounds(this.button_x + this.button_width/2 + this.button_x_diff, this.calculateInMenuYPosition(11), button_width/2 - button_x_diff, button_height);
		stopButton.setUI(new DefaultButtonUI());
		menuPanel.add(stopButton);
		stopButton.setBorder(this.button_border);
		stopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				simulation.stop();

			}
		});

		JButton zoomInButton = new JButton("+");
		zoomInButton.setBounds(this.button_x + this.button_width/2 + this.button_x_diff, this.calculateInMenuYPosition(2), button_width/2 - button_x_diff, button_height);
		zoomInButton.setUI(new DefaultButtonUI());
		zoomInButton.setBorder(this.button_border);
		menuPanel.add(zoomInButton);
		zoomInButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (slider.getValue() < 99) {
					visuals.IncreaseZoomMultiplier();
					System.out.println("zoom in");
					slider.setValue((int) (50 * visuals.getZoomMultiplier()));
					repaint();
				}
			}
		});

		JButton zoomOutButton = new JButton("-");
		zoomOutButton.setBounds(button_x, this.calculateInMenuYPosition(2), button_width/2 - this.button_x_diff, button_height);
		zoomOutButton.setUI(new DefaultButtonUI());
		zoomOutButton.setBorder(this.button_border);
		menuPanel.add(zoomOutButton);
		zoomOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (slider.getValue() > 1) {
					visuals.DecreaseZoomMultiplier();
					System.out.println("zoom out");
					slider.setValue((int) (50 * visuals.getZoomMultiplier()));
					repaint();
				}
			}
		});

		JButton resetPositionButton = new JButton("Reset Camera");
		resetPositionButton.setBounds(button_x, this.calculateInMenuYPosition(1), button_width, button_height);
		resetPositionButton.setUI(new DefaultButtonUI());
		resetPositionButton.setBorder(this.button_border);
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

		JButton saveButton = new JButton("Save");
		saveButton.setBounds(button_x, this.calculateInMenuYPosition(10), button_width/2 - button_x_diff, button_height);
		saveButton.setUI(new DefaultButtonUI());
		saveButton.setBorder(this.button_border);
		menuPanel.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			int count = 0;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				count++;
				streetMap.toString();

				BufferedWriter bw = null;
				FileWriter fw = null;
				File f = new File("./savefiles/streetmap" + count + ".txt");
				try {
					while (f.exists() && !f.isDirectory()) {
						count++;
						f = new File("./savefiles/streetmap" + count + ".txt");
					}

					fw = new FileWriter(f);
					bw = new BufferedWriter(fw);
					bw.write(streetMap.toString());
					bw.close();
					fw.close();
					System.out.println("saved: " + "./savefiles/streetmap" + count + ".txt");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		JButton loadButton = new JButton("Load");
		loadButton.setBounds(this.button_x + this.button_width/2 + this.button_x_diff, this.calculateInMenuYPosition(10), button_width/2 - button_x_diff, button_height);
		loadButton.setUI(new DefaultButtonUI());
		loadButton.setBorder(this.button_border);
		menuPanel.add(loadButton);
		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				fc.setCurrentDirectory(new File("./savefiles/"));
				int returnVal = fc.showOpenDialog(drawPanel);
				File file = fc.getSelectedFile();
				if (file != null) {

					// JSON from file to Object
					try {
						clearMap();
						Scanner sc = new Scanner(file);
						sc.useDelimiter(",");
						String next = sc.next();
						Boolean change = false;
						while (!next.equals("p")) {

							if (next.equals("#")) {
								change = true;
								next = sc.next();
							} else if (!change) {
								int x = Integer.parseInt(next);
								next = sc.next();
								int y = Integer.parseInt(next);
								streetMap.addIntersection(new Intersection(x, y));
								next = sc.next();
							} else {
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
								System.out.println("next1: "+next);
								streetMap.getRoads().get(streetMap.getRoads().size()-1).setRoadType(next);
								next = sc.next();
								System.out.println("next2: "+next);
								streetMap.getRoads().get(streetMap.getRoads().size()-1).setLanes(Integer.parseInt(next));
								System.out.println("next3: "+next);
								next = sc.next();
								System.out.println("next4: "+next);
							}
							System.out.println("loading..." + next);
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
		
		// SIMULATION CONTROLS
		JButton slowDownButton = new JButton("Speed-");
		slowDownButton.setBounds(button_x, this.calculateInMenuYPosition(12), button_width/2 - this.button_x_diff, button_height);
		slowDownButton.setUI(new CriticalButtonUI());
		slowDownButton.setBorder(this.button_border);
		menuPanel.add(slowDownButton);
		slowDownButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simulation.setSimulatedSecondsPerRealSecond(Math.max(0, simulation.getSimulatedSecondsperRealSecond() - 10));
			}
		});
		
		JButton speedUpButton = new JButton("Speed+");
		speedUpButton.setBounds(this.button_x + this.button_width/2 + this.button_x_diff, this.calculateInMenuYPosition(12), button_width/2 - button_x_diff, button_height);
		speedUpButton.setUI(new ImportantButtonUI());
		speedUpButton.setBorder(this.button_border);
		menuPanel.add(speedUpButton);
		speedUpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simulation.setSimulatedSecondsPerRealSecond(simulation.getSimulatedSecondsperRealSecond() + 10);
			}
		});
		
		// EXIT
		JButton exitButton = new JButton("Exit");
		exitButton.setBounds(this.menu_width/2 - this.button_width/4, (this.menu_height - 50) - button_height - button_y_diff, button_width/2, button_height);
		exitButton.setUI(new CriticalButtonUI());
		exitButton.setBorder(this.button_border);
		menuPanel.add(exitButton);
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simulation.stop();
				System.exit(0);
			}
		});
		
//
//		txtMinNumberOf = new JTextField();
//		txtMinNumberOf.setText("0");
//		txtMinNumberOf.setBounds(61, 559, 96, 20);
//		menuPanel.add(txtMinNumberOf);
//		txtMinNumberOf.setColumns(10);
//
//		txtMaxNumberOf = new JTextField();
//		txtMaxNumberOf.setBounds(61, 590, 96, 20);
//		txtMaxNumberOf.setText("0");
//		menuPanel.add(txtMaxNumberOf);
//		txtMaxNumberOf.setColumns(10);

//		JLabel lblMin = new JLabel("Min");
//		lblMin.setBounds(button_x, 562, 28, 14);
//		lblMin.setForeground(this.contrast_font_color );
//		menuPanel.add(lblMin);
//
//		JLabel lblMax = new JLabel("Max");
//		lblMax.setBounds(button_x, 596, 28, 14);
//		lblMax.setForeground(this.contrast_font_color );
//		menuPanel.add(lblMax);
//
//		JButton randomGraphButton = new JButton("random graph");
//		randomGraphButton.setUI(new DefaultButtonUI());
//		Random rnd = new Random();
//		int maxX = drawPanel.getX() + drawPanel.getBounds().width - 10;
//		int minX = drawPanel.getX();
//		int maxY = drawPanel.getY() + drawPanel.getBounds().height - 10;
//		int minY = drawPanel.getY();
//
//		randomGraphButton.setBounds(button_x, this.calculateInMenuYPosition(12), button_width, button_height);
//		menuPanel.add(randomGraphButton);
//		randomGraphButton.setBorder(this.button_border);
//		randomGraphButton.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//
//				clearMap();
//				int maxNumberOfRoads = Integer.parseInt(txtMaxNumberOf.getText());
//				int minNumberOfRoads = Integer.parseInt(txtMinNumberOf.getText());
//
//				int numberOfRoads = rnd.nextInt(maxNumberOfRoads - minNumberOfRoads + 1) + minNumberOfRoads;
//
//				for (int i = 0; i < numberOfRoads; i++) {
//					if (streetMap.getIntersections().size() == 0) {
//						int coordinateX1 = rnd.nextInt(maxX - minX + 1) + minX;
//						int coordinateY1 = rnd.nextInt(maxY - minY + 1) + minY;
//						int coordinateX2 = rnd.nextInt(maxX - minX + 1) + minX;
//						int coordinateY2 = rnd.nextInt(maxY - minY + 1) + minY;
//						boolean oke = false;
//						while (!oke) {
//							double distance2 = (double) (Math.sqrt(Math.pow(coordinateX1 - coordinateX2, 2)
//									+ (Math.pow(coordinateY1 - coordinateY2, 2))));
//							if (distance2 > DISTANCE_BETWEEN_INTERSECTIONS) {
//								oke = true;
//							} else {
//								coordinateX2 = rnd.nextInt(maxX - minX + 1) + minX;
//								coordinateY2 = rnd.nextInt(maxY - minY + 1) + minY;
//							}
//						}
//
//						streetMap.addIntersection(new Intersection(coordinateX1, coordinateY1));
//						streetMap.addIntersection(new Intersection(coordinateX2, coordinateY2));
//						Road r = new Road(streetMap.getIntersections().get(streetMap.getIntersections().size() - 1),
//								streetMap.getIntersections().get(streetMap.getIntersections().size() - 2));
//						r.setStreetMap(streetMap);
//						streetMap.addRoad(r);
//					} else {
//						Intersection startIntersection = streetMap.getIntersections()
//								.get(rnd.nextInt((streetMap.getIntersections().size() - 1) - 0 + 1) + 0);
//						int coordinateX1 = rnd.nextInt(maxX - minX + 1) + minX;
//						int coordinateY1 = rnd.nextInt(maxY - minY + 1) + minY;
//
//						boolean oke = false;
//						while (!oke) {
//							double distance2 = (double) (Math
//									.sqrt(Math.pow(coordinateX1 - startIntersection.getXCoord(), 2)
//											+ (Math.pow(coordinateY1 - startIntersection.getYCoord(), 2))));
//							if (distance2 > DISTANCE_BETWEEN_INTERSECTIONS) {
//								oke = true;
//							} else {
//								coordinateX1 = rnd.nextInt(maxX - minX + 1) + minX;
//								coordinateY1 = rnd.nextInt(maxY - minY + 1) + minY;
//							}
//						}
//
//						streetMap.addIntersection(new Intersection(coordinateX1, coordinateY1));
//						Road r = new Road(streetMap.getIntersections().get(streetMap.getIntersections().size() - 1),
//								startIntersection);
//						r.setStreetMap(streetMap);
//						streetMap.addRoad(r);
//					}
//
//				}
//
//				repaint();
//			}
//		});

		String[] lane_options = {"One", "Two", "Three"};
		JComboBox<String> numb_lanes_cbox = new JComboBox<String>(lane_options);
		numb_lanes_cbox.setSelectedIndex(0);
		numb_lanes_cbox.setBounds(this.button_x, this.calculateInMenuYPosition(5), this.button_width, this.button_height);
		numb_lanes_cbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switch ((String) numb_lanes_cbox.getSelectedItem()) {
					case "One":
						numb_lanes = 1;
						break;

					case "Two":
						numb_lanes = 2;
						break;
						
					case "Three":
						numb_lanes = 3;
						break;
					
					default:
						break;
				}
			}
		});
		menuPanel.add(numb_lanes_cbox);
		
		String[] road_options = {"Normal", "Highway", "Dirt"};
		JComboBox<String> road_type_cbox = new JComboBox<String>(road_options);
		road_type_cbox.setSelectedIndex(0);
		road_type_cbox.setBounds(this.button_x, this.calculateInMenuYPosition(6), this.button_width, this.button_height);
		road_type_cbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switch ((String) road_type_cbox.getSelectedItem()) {
					case "Normal":
						road_type = RoadType.ROAD;
						break;

					case "Highway":
						road_type = RoadType.HIGHWAY;
						break;
						
					case "Dirt":
						road_type = RoadType.DIRT_ROAD;
						break;
					
					default:
						break;
				}
			}
		});
		menuPanel.add(road_type_cbox);
		
//		addLabel.setBounds(button_x, 354, 147, 14);
//		menuPanel.add(addLabel);

		JButton addDeleteButton = new JButton("add/delete");
		addDeleteButton.setBounds(button_x, this.calculateInMenuYPosition(8), button_width, button_height);
		addDeleteButton.setUI(new DefaultButtonUI());
		menuPanel.add(addDeleteButton);
		addDeleteButton.setBorder(this.button_border);
		addDeleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (addLabel.getText().equals("add")) {
					addLabel.setText("delete");
				} else {
					addLabel.setText("add");
				}

			}
		});

		JButton experimentButton = new JButton("Experiment");
		experimentButton.setBounds(button_x, this.calculateInMenuYPosition(9), button_width, button_height);
		experimentButton.setUI(new DefaultButtonUI());
		experimentButton.setBorder(this.button_border);
		menuPanel.add(experimentButton);
		experimentButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				JPanel myPanel = new JPanel();
				myPanel.add(new JLabel("duration in days:"));
				myPanel.add(duration);
				myPanel.add(Box.createHorizontalStrut(15)); // a spacer
				myPanel.add(new JLabel("amout of cars per day:"));
				myPanel.add(density);
				myPanel.add(Box.createHorizontalStrut(15)); // a spacer
				myPanel.add(new JLabel("strategy:"));
				myPanel.add(strategy);
				myPanel.add(Box.createHorizontalStrut(15)); // a spacer
				myPanel.add(new JLabel("schedule:"));
				myPanel.add(schedule);
				myPanel.add(Box.createHorizontalStrut(15)); // a spacer
				myPanel.add(visualize);
				int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter data",
						JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					System.out.println("duration value:  " + duration.getText());
					System.out.println("density value:   " + density.getText());
					System.out.println("schedule value:  " + schedule.getSelectedIndex());
					System.out.println("strategy value:  " + strategy.getSelectedIndex());
					System.out.println("visualize value: " + visualize.isSelected());
					// then start experiment.

					Distribution arrival_schedule;
					switch ((String) schedule.getSelectedItem()) {
						case "poisson":
							arrival_schedule = Distribution.POISSON;
							break;

						case "gaussian":
							arrival_schedule = Distribution.GAUSSIAN;
							break;

						case "empirical":
							arrival_schedule = Distribution.EMPIRICAL;
							break;

						default:
							arrival_schedule = Distribution.GAUSSIAN;
							break;
					}

					Strategy control_strategy;
					switch ((String) strategy.getSelectedItem()) {
						default:
							control_strategy = Strategy.BENCHMARK_CYCLING;
							break;
					}

					Experiment exp = new Experiment(arrival_schedule, control_strategy,
							Integer.parseInt(duration.getText()), visualize.isSelected());
					simulation.setExperiment(exp);

				}

			}
		});

		// ADDS MOUSE AND KEY LISTENER
		Handlerclass handler = new Handlerclass();
		drawPanel.addMouseListener(handler);
		drawPanel.addMouseMotionListener(handler);
		drawPanel.setFocusable(true);

		drawPanel.requestFocusInWindow();

	}

	private void adjustPanelSizes() {
		this.map_height = this.getHeight() - this.info_height;
		this.map_width = this.getWidth() - this.menu_width;
		this.map_origin = new Point(0, this.info_height);
		
		this.menu_height = this.getHeight();
		this.menu_origin = new Point(this.map_width, 0);
		
		this.info_width = this.map_width;
	}

	public void clearMap() {
		simulation.reset();
		streetMap.clearMap();
		visuals.resetZoomMultiplier();
		repaint();
	}

	public void componentResized(ComponentEvent ce) {
		adjustPanelSizes();
	}
	
	/**
	 * 
	 * @author thomas this is the mouse/key listener. in here all the clicks and
	 *         movement of the mouse are registered and roads and intersection are
	 *         created.
	 */
	private class Handlerclass implements MouseListener, MouseMotionListener {

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
		 * this is the only used method and registers the clicks.
		 */
		@Override
		public void mousePressed(java.awt.event.MouseEvent e) {

			clickCounter++;

			int x = (int) (e.getX() / visuals.getZoomMultiplier() - visuals.getChangeX());
			int y = (int) (e.getY() / visuals.getZoomMultiplier() - visuals.getChangeY());

			if (addLabel.getText().equals("add")) {
				if (clickCounter == 1) {

					if (e.getButton() == MouseEvent.BUTTON3) {
						if (streetMap.getRoads().size() > 0) {
							int nearestX = -1;
							int nearestY = -1;
							double distance = -1;
							for (Intersection sec : streetMap.getIntersections()) {

								double distance2 = (double) (Math
										.sqrt(Math.pow(x - sec.getXCoord(), 2) + (Math.pow(y - sec.getYCoord(), 2))));
								// System.out.println("1 distance "+ distance+" distance 2 "+distance2);
								if (distance == -1) {
									distance = distance2;
									nearestX = sec.getXCoord();
									nearestY = sec.getYCoord();
								} else if (distance2 < distance) {
									// System.out.println("2 distance "+ distance+" distance 2 "+distance2);
									distance = distance2;
									nearestX = sec.getXCoord();
									nearestY = sec.getYCoord();
								}

							}

						}
					}

					if (streetMap.getRoads().isEmpty()) {
						startX = x;
						startY = y;
						Intersection in = new Intersection(startX, startY);
						streetMap.addIntersection(in);

					} else {
						int nearestX = -1;
						int nearestY = -1;
						double distance = -1;
						for (Intersection sec : streetMap.getIntersections()) {

							double distance2 = (double) (Math
									.sqrt(Math.pow(x - sec.getXCoord(), 2) + (Math.pow(y - sec.getYCoord(), 2))));
							// System.out.println("1 distance "+ distance+" distance 2 "+distance2);
							if (distance == -1) {
								distance = distance2;
								nearestX = sec.getXCoord();
								nearestY = sec.getYCoord();
							} else if (distance2 < distance) {
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
					}

					visuals.setStartPosX(startX);
					visuals.setStartPosY(startY);
					if (clickCounter != 0) {
						visuals.setDrawLine(true);
					}

				} else {

					if (e.getButton() == MouseEvent.BUTTON3) {
						visuals.setDrawLine(false);
						clickCounter = 0;
					} else {

						int nearestX = -1;
						int nearestY = -1;
						double distance = -1;
						for (Intersection sec : streetMap.getIntersections()) {

							double distance2 = (double) (Math
									.sqrt(Math.pow(x - sec.getXCoord(), 2) + (Math.pow(y - sec.getYCoord(), 2))));
							// System.out.println("1 distance "+ distance+" distance 2 "+distance2);
							if (distance == -1) {
								distance = distance2;
								nearestX = sec.getXCoord();
								nearestY = sec.getYCoord();
							} else if (distance2 < distance) {
								// System.out.println("2 distance "+ distance+" distance 2 "+distance2);
								distance = distance2;
								nearestX = sec.getXCoord();
								nearestY = sec.getYCoord();
							}

						}
						
						if (distance < visuals.getMaxIntersectionSize()) {
							endX = nearestX;
							endY = nearestY;
						} else {
							endX = x;
							endY = y;
						}

						Intersection in = new Intersection(endX, endY);
						streetMap.addIntersection(in);

						Road r;
						switch (road_type) {
							case ROAD:
								r = new Road(startX, startY, endX, endY);
								break;
							
							case DIRT_ROAD:
								r = new DirtRoad(startX, startY, endX, endY);
								break;

							case HIGHWAY:
								r = new Highway(startX, startY, endX, endY);
								break;

							default:
								// collect all unknown road types and standard road under default
								r = new Road(startX, startY, endX, endY);
								break;
						}
						
						r.setStreetMap(streetMap);
						int l = numb_lanes;

						r.setLanes(l);
						if ((int) (r.getLength() / visuals.getDivider()) >= 2) {
							streetMap.addRoad(r);
						} else {
							streetMap.removeIntersection(in);
						}

						clickCounter = 0;
						
						visuals.setDrawLine(false);
					}

					System.out.println(streetMap.getIntersections());
					System.out.println(streetMap.getRoads());
					repaint();
				}
			} else {

				if (clickCounter == 1) {
					System.out.println("select first point to remove");
					if (!streetMap.getRoads().isEmpty()) {
						int nearestX = -1;
						int nearestY = -1;
						double distance = -1;
						for (Intersection sec : streetMap.getIntersections()) {
							double distance2 = (double) (Math
									.sqrt(Math.pow(x - sec.getXCoord(), 2) + (Math.pow(y - sec.getYCoord(), 2))));
							// System.out.println("1 distance "+ distance+" distance 2 "+distance2);
							if (distance == -1) {
								distance = distance2;
								nearestX = sec.getXCoord();
								nearestY = sec.getYCoord();
							} else if (distance2 < distance) {
								// System.out.println("2 distance "+ distance+" distance 2 "+distance2);
								distance = distance2;
								nearestX = sec.getXCoord();
								nearestY = sec.getYCoord();
							}

						}
						toRemove.add(streetMap.getIntersectionByCoordinates(nearestX, nearestY));
						System.out.println("toRemove size " + toRemove.size());
					} else {
						clickCounter--;
					}

				} else {
					System.out.println("select second point to remove");

					int nearestX = -1;
					int nearestY = -1;
					double distance = -1;
					for (Intersection sec : streetMap.getIntersections()) {
						double distance2 = (double) (Math
								.sqrt(Math.pow(x - sec.getXCoord(), 2) + (Math.pow(y - sec.getYCoord(), 2))));
						// System.out.println("1 distance "+ distance+" distance 2 "+distance2);
						if (distance == -1) {
							distance = distance2;
							nearestX = sec.getXCoord();
							nearestY = sec.getYCoord();
						} else if (distance2 < distance) {
							// System.out.println("2 distance "+ distance+" distance 2 "+distance2);
							distance = distance2;
							nearestX = sec.getXCoord();
							nearestY = sec.getYCoord();
						}
					}
					toRemove.add(streetMap.getIntersectionByCoordinates(nearestX, nearestY));
					boolean remove1 = true;
					boolean remove2 = true;
					if (toRemove.get(0).getConnectedIntersections().size() > 1) {
						System.out.println("connections " + toRemove.get(0).getConnectedIntersections().size());
						remove1 = false;
					}
					if (toRemove.get(1).getConnectedIntersections().size() > 1) {
						System.out.println("connections " + toRemove.get(1).getConnectedIntersections().size());
						remove2 = false;
					}

					System.out.println(
							"toRemove size " + toRemove.size() + " remove1: " + remove1 + " remove2: " + remove2);
					streetMap.removeRoadBetween(toRemove.get(0), toRemove.get(1));
					if (remove1) {
						streetMap.removeIntersection(toRemove.get(0));
					}
					if (remove2) {
						streetMap.removeIntersection(toRemove.get(1));
					}
					toRemove.clear();
					clickCounter = 0;
				}
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

			mouseX = (int) (e.getX() / visuals.getZoomMultiplier() - visuals.getChangeX());
			mouseY = (int) (e.getY() / visuals.getZoomMultiplier() - visuals.getChangeY());

			if (streetMap.getIntersections().size() > 0) {
				int nearestX = -1;
				int nearestY = -1;
				double distance = -1;
				for (Intersection sec : streetMap.getIntersections()) {

					double distance2 = (double) (Math
							.sqrt(Math.pow(mouseX - sec.getXCoord(), 2) + (Math.pow(mouseY - sec.getYCoord(), 2))));
					// System.out.println("1 distance "+ distance+" distance 2 "+distance2);
					if (distance == -1) {
						distance = distance2;
						nearestX = sec.getXCoord();
						nearestY = sec.getYCoord();
					} else if (distance2 < distance) {
						// System.out.println("2 distance "+ distance+" distance 2 "+distance2);
						distance = distance2;
						nearestX = sec.getXCoord();
						nearestY = sec.getYCoord();
					}

				}
				if (distance <= visuals.getMaxIntersectionSize() + 10) {
					Intersection colorRed = streetMap.getIntersectionByCoordinates(nearestX, nearestY);
					visuals.setDrawRed(colorRed);
				} else {
					visuals.setDrawRed(null);

				}
			}
			
			visuals.setMousePosX(mouseX);
			visuals.setMousePosY(mouseY);
			repaint();

		}

	}
	
	private int calculateInMenuYPosition(int position) {
		return this.initial_button_offset + (position * this.button_height) + (position * this.button_y_diff);
	}

	public void redraw() {
		repaint();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
