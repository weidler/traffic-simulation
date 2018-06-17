package graphical_interface;

import experiment.Experiment;

import javax.swing.*;
import java.awt.*;

public class ExperimentBar extends JPanel {

	private Experiment experiment;

	public ExperimentBar(Experiment exp) {
		this.experiment = exp;
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		this.setPreferredSize(new Dimension(200, 50));

		Dimension panel_dim = new Dimension(50, 50);

		JPanel duration_panel = new JPanel();
		duration_panel.setPreferredSize(panel_dim);
		duration_panel.setBackground(Color.BLUE);
		JLabel duration_label = new JLabel(Integer.toString(exp.getSimulationLengthInDays()));
		duration_label.setForeground(Color.WHITE);
		duration_panel.add(duration_label);

		JPanel distro_panel = new JPanel();
		distro_panel.setPreferredSize(panel_dim);
		distro_panel.setBackground(Color.RED);
		JLabel distro_label = new JLabel(experiment.getArrivalGenerator().name().substring(0,1));
		distro_label.setForeground(Color.WHITE);
		distro_panel.add(distro_label);

		JPanel strategy_panel = new JPanel();
		strategy_panel.setPreferredSize(panel_dim);
		strategy_panel.setForeground(Color.WHITE);
		strategy_panel.setBackground(Color.GREEN);
		JLabel strategy_label = new JLabel(experiment.getControlStrategy().name().substring(0,2));
		strategy_label.setForeground(Color.WHITE);
		strategy_panel.add(strategy_label);

		JPanel status_panel = new JPanel();
		status_panel.setPreferredSize(panel_dim);
		status_panel.setForeground(Color.WHITE);
		status_panel.setBackground(Color.lightGray);

		this.add(duration_panel);
		this.add(distro_panel);
		this.add(strategy_panel);
		this.add(status_panel);
	}

}
