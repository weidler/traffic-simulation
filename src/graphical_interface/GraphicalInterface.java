package graphical_interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.w3c.dom.events.MouseEvent;

import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;

public class GraphicalInterface extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	

	/**
	 * Create the frame.
	 */
	public GraphicalInterface() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 848, 534);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setResizable(false);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 639, 474);
		panel.setBackground(Color.CYAN);
		contentPane.add(panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(659, 11, 167, 474);
		panel_1.setBackground(Color.LIGHT_GRAY);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JButton startButton = new JButton("start");
		startButton.setBounds(34, 21, 89, 23);
		panel_1.add(startButton);
		startButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("start");
			}
			
		});
		
		Handlerclass handler = new Handlerclass();
		panel.addMouseListener(handler);
			
	
	}
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

		@Override
		public void mousePressed(java.awt.event.MouseEvent e) {
			System.out.println("x coordinate: "+e.getX());
			System.out.println("y coordinate: "+e.getY());
			System.out.println("");
			
		}

		@Override
		public void mouseReleased(java.awt.event.MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}
