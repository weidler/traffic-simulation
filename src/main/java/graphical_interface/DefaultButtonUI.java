package graphical_interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;

public class DefaultButtonUI extends BasicButtonUI implements MouseListener {

	protected Color     hoverColor   = Color.decode("#527ec4");
    protected Color     normalColor  = Color.decode("#5378b2");
    protected Color     pressedColor = Color.decode("#5378b2");
    protected Color     btnFontColor = Color.WHITE;
    protected ImageIcon normalIcon;
    protected ImageIcon hoverIcon;
    protected ImageIcon pressedIcon;
    protected Font      btnFont;
    
    public DefaultButtonUI() {
		super();
		this.setParameterDefaults();
	}

	protected void setParameterDefaults() {
    	this.hoverColor   = Color.decode("#527ec4");
        this.normalColor  = Color.decode("#5378b2");
        this.pressedColor = Color.decode("#5378b2");
	}

	@Override
    public void installUI(JComponent comp) {
        super.installUI(comp);
        comp.addMouseListener(this);
    }

    @Override
    public void uninstallUI(JComponent comp) {
        super.uninstallUI(comp); 
        comp.removeMouseListener(this);
    }

    @Override
    protected void installDefaults(AbstractButton btn) {
        super.installDefaults(btn);
        btn.setIcon(this.normalIcon);
        btn.setBackground(this.normalColor);
        btn.setForeground(this.btnFontColor);
        btn.setFont(this.btnFont);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
    }

    @Override
    public Dimension getPreferredSize(JComponent comp) {
        Dimension dim = super.getPreferredSize(comp);
        if(comp.getBorder() != null){
            Insets insets = comp.getBorder().getBorderInsets(comp);
            dim.setSize(dim.width + insets.left + insets.right, dim.height + insets.top + insets.bottom);
        }
        return dim;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    	changeButtonStyle((JButton) e.getComponent(), this.pressedColor, this.pressedIcon);
    }

	@Override
	public void mousePressed(MouseEvent e) {
		changeButtonStyle((JButton) e.getComponent(), this.pressedColor, this.pressedIcon);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		changeButtonStyle((JButton)e.getComponent(), this.normalColor, this.normalIcon);
	}

  @Override
  public void mouseEntered(MouseEvent e)
  {
    changeButtonStyle((JButton) e.getComponent(), this.hoverColor, this.hoverIcon);
  }

  @Override
  public void mouseExited(MouseEvent e) {
    changeButtonStyle((JButton)e.getComponent(), this.normalColor, this.normalIcon);
  }
  
  private void changeButtonStyle(JButton btn, Color color, ImageIcon icon){
      btn.setBackground(color);
      btn.setIcon(icon);
  }
}
