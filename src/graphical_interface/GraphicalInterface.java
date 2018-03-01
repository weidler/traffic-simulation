package graphical_interface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.w3c.dom.events.MouseEvent;

import datastructures.Connection;
import datastructures.Intersection;

import java.awt.Graphics2D;

import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
/**
 * 
 * @author thomas
 * this class is the interface
 */

public class GraphicalInterface extends JFrame {

	/**
	 * the panel int which drawing are shown.
	 */
	private JPanel contentPane = new Visuals();

	/**
	 * create interface.
	 */
	/**
	 * clickcounter checks if a click was a startpoint or endpoint of a road.
	 */
	private int clickCounter = 0;
	/**
	 * start coordinates of road.
	 */
	private int startX;
	private int startY;
	/**
	 * end coordinates of road.
	 */
	private int endX;
	private int endY;
	private final boolean TWO_WAY = true;
	
	public GraphicalInterface() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 848, 534);
		
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
	/**
	 * 
	 * @author thomas
	 * this is the mouselistener
	 */
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
		/**
		 *  this is the only used method and registers the clicks.
		 */
		@Override
		public void mousePressed(java.awt.event.MouseEvent e) {
			clickCounter++;
			
			int x = e.getX();
			int y = e.getY();
			if(clickCounter == 1)
			{
				System.out.println("startcoords");
				startX = x;
				startY = y;
			}
			else
			{
				System.out.println("endcoords");
				endX = x;
				endY = y;
				int length = (int) (Math.sqrt((endX - startX)^2 + (endY - startY)^2)) ;
				Connection c = new Connection(length, TWO_WAY);
				Intersection in = new Intersection(x, y);
				repaint(); 
				
			}		
			
			System.out.println("x coordinate: "+x);
			System.out.println("y coordinate: "+y);
			System.out.println("");
			System.out.println("changed");
			
			if (clickCounter == 2) {
				clickCounter = 0;
			}
			
		}

		@Override
		public void mouseReleased(java.awt.event.MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
		
	}
	
	
	
}
