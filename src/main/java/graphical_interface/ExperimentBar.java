package graphical_interface;

import buttons.IconButtonUI;
import experiment.Experiment;
import experiment.ExperimentWrapper;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExperimentBar extends JPanel {

	private Experiment experiment;

	public ExperimentBar(Experiment exp, ExperimentWrapper wrapper, ExperimenterPanel container) {
		this.experiment = exp;
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		this.setPreferredSize(new Dimension(250, 50));

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
		JLabel distro_label = new JLabel(experiment.getArrivalGenerator().name().substring(0,1) + "(" + experiment.getIaTime() + ")");
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
		if (wrapper.isFinished(exp)) {
			JLabel status_label = new JLabel("\uF00C");
			status_label.setFont(IconFont.getFontAwesome());
			status_panel.add(status_label);
			status_label.setBackground(null);
		} else {
			status_panel.setLayout(new GridLayout(2, 1));

			JButton edit_button = new JButton("\uF044");
			edit_button.setUI(new IconButtonUI());
			edit_button.setBorderPainted(false);
			edit_button.setContentAreaFilled(false);
			edit_button.setOpaque(false);
			edit_button.setFocusPainted(false);
			edit_button.setMinimumSize(new Dimension(50, 50));
			edit_button.setPreferredSize(new Dimension(50, 50));
			edit_button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					container.updateList();
				}
			});

			JButton remove_button = new JButton("\uF2ED");
			remove_button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					wrapper.removeExperiment(exp);
					container.updateList();
				}
			});

			remove_button.setUI(new IconButtonUI());
			remove_button.setBackground(null);
			remove_button.setBorderPainted(false);
			remove_button.setContentAreaFilled(false);
			remove_button.setOpaque(false);
			remove_button.setFocusPainted(false);
			remove_button.setMinimumSize(new Dimension(10, 50));

			Font font = IconFont.getFontAwesome();
			//font = font.deriveFont(22);
			edit_button.setFont(font);
			remove_button.setFont(font);

			//status_panel.add(edit_button);
			status_panel.add(remove_button);
		}

		status_panel.setBackground(null);

		this.add(duration_panel);
		this.add(distro_panel);
		this.add(strategy_panel);
		this.add(status_panel);
	}

}
