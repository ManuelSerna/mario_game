import javax.swing.JPanel;
import java.awt.*;

class View extends JPanel
{
	// Reference model to update graphics
	private Model model;

	View(Model m)
	{
		model = m;
	}

	// Draw all sprite images
	public void paintComponent(Graphics g)
	{
		// Original model sprites
        for(int i = 0; i < model.sprites.size(); i++)
        {
            Sprite s = model.sprites.get(i);
            s.draw(g);// polymorphism happens here, s may be Mario or a block
        }
    }
}