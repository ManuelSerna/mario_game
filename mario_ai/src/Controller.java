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

	private boolean controlAI;

	{
		controlAI = true;
	}

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
        
        // change x to account for side-scrolling
		if((right - left > 20) && (bottom - top > 20))
		{
			model.addBrick(left + model.getMasterScrollPos(), top, right - left, bottom - top);
		}
    }

	//=================================================================================================================
	// Mouse events
	//=================================================================================================================
	public void mouseClicked(MouseEvent e)
	{
		model.addBlock(mouseDownX + model.getMasterScrollPos(), mouseDownY);
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

			// Save the current model
			case KeyEvent.VK_S:
                model.save("map.json");
                System.out.println("Model saved.");
				System.out.println("-----------------------------------------------------");
                break;

            // Load from json file
            case KeyEvent.VK_L:
                model.load("map.json");
                System.out.println("Model loaded.");
				System.out.println("-----------------------------------------------------");
                break;

			// Clear all sprites, except the current background and Mario
            case KeyEvent.VK_C:
				for(int i = model.sprites.size()-1; i >= 0; i--)
				{
					// Remove instance if sprite is NOT a background and Mario
					if(!model.sprites.get(i).isBackground() && !model.sprites.get(i).isMario())
					{
						System.out.println("Sprite " + i + ": " + model.sprites.get(i) + " removed.");
						model.sprites.remove(i);
					}
				}
				System.out.println("-----------------------------------------------------");
                break;

            // Print Sprites in collection to the console
			case KeyEvent.VK_P:
				for(int i = 0; i < model.sprites.size(); i++)
				{
					System.out.println("Sprite " + i + ": " + model.sprites.get(i));
				}
				System.out.println("-----------------------------------------------------");
				break;

            // Exit program
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
				model.mario.goingRight =false;
				break;
			case KeyEvent.VK_LEFT:
				keyLeft = false;
				model.mario.goingLeft = false;
				break;
			case KeyEvent.VK_SPACE:
				keySpace = false;
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
		if(controlAI)
		{
			// Evaluate each possible action
			double score_run          = model.evaluateAction(Action.RUN, 0);
			double score_jump         = model.evaluateAction(Action.JUMP, 0);
			double score_jump_and_run = model.evaluateAction(Action.JUMP_AND_RUN, 0);

			// Do the best action
			if (score_jump_and_run > score_jump && score_jump_and_run > score_run)
			{
				model.performAction(Action.JUMP_AND_RUN);
			}
			else if (score_jump >= score_run)
			{
				model.performAction(Action.JUMP);
			}
			else
			{
				model.mario.goingRight = true;
				model.performAction(Action.RUN);
			}
		}
		else
		{
			if (keyRight) { model.setMasterScrollPos(model.getMasterScrollPos() + model.getMasterScrollSpeed()); }
			if (keyLeft) { model.setMasterScrollPos(model.getMasterScrollPos() - model.getMasterScrollSpeed()); }
			if (keySpace)
			{
				if (model.mario.jumpFrames < 4)
				{
					model.mario.yVelocity -= 10.1;
					model.mario.posYVelocity = true;
				}
			}
		}
	}
}