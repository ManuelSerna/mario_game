//*********************************************************************************************************************
// Brick class - contains coords for brick, as well as input from a Json object
//*********************************************************************************************************************
import java.awt.*;

class Brick extends Sprite
{
	{
		imageIndex = 7;
	}

	Brick(int inputX, int inputY, int inputW, int inputH)
	{
		super(inputX, inputY, inputW, inputH);
	}

	Brick(Json ob)
	{
		super(ob);
	}

	Brick(Brick inputBr)
	{
		super(inputBr);
	}

	@Override
	public boolean isBrick()
	{
		return true;
	}

	@Override
	public void update(int masterScrollPos)
	{
		// Update this sprite's scroll position
		this.scrollPos = masterScrollPos;
	}

	// Draw brick, and increment/decrement with scrolling position
	@Override
	void draw(Graphics g)
	{
		g.drawImage(images[imageIndex], this.x - scrollPos, y, w, h, null);
	}
}
