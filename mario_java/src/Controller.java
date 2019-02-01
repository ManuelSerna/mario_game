import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/*
Classes ActionListener, MouseListener, and KeyListener
are all APIs. All their abstract methods must all be implemented.
*/

class Controller implements ActionListener, MouseListener, KeyListener
{
	// Reference model to update sprites
	private Model model;

	// Store mouse coordinates when button is clicked
	private int mouseDownX;
	private int mouseDownY;

	// Flags go true when their corresponding key is pressed
	private boolean keyLeft;
	private boolean keyRight;
	private boolean keySpace;

	Controller(Model m)
	{
		model = m;

		mouseDownX = 0;
		mouseDownY = 0;

		keyLeft  = false;
		keyRight = false;
		keySpace = false;
	}

	public void mousePressed(MouseEvent e)
	{ 
        mouseDownX = e.getX();
        mouseDownY = e.getY();
    }

    public void mouseReleased(MouseEvent e)
    {
        int x1 = mouseDownX;// x when mouse is clicked
        int x2 = e.getX();// x when mouse button is released
        int y1 = mouseDownY;
        int y2 = e.getY();
        
        // get corners
        int left   = Math.min(x1, x2);
        int right  = Math.max(x1, x2);
        int top    = Math.min(y1, y2);
        int bottom = Math.max(y1, y2);
        
        // Change x to account for side-scrolling
		if((right - left > 20) && (bottom - top > 20))
		{
			//System.out.println("Brick placed, it was big enough");
			model.addBrick(left + Sprite.scrollPos, top, right - left, bottom - top);
		}
    }

	//=================================================================================================================
	// Mouse events
	//=================================================================================================================
	public void mouseClicked(MouseEvent e)
	{
		model.addBlock(mouseDownX + Sprite.scrollPos, mouseDownY);
	}

    public void mouseEntered(MouseEvent e)
	{

	}

    public void mouseExited(MouseEvent e)
	{

	}

	public void actionPerformed(ActionEvent e)
	{

	}

	//=================================================================================================================
	// Key events
	//=================================================================================================================
	public void keyPressed(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			// Directional keys - control Mario's movement
			case KeyEvent.VK_RIGHT:
				keyRight = true;
				model.mario.goingRight = true;
				break;
			case KeyEvent.VK_LEFT:
				keyLeft = true;
				model.mario.goingLeft = true;
				break;
			case KeyEvent.VK_SPACE:
				keySpace = true;
				break;
			case KeyEvent.VK_S:
                model.save("map.json");
                System.out.println("Model saved.");
                break;
            case KeyEvent.VK_L:
                model.load("map.json");
                System.out.println("Model loaded.");
                break;
                /*
            case KeyEvent.VK_C:
                model.sprites.clear();
                System.out.println("Screen cleared.");
                break;
                */
			case KeyEvent.VK_ESCAPE:
				System.out.println("Exiting Program.");
				System.exit(1);
				break;
		}
	}

	public void keyReleased(KeyEvent e)
	{
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_RIGHT:
				keyRight = false;
				model.mario.goingRight = false;
				break;
			case KeyEvent.VK_LEFT:
				keyLeft = false;
				model.mario.goingLeft = false;
				break;
			case KeyEvent.VK_SPACE: keySpace = false;
			break;
		}
	}

	public void keyTyped(KeyEvent e)
	{

	}

	//=================================================================================================================
    // Update scroll position in the model
	//=================================================================================================================
	void update()
	{
		// Increment/Decrement static variable, all this should probably stay here
		if(keyRight)
		{
			Sprite.scrollPos += Sprite.scrollSpeed;
		}

		if(keyLeft)
		{
			Sprite.scrollPos -= Sprite.scrollSpeed;
		}

		if(keySpace)
		{
			// Count number of frames for mario, if it is below a certain num, keep subtracting yVelocity
			if(model.mario.jumpFrames < 4)
			{
				model.mario.yVelocity -= 10.1;
			}
		}
	}
}