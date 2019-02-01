import java.awt.*;
import java.util.ArrayList;

public class GreenKoopa extends Sprite
{
    final int SCREEN_HEIGHT = 650;

    private boolean goingRight = false;
    private boolean goingLeft = true;

    // Get sprite list from Model to check for collisions
    private ArrayList<Sprite> modelSprites;

    {
        imageIndex = 11;
    }

    GreenKoopa(int inputX, int inputY, ArrayList<Sprite> inputList)
    {
        super(inputX, inputY, 60, 95);

        modelSprites = inputList;
    }

    GreenKoopa(Json ob, ArrayList<Sprite> inputList)
    {
        super(ob);

        modelSprites = inputList;
    }

    @Override
    public boolean isGKoopa()
    {
        return true;
    }

    void update()
    {
        // If green koopa hits any block, turn around
        for(int i = 0; i < modelSprites.size(); i++)
        {
            Sprite s = modelSprites.get(i);

            if (goingLeft)
            {
                this.x--;

                /*
                if(s.isGKoopa() || collision(s))
                {
                    getOutOfSprite(s);
                    goingRight = true;
                }*/
            }
            else if (goingRight)
            {
                this.x++;
                /*
                if(s.isGKoopa() || collision(s))
                {
                    getOutOfSprite(s);
                    goingLeft = true;
                }*/
            }
        }

        // Place green koopa on ground
        if(this.y >= SCREEN_HEIGHT-127-h)
        {
            this.jumpFrames = 0;// reset counter when hitting the ground
            this.yVelocity = 0.0;// stop on the ground
            this.y = SCREEN_HEIGHT-127-h;// adjust Mario coords
        }
    }
    void draw(Graphics g)
    {
        g.drawImage(images[imageIndex], this.x - scrollPos, y, w, h, null);
    }
}
