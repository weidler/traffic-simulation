package buttons;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * https://www.javacodex.com/More-Examples/2/14
 */
public class RoundButton extends JButton {

  public RoundButton(String label) {
    super(label);
    setFocusable(false);
    setContentAreaFilled(false);
  }

  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    if (getModel().isArmed()) {
      g.setColor(Color.gray);
    } else {
      g.setColor(getBackground());
    }
    g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);

    super.paintComponent(g);
  }

  protected void paintBorder(Graphics2D g) {
    g.setColor(this.getBackground().darker());
    g.drawOval(0, 0, getSize().width - 1, getSize().height - 1);
  }

  Shape shape;
  public boolean contains(int x, int y) {
    // If the button has changed size,  make a new shape object.
    if (shape == null || !shape.getBounds().equals(getBounds())) {
      shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
    }
    return shape.contains(x, y);
  }
}
