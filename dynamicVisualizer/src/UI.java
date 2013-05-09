import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public interface UI {
	public void Update(long dt);
	
	public BufferedImage Draw(Dimension d);
}
