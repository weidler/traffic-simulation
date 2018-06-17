
package graphical_interface;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;

import buttons.*;
import core.Simulation;
import datastructures.Intersection;
import datastructures.StreetMap;
import experiment.Experiment;
import road.DirtRoad;
import road.Highway;
import road.Road;
import type.Distribution;
import type.RoadType;
import type.Strategy;
import type.ZoneType;

import javax.swing.border.Border;

import car.Car;

import static com.sun.org.apache.xerces.internal.utils.SecuritySupport.getResourceAsStream;

/**
 *
 * @author thomas this class is the interface
 */

public class GraphicalInterface extends JFrame {

	private JTextField fileName = new JTextField(20);
	private final JFileChooser fc = new JFileChooser();
	private final int DISTANCE_BETWEEN_INTERSECTIONS = 30;

	private final int button_width = 150;
	private final int button_height = 30;

	private final Dimension button_dimensions = new Dimension(button_width, button_height);
	private final Dimension small_button_dimensions = new Dimension(button_width/2, button_height);

	private final Dimension round_button_dimensions = new Dimension(button_height, button_height);
	private final Dimension small_round_button_dimensions = new Dimension((int) (button_height/1.5), (int) (button_height/1.5));

	private Border button_border = BorderFactory.createEmptyBorder();
	private Font icon_font = IconFont.getFontAwesome();
	private Font small_icon_font = icon_font.deriveFont(Font.PLAIN, 9);

	private final Color menu_bg = Color.decode("#3a3a3a");
	private final Color map_bg = Color.decode("#5e5d5d");
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
	private JPanel contentPane;
	private JPanel menuPanel;
	private JPanel topPanel;
	private JPanel drawPanel;

	private StreetMap streetMap;
	private Simulation simulation;

	/**
	 * JPanel that shows the roads etc.
	 */
	private JCheckBox directedRoad;
	private Visuals visuals;
	private JTextField txtMinNumberOf;
	private JTextField txtMaxNumberOf;
	private Car lastHovered;
	private JTextField lanesTextField;
	private JLabel addLabel = new JLabel("add");
	private ArrayList<Intersection> toRemove = new ArrayList<Intersection>();

	private int numb_lanes = 1;
	protected RoadType road_type = RoadType.ROAD;
	protected ZoneType zone_type = ZoneType.RESIDENTIAL;
	private Color contrast_font_color = Color.WHITE;

	JPanel populationPanel;
	JPanel experimenterPanel;


	/**
	 * create interface. including buttons and listeners
	 */
	public GraphicalInterface(Simulation simulation) {
		super("Traffic Simulation");

		this.simulation = simulation;
		this.streetMap = simulation.getStreetMap();
		this.visuals = new Visuals(simulation);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(1200, 700));
		setResizable(true);
		setFocusable(true);
		requestFocusInWindow();
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setBorder(BorderFactory.createEmptyBorder());
		contentPane.setLayout(new GridBagLayout());

		//  MENU PANEL
		menuPanel = new MenuPanel(new GridBagLayout());
		menuPanel.setPreferredSize(new Dimension(200, 700));
		menuPanel.setMinimumSize(new Dimension(200, 700));
		menuPanel.setBorder(BorderFactory.createEmptyBorder());
		menuPanel.setBackground(this.menu_bg);
		menuPanel.setForeground(this.contrast_font_color );

		// DRAW PANEL
		drawPanel = visuals;
		drawPanel.setPreferredSize(new Dimension(1000, 600));
		drawPanel.setMinimumSize(new Dimension(1000, 600));
		drawPanel.setBorder(BorderFactory.createEmptyBorder());
		drawPanel.setBackground(this.map_bg);

