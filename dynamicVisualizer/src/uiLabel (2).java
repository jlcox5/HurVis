import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;


public class uiLabel implements uiControl {
	
	String    text;
	Bounds area;
	
	public uiLabel(Bounds narea, String ntext){
		text = ntext;
		area = narea;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Graphics g, Dimension d) {
		int textWidth = g.getFontMetrics().stringWidth(text);
		int textHeight = g.getFontMetrics().getHeight();
		Rectangle rect = area.project(d);
		float ftW = textWidth;
		float ftH = textHeight;
		boolean replaceFont = ftW > area.width;
		Font oldFont = null;
		if(replaceFont){
		   oldFont = g.getFont();
		   Font newFont = oldFont.deriveFont(oldFont.getSize2D()*((float)rect.width)/ftW);
		   g.setFont(newFont);
		}

		g.setColor(Color.green);
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
		g.setColor(Color.blue);
		g.drawString(text, rect.x, rect.y+(textHeight));
		if(replaceFont){
			g.setFont(oldFont);
		}
	}

	@Override
	public Bounds getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

}
