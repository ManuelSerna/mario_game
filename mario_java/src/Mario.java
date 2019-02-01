import java.awt.*;
import java.util.ArrayList;

class Mario extends Sprite
{
    // Animation frames for drawing Mario
    // Note: adjust when modifying image array indexes
    private final int STILL_FRAME = 1;
    private final int RIGHT_FRAME1 = STILL_FRAME + 1;
    private final int RIGHT_FRAME2 = RIGHT_FRAME1 + 1;
    private final int LEFT_FRAME1 = RIGHT_FRAME2 + 1;
    private final int LEFT_FRAME2 = LEFT_FRAME1 + 1;

    private final double Y_DOWN_ACCEL = 2.2;// downward acceleration, 1.2 works
    private final int SCREEN_HEIGHT = 650;

    boolean goingRight;// is Mario going right?
    private int rightCounter;// count frames going right

    boolean goingLeft;// is Mario going left?
    private int leftCounter;// count frames going left

    // Get sprite list from Model to check for collisions
    private ArrayList<Sprite> modelSprites;

    //=================================================================================================================
    // Mario coordinate and JSON constructors
    //=================================================================================================================
    Mario(int startXPos, int startYPos, int spriteWidth, int spriteHeight, ArrayList<Sprite> inputList)
    {
        super(startXPos, startYPos, spriteWidth, spriteHeight);

        // Reference sprite list in model so we can check for all sprites when updating, makes update more clean
        this.modelSprites = inputList;

        this.goingRight = false;
        this.goingLeft = false;
    }

    Mario(Json j, ArrayList<Sprite> inputList)
    {
        super(j);

        // Reference sprite list in model so we can check for all sprites when updating, makes update more clean
        this.modelSprites = inputList;

        this.goingRight = false;
        this.goingLeft = false;
    }

    //=================================================================================================================
    // Check sprite type
    //=================================================================================================================
    @Override
    public boolean isMario()
    {
        return true;
    }

    //=================================================================================================================
    // Update Mario physics
    //=================================================================================================================
    @Override
    void update()
    {
        //-------------------------------------------------------------------------------------------------------------
        // Mario jumping mechanic
        //-------------------------------------------------------------------------------------------------------------
        this.yVelocity += Y_DOWN_ACCEL;
        this.y += yVelocity;

        // If sprite hits the ground, draw sprite above it (update y coord)
        if(this.y >= SCREEN_HEIGHT-127-h)
        {
            this.jumpFrames = 0;// reset counter when hitting the ground
            this.yVelocity = 0.0;// stop on the ground
            this.y = SCREEN_HEIGHT-127-h;// adjust Mario coords
        }

        this.jumpFrames++;// keep counting to get jump velocity
        //-------------------------------------------------------------------------------------------------------------
        // Mario collides with: Brick or coin block
        //-------------------------------------------------------------------------------------------------------------
        for(int i = 0; i < modelSprites.size(); i++)
        {
            Sprite s = modelSprites.get(i);

            if((s.isBrick() || s.isCoinBlock()) && collision(s))
            {
                // Mark the target coin block as hit, only want one block to spit out coins at a time
                if(s.isCoinBlock())
                {
                    s.isHit = true;
                }

                getOutOfSprite(s);
            }
            else
            {
                Sprite.scrollSpeed = Sprite.SCROLL_CONST;
                s.isHit = false;
            }
        }
    }
    //=================================================================================================================
    // Draw Mario: animate Mario's movement according to direction
    //=================================================================================================================
    @Override
    void draw(Graphics g)
    {
        if(this.isMario())
        {
            // Move horizontally RIGHT
            if (goingRight)
            {
                if (rightCounter % 9 == 0)
                { animationFrame = RIGHT_FRAME1; }
                else if (rightCounter % 9 == 4)
                { animationFrame = RIGHT_FRAME2; }

                g.drawImage(images[animationFrame], this.x, this.y, null);
                rightCounter++;
            }
            // Move horizontally LEFT
            else if (goingLeft)
            {
                if (leftCounter % 9 == 0)
                { animationFrame = LEFT_FRAME1; }
                else if (leftCounter % 9 == 4)
                { animationFrame = LEFT_FRAME2; }

                g.drawImage(images[animationFrame], this.x, this.y, null);
                leftCounter++;
            }
            // Remain STILL
            else
            {
                // Reset counters, and display idle Mario frame
                rightCounter = 0;
                leftCounter = 0;
                g.drawImage(images[STILL_FRAME], this.x, this.y, null);
            }
        }
    }
}