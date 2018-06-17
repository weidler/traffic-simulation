package graphical_interface;

import buttons.DefaultButtonUI;
import com.google.common.collect.Lists;
import core.Simulation;
import datastructures.StreetMap;
import experiment.Experiment;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import road.Road;
import type.Distribution;
import type.Strategy;
import type.ZoneType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ExperimenterPanel extends JPanel {

	Simulation simulation;
	JButton addExperimentButton;
	JPanel experiments_panel;

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

		addExperimentButton = new JButton("Add Experiment");
		addExperimentButton.setUI(new DefaultButtonUI());
		addExperimentButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String[] strategyList = { "circulating lights", "weigthed cycling","coordinated","informed cycling","waiting" };
				String[] scheduleList = { "empirical", "poisson", "gaussian"};

				JComboBox<String> strategy = new JComboBox<>(strategyList);
				JComboBox<String> schedule = new JComboBox<>(scheduleList);
				JCheckBox visualize = new JCheckBox("visualize?");
				JTextField duration = new JTextField(5); // in days
				JTextField inter = new JTextField(5); // time for car arrival
				JTextField phaseLength = new JTextField(5);

				JPanel myPanel = new JPanel();
				myPanel.add(new JLabel("Duration in Days:"));
				duration.setText("1");
				myPanel.add(duration);
				myPanel.add(Box.createHorizontalStrut(15)); // a spacer
				myPanel.add(new JLabel("Inter Arrival Time:"));
				inter.setText("10");
				myPanel.add(inter);
				myPanel.add(Box.createHorizontalStrut(15)); // a spacer
				myPanel.add(new JLabel("Control Strategy:"));
				myPanel.add(strategy);
				myPanel.add(Box.createHorizontalStrut(15)); // a spacer
				myPanel.add(new JLabel("Phase Length:"));
				phaseLength.setText("5");
				myPanel.add(phaseLength);
				myPanel.add(Box.createHorizontalStrut(15)); // a spacer
				myPanel.add(new JLabel("Schedule:"));
				myPanel.add(schedule);
				myPanel.add(Box.createHorizontalStrut(15)); // a spacer
				visualize.setSelected(true);
				myPanel.add(visualize);
				int result = JOptionPane.showConfirmDialog(null, myPanel, "Please Enter data",
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

					Strategy control_strategy;
					switch ((String) strategy.getSelectedItem()) {
						case "circulating lights":
							control_strategy = Strategy.BENCHMARK_CYCLING;
							break;

						case "weigthed cycling":
							control_strategy = Strategy.WEIGHTED_CYCLING;
							break;

						case "coordinated":
							control_strategy = Strategy.COORDINATED;
							break;
						case "waiting":
							control_strategy = Strategy.WAITING;
							break;

						case "informed cycling":
							control_strategy = Strategy.INFORMED_CYCLING;
							break;

						default:
							control_strategy = Strategy.BENCHMARK_CYCLING;
							break;
					}

					Experiment exp = new Experiment(arrival_schedule,
							control_strategy,
							Integer.parseInt(duration.getText()),
							visualize.isSelected(),
							Integer.parseInt(inter.getText()),
							Integer.parseInt(phaseLength.getText())
					);

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
	}

	private void drawExperimentBars() {
		experiments_panel.removeAll();

		GridBagConstraints gbc;
		int pos = 0;
		ArrayList<ExperimentBar> bars = new ArrayList<>();
		for (Experiment exp : this.simulation.getExperimentWrapper().getAllExperiments()) {
			ExperimentBar bar = new ExperimentBar(exp);
			bar.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));

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
