import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

public interface Visualizer {
   public void Update();
   public Image Draw(Dimension d);
   public Image getRender(int reqw, int reqh);
}
