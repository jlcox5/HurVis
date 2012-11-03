import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public interface UI {
	public void Update();
	
	public BufferedImage Draw(Dimension d);
}
