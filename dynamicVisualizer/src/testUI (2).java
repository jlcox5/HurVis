import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class testUI implements UI {
	
	public testUI(){
		test = new uiLabel(new Bounds(0.75f,0.0f,.25f,.25f),"Hello World");
	}

	@Override
	public void Update() {
		// TODO Auto-generated method stub

	}

	@Override
	public BufferedImage Draw(Dimension d) {
		// TODO Auto-generated method stub
        BufferedImage render = new BufferedImage(d.width, d.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = render.createGraphics();
        
        test.draw(g, d);
        
        return render;
	}
	
	private uiControl test;

}
