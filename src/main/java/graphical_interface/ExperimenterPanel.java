package graphical_interface;

import buttons.DefaultButtonUI;
import buttons.MenuButton;
import core.Simulation;
import experiment.Experiment;
import type.Distribution;
import type.Strategy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

public class ExperimenterPanel extends JPanel {

	private Simulation simulation;
	private JButton addExperimentButton;
	private JButton saveButton;
	private JButton loadButton;
	private JPanel experiments_panel;

	public ExperimenterPanel(Simulation simulation) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.simulation = simulation;

		experiments_panel = new JPanel();
		experiments_panel.setLayout(new GridBagLayout());
		JScrollPane scroller = new JScrollPane(
				experiments_panel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
		);
		scroller.setPreferredSize(new Dimension(this.getWidth(), 400));

		saveButton = new MenuButton("Save");
		saveButton.setUI(new DefaultButtonUI());
		saveButton.addActionListener(new ActionListener() {
			int count = 0;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JPanel myPanel = new JPanel();
				myPanel.add(new JLabel("file name: "));
				JTextField fileName = new JTextField(20);
				myPanel.add(fileName);
				int result = JOptionPane.showConfirmDialog(null, myPanel, "Experiment Setup Name",
						JOptionPane.OK_CANCEL_OPTION);

				simulation.getExperimentWrapper().saveSetup(fileName.getText());
			}
		});

		loadButton = new MenuButton("Load");
		loadButton.setUI(new DefaultButtonUI());
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("./savefiles/"));
				int returnVal = fc.showOpenDialog(experiments_panel);
				File file = fc.getSelectedFile();
				if (file != null) {
					simulation.getExperimentWrapper().loadSetup(file);
					updateList();
				}
			}
		});

		addExperimentButton = new JButton("Add Experiment");
		addExperimentButton.setUI(new DefaultButtonUI());
		addExperimentButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String[] strategyList = { "BASIC_CYCLING", "PRIORITY_CYCLING", "COORDINATED",
						"INFORMED_CYCLING", "WEIGHTED_CYCLING", "QUEUE_WEIGHTED_CYCLING",
						"FLOW_WEIGHTED_CYCLING", "DENSITY_WEIGHTED_CYCLING"};
				String[] scheduleList = { "Empirical", "Poisson", "Gaussian"};

				JComboBox<String> strategy = new JComboBox<>(strategyList);
				JComboBox<String> schedule = new JComboBox<>(scheduleList);
				JCheckBox visualize = new JCheckBox("Visualize?");
				Experiment default_exp = new Experiment();

				JTextField duration = new JTextField(5); // in days
				JTextField inter = new JTextField(5); // time for car arrival
				JTextField phaseLength = new JTextField(5);
				JTextField fileName = new JTextField(20);
				
				JPanel new_experiment_dialog = new JPanel(new GridLayout(7, 2));
				new_experiment_dialog.add(new JLabel("Duration in Days:"));
				duration.setText(Integer.toString(default_exp.getSimulationLengthInDays()));
				new_experiment_dialog.add(duration);
				new_experiment_dialog.add(Box.createHorizontalStrut(15)); // a spacer
				new_experiment_dialog.add(new JLabel("Inter Arrival Time:"));
				inter.setText(Integer.toString(default_exp.getIaTime()));
				new_experiment_dialog.add(inter);
				new_experiment_dialog.add(Box.createHorizontalStrut(15)); // a spacer
				new_experiment_dialog.add(new JLabel("Control Strategy:"));
				new_experiment_dialog.add(strategy);
				new_experiment_dialog.add(Box.createHorizontalStrut(15)); // a spacer
				new_experiment_dialog.add(new JLabel("Phase Length:"));
				phaseLength.setText(Integer.toString(default_exp.getPhaseLength()));
				new_experiment_dialog.add(phaseLength);
				new_experiment_dialog.add(Box.createHorizontalStrut(15)); // a spacer
				new_experiment_dialog.add(new JLabel("Schedule:"));
				new_experiment_dialog.add(schedule);
				new_experiment_dialog.add(Box.createHorizontalStrut(15)); // a spacer
				new_experiment_dialog.add(new JLabel("fileName:"));
				new_experiment_dialog.add(fileName);
				new_experiment_dialog.add(Box.createHorizontalStrut(15)); // a spacer
				visualize.setSelected(true);
				new_experiment_dialog.add(visualize);
				int result = JOptionPane.showConfirmDialog(null, new_experiment_dialog, "Please Enter data",
						JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					System.out.println("duration value:  " + duration.getText());
					System.out.println("Inter arrival time value:   " + inter.getText());
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
							arrival_schedule = Distribution.EMPIRICAL;
							break;
					}

					Strategy control_strategy = Strategy.stringToType((String) strategy.getSelectedItem());

					Experiment exp = new Experiment(arrival_schedule,
							control_strategy,
							Integer.parseInt(duration.getText()),
							visualize.isSelected(),
							Integer.parseInt(inter.getText()),
							Integer.parseInt(phaseLength.getText())
					);
					
					exp.setName(fileName.getText());

					simulation.getExperimentWrapper().addExperiment(exp);
					drawExperimentBars();
					revalidate();
					repaint();
				}
			}
		});

		drawExperimentBars();
		this.add(scroller);
		this.add(addExperimentButton);
		this.add(saveButton);
		this.add(loadButton);
	}

	public void updateList() {
		drawExperimentBars();
		revalidate();
		repaint();
	}

	private void drawExperimentBars() {
		experiments_panel.removeAll();

		GridBagConstraints gbc;
		int pos = 0;
		ArrayList<ExperimentBar> bars = new ArrayList<>();
		for (Experiment exp : this.simulation.getExperimentWrapper().getAllExperiments()) {
			ExperimentBar bar = new ExperimentBar(exp, this.simulation.getExperimentWrapper(), this);
			if (this.simulation.getExperimentWrapper().currentExperiment() == exp) {
				bar.setBorder(BorderFactory.createDashedBorder(Color.RED, 2, 2, 2, true));
			} else {
				bar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
			}

			gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.NORTH;
			gbc.gridx = 0;
			gbc.gridy = pos;
			gbc.weightx = 1;
			experiments_panel.add(bar, gbc);

			pos++;
		}

		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.gridx = 0;
		gbc.gridy = pos;
		gbc.weightx = 1;
		gbc.weighty = 1;
		JPanel spacer = new JPanel();
		spacer.setBackground(null);
		experiments_panel.add(spacer, gbc);
	}
}