		// TOP PANEL
		topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());
		topPanel.setPreferredSize(new Dimension(1000, 100));
		topPanel.setMinimumSize(new Dimension(1000, 100));
		topPanel.setBorder(BorderFactory.createEmptyBorder());
		topPanel.setBackground(this.info_bg);

		GridBagConstraints c;
		// TOP PANEL
		c = new GridBagConstraints();
		defineGBC(c, GridBagConstraints.HORIZONTAL, 0, 0, 1, 1, 1, 0);
		contentPane.add(topPanel, c);

		// MAP PANEL
		c = new GridBagConstraints();
		defineGBC(c, GridBagConstraints.BOTH, 0, 1, 1, 1, 1, 1);
		contentPane.add(drawPanel, c);

		// MENU PANEL
		c = new GridBagConstraints();
		defineGBC(c, GridBagConstraints.VERTICAL, 1, 0, 1, 2, 0, 1);
		contentPane.add(menuPanel, c);

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

		// BUTTONS
		JButton clearButton = new MenuButton("Reset");
		clearButton.setPreferredSize(button_dimensions);
		clearButton.setUI(new CriticalButtonUI());
		clearButton.setBorder(this.button_border);

		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				reset();
				simulation.stop();
			}
		});

		JSlider slider = new JSlider();
		slider.setValue(50);
		slider.setEnabled(false);
		slider.setBackground(null);

		JButton startButton = new RoundButton("\uF04b");
		startButton.setFont(icon_font);
		startButton.setPreferredSize(round_button_dimensions);
		startButton.setUI(new ImportantButtonUI());
		startButton.setBorder(this.button_border);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				simulation.start();
			}
		});

		JButton stopButton = new RoundButton("\uf04d");
		stopButton.setFont(icon_font);
		stopButton.setPreferredSize(round_button_dimensions);
		stopButton.setUI(new DefaultButtonUI());
		stopButton.setBorder(this.button_border);
		stopButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				simulation.stop();

			}
		});

		JButton zoomInButton = new MenuButton("+");
		zoomInButton.setPreferredSize(small_button_dimensions);
		zoomInButton.setUI(new DefaultButtonUI());
		zoomInButton.setBorder(this.button_border);
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

		JButton zoomOutButton = new MenuButton("-");
		zoomOutButton.setPreferredSize(small_button_dimensions);
		zoomOutButton.setUI(new DefaultButtonUI());
		zoomOutButton.setBorder(this.button_border);
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

		JButton resetPositionButton = new MenuButton("Reset Camera");
		resetPositionButton.setPreferredSize(button_dimensions);
		resetPositionButton.setUI(new DefaultButtonUI());
		resetPositionButton.setBorder(this.button_border);
		resetPositionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				visuals.resetPosition();
				visuals.resetZoomMultiplier();
				slider.setValue(50);
				repaint();

			}
		});

		JButton saveButton = new MenuButton("\uF0C7");
		saveButton.setFont(icon_font);
		saveButton.setPreferredSize(small_button_dimensions);
		saveButton.setUI(new DefaultButtonUI());
		saveButton.setBorder(this.button_border);
		saveButton.addActionListener(new ActionListener() {
			int count = 0;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JPanel myPanel = new JPanel();
				myPanel.add(new JLabel("file name: "));
				fileName.setText("new_map");
				myPanel.add(fileName);
				int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter a Map Name",
						JOptionPane.OK_CANCEL_OPTION);

				streetMap.save(fileName.getText());
			}
		});

		JButton loadButton = new MenuButton("\uF07C");
		loadButton.setFont(icon_font);
		loadButton.setPreferredSize(small_button_dimensions);
		loadButton.setUI(new DefaultButtonUI());
		loadButton.setBorder(this.button_border);
		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				fc.setCurrentDirectory(new File("./savefiles/"));
				int returnVal = fc.showOpenDialog(drawPanel);
				File file = fc.getSelectedFile();
				if (file != null) {
					reset();
					streetMap.load(file);
					repaint();
				}
			}
		});

		// SIMULATION CONTROLS
		JButton pauseButton = new RoundButton("\uF04C");
		pauseButton.setFont(small_icon_font);
		pauseButton.setPreferredSize(small_round_button_dimensions);
		pauseButton.setUI(new DefaultButtonUI());
		pauseButton.setBorder(this.button_border);
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simulation.setSimulatedSecondsPerRealSecond(0);
				simulation.setFullSpeed(false);
			}
		});

		JButton normalSpeed = new RoundButton("\uF105");
		normalSpeed.setFont(small_icon_font);
		normalSpeed.setPreferredSize(small_round_button_dimensions);
		normalSpeed.setUI(new DefaultButtonUI());
		normalSpeed.setBorder(this.button_border);
		normalSpeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simulation.setSimulatedSecondsPerRealSecond(1);
				simulation.setFullSpeed(false);
			}
		});

		JButton fastSpeed = new RoundButton("\uF101");
		fastSpeed.setFont(small_icon_font);
		fastSpeed.setPreferredSize(small_round_button_dimensions);
		fastSpeed.setUI(new DefaultButtonUI());
		fastSpeed.setBorder(this.button_border);
		fastSpeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simulation.setSimulatedSecondsPerRealSecond(100);
				simulation.setFullSpeed(false);
			}
		});

		JButton fullSpeedButton = new RoundButton("\uF135");
		fullSpeedButton.setUI(new CriticalButtonUI());
		fullSpeedButton.setForeground(Color.WHITE);
		fullSpeedButton.setFont(small_icon_font);
		fullSpeedButton.setPreferredSize(small_round_button_dimensions);
		fullSpeedButton.setBorder(this.button_border);
		fullSpeedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simulation.setFullSpeed(!simulation.getFullSpeed());
			}
		});

		// EXIT
		JButton exitButton = new MenuButton("Exit");
		exitButton.setPreferredSize(button_dimensions);
		exitButton.setUI(new CriticalButtonUI());
		exitButton.setBorder(this.button_border);
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simulation.stop();
				System.exit(0);
			}
		});

		String[] lane_options = {"One", "Two", "Three"};
		JComboBox<String> numb_lanes_cbox = new JComboBox<String>(lane_options);
		numb_lanes_cbox.setSelectedIndex(0);
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


		String[] road_options = {"Normal", "Highway", "Dirt"};
		JComboBox<String> road_type_cbox = new JComboBox<String>(road_options);
		road_type_cbox.setSelectedIndex(0);
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


		String[] area_options = {"Residential", "Mixed", "Commercial", "Industrial"};
		JComboBox<String> area_type_cbox = new JComboBox<String>(area_options);
		area_type_cbox.setSelectedIndex(0);
		area_type_cbox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switch ((String) area_type_cbox.getSelectedItem()) {
					case "Residential":
						zone_type = ZoneType.RESIDENTIAL;
						break;

					case "Mixed":
						zone_type = ZoneType.MIXED;
						break;

					case "Commercial":
						zone_type = ZoneType.COMMERCIAL;
						break;

					case "Industrial":
						zone_type = ZoneType.INDUSTRIAL;
						break;

					default:
						break;
				}
			}
		});


		directedRoad = new JCheckBox("Directed");
		directedRoad.setSelected(false);
		directedRoad.setBackground(null);
		directedRoad.setForeground(Color.WHITE);
		directedRoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

			}
		});

		JCheckBox deleteRoadBox = new JCheckBox("Delete");
		deleteRoadBox.setSelected(false);
		deleteRoadBox.setBackground(null);
		deleteRoadBox.setForeground(Color.WHITE);
		deleteRoadBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (deleteRoadBox.isSelected()) {
					addLabel.setText("delete");
				} else {
					addLabel.setText("add");
				}
			}
		});

		JButton populationPopUpButton = new MenuButton("Show Population Info");
		populationPopUpButton.setPreferredSize(button_dimensions);
		populationPopUpButton.setUI(new DefaultButtonUI());
		populationPopUpButton.setBorder(this.button_border);
		populationPopUpButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				populationPanel = new PopulationPanel(streetMap);
				JOptionPane pane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE);
				pane.add(populationPanel);

				JDialog dialog = pane.createDialog(GraphicalInterface.this, "Population Overview");
				dialog.setModal(false);
				dialog.show();
			}
		});

		JButton openExperimenterButton = new MenuButton("\uF0C3 Open Experimenter");
		openExperimenterButton.setFont(icon_font);
		openExperimenterButton.setPreferredSize(button_dimensions);
		openExperimenterButton.setUI(new DefaultButtonUI());
		openExperimenterButton.setBorder(this.button_border);
		openExperimenterButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				experimenterPanel = new ExperimenterPanel(simulation);
				JOptionPane pane = new JOptionPane(null, JOptionPane.PLAIN_MESSAGE);
				pane.add(experimenterPanel);

				JDialog dialog = pane.createDialog(GraphicalInterface.this, "Experiments");
				dialog.setModal(false);
				dialog.show();
			}
		});

		// SOME PANELS

		JPanel clock_panel = new InfoPanel(simulation);
		clock_panel.setPreferredSize(new Dimension(150, 100));
		clock_panel.setBorder(BorderFactory.createEmptyBorder());
		clock_panel.setBackground(null);

		clock_panel.add(pauseButton);
		clock_panel.add(normalSpeed);
		clock_panel.add(fastSpeed);
		clock_panel.add(fullSpeedButton);

		JPanel title_panel = new TitlePanel(100);
		title_panel.setPreferredSize(new Dimension(200, 100));
		title_panel.setMinimumSize(new Dimension(200, 100));
		title_panel.setBackground(null);
		title_panel.setForeground(this.contrast_font_color);

		populationPanel = new PopulationPanel(streetMap);

		// APPEND THE ELEMENTS TO THEIR RESPECTIVE PANEL

		topPanel.add(stopButton);
		topPanel.add(clock_panel);
		topPanel.add(startButton);

		ArrayList<Component[]> menu_components = new ArrayList<>();
		menu_components.add(new Component[]{clearButton});
		menu_components.add(new Component[]{zoomInButton, zoomOutButton});
		menu_components.add(new Component[]{resetPositionButton});
		menu_components.add(new Component[]{slider});

		menu_components.add(new Component[]{numb_lanes_cbox});
		menu_components.add(new Component[]{road_type_cbox});
		menu_components.add(new Component[]{area_type_cbox});
		menu_components.add(new Component[]{directedRoad, deleteRoadBox});

		menu_components.add(new Component[]{saveButton, loadButton});

		menu_components.add(new Component[]{populationPopUpButton});
		menu_components.add(new Component[]{openExperimenterButton});

		// CREATE GRID
		GridBagConstraints gbc_menu;

		gbc_menu = new GridBagConstraints();
		defineGBC(gbc_menu, GridBagConstraints.BOTH, 0, 0, 2, 1, 1, 0);
		gbc_menu.anchor = GridBagConstraints.NORTHWEST;
		menuPanel.add(title_panel, gbc_menu);

		int gridy = 1;
		for (Component[] comps : menu_components) {
			gbc_menu = new GridBagConstraints();

			int gridwidth = 2;
			if (comps.length == 2) gridwidth = 1;

			int gridx = 0;
			for (Component comp : comps) {
				comp.setMaximumSize(button_dimensions);
				comp.setMinimumSize(button_dimensions);
				defineGBC(gbc_menu, GridBagConstraints.BOTH, gridx, gridy, gridwidth, 1, 1, 0);
				gbc_menu.anchor = GridBagConstraints.NORTHWEST;
				gbc_menu.insets = new Insets(0, 10,10,10);

				menuPanel.add(comp, gbc_menu);
				gridx++;
			}
			gridy++;
		}

		// this spacer fills up bottom space and pushes menu updwards
		JPanel spacer = new JPanel();
		spacer.setBackground(null);
		gbc_menu = new GridBagConstraints();
		defineGBC(gbc_menu, GridBagConstraints.BOTH, 0, gridy, 2, 1, 1, 1);
		gbc_menu.anchor = GridBagConstraints.SOUTHWEST;
		menuPanel.add(spacer, gbc_menu);

		// EXIT button
		gbc_menu = new GridBagConstraints();
		exitButton.setMinimumSize(button_dimensions);
		defineGBC(gbc_menu, GridBagConstraints.BOTH, 0, gridy + 1, 2, 1, 1, 0);
		gbc_menu.anchor = GridBagConstraints.NORTH;
		gbc_menu.insets = new Insets(0, 50,10,50);
		menuPanel.add(exitButton, gbc_menu);



		// ADDS MOUSE AND KEY LISTENER
		Handlerclass handler = new Handlerclass();
		drawPanel.addMouseListener(handler);
		drawPanel.addMouseMotionListener(handler);
		drawPanel.setFocusable(true);

		drawPanel.requestFocusInWindow();

	}

	public void reset() {
		simulation.reset();
		streetMap.clearMap();
		visuals.resetZoomMultiplier();
		repaint();
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

						switch (zone_type) {
							case MIXED:
								r.setZoneType(ZoneType.MIXED);
								break;
							case RESIDENTIAL:
								r.setZoneType(ZoneType.RESIDENTIAL);
								break;
							case INDUSTRIAL:
								r.setZoneType(ZoneType.INDUSTRIAL);
								break;
							case COMMERCIAL:
								r.setZoneType(ZoneType.COMMERCIAL);
								break;
							default:
								r.setZoneType(ZoneType.MIXED);
								break;
						}


						r.setStreetMap(streetMap);
						int l = numb_lanes;

						r.setLanes(l);
						if (directedRoad.isSelected()) 
						{
							r.toggleDirected();
						}
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

	public void defineGBC(GridBagConstraints c, int fill, int gridx, int gridy, int gridwidth, int gridheight, int weightx, int weighty) {
		c.fill = fill;
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = gridwidth;
		c.gridheight = gridheight;
		c.weightx = weightx;
		c.weighty = weighty;
	}

	public void redraw() {
		repaint();
	}

	public Object getPopulationPanel() {
		return this.populationPanel;
	}

}
