import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public interface uiControl extends MouseListener, MouseMotionListener {
   public void draw(Graphics g, Dimension d);
   public Bounds getBounds();
}
