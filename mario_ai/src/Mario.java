import java.awt.*;

class Mario extends Sprite
{
    // Animation frames for drawing Mario
    // Note: adjust when modifying image array indexes
    private final int STILL_FRAME = 1;
    private final int RIGHT_FRAME1 = STILL_FRAME + 1;
    private final int RIGHT_FRAME2 = RIGHT_FRAME1 + 1;
    private final int LEFT_FRAME1 = RIGHT_FRAME2 + 1;
    private final int LEFT_FRAME2 = LEFT_FRAME1 + 1;
    private final int JUMP_FRAME = LEFT_FRAME2 + 1;

    private final double Y_DOWN_ACCEL = 2.2;// downward acceleration, 1.2 works
    private final int SCREEN_HEIGHT = 650;

    boolean goingRight;// is Mario going right?
    private int rightCounter;// count frames going right

    boolean goingLeft;// is Mario going left?
    private int leftCounter;// count frames going left

    // Keep track of the number of times Mario jumps
    int numJumps;

    // Keep track of the amount of coins
    int coins;

    // Confirm jump if Mario's y velocity changed direction ( - -> + )
    boolean negYVelocity;
    boolean posYVelocity;

    // Get sprite list from Model to check for collisions
    private Model model;

    //=================================================================================================================
    // Mario coordinate and JSON constructors
    //=================================================================================================================
    Mario(int startXPos, int startYPos, int spriteWidth, int spriteHeight, Model inputModel)
    {
        super(startXPos, startYPos, spriteWidth, spriteHeight);

        // Reference entire model, make updating master scrolling variables easier
        this.model = inputModel;

        this.goingRight = false;
        this.goingLeft = false;

        numJumps = 0;
        coins = 0;
        negYVelocity = false;
        posYVelocity = false;
    }

    Mario(Json j, Model inputModel)
    {
        super(j);

        // Reference sprite list in model so we can check for all sprites when updating, makes update more clean
        this.model = inputModel;

        this.goingRight = false;
        this.goingLeft = false;

        numJumps = 0;
        coins = 0;
        negYVelocity = false;
        posYVelocity = false;
    }

    Mario(Mario inputM, Model inputModel)
    {
        super(inputM);

        this.model = inputModel;

        this.goingRight = inputM.goingRight;
        this.goingLeft = inputM.goingLeft;

        this.numJumps = inputM.numJumps;
        this.coins = inputM.coins;

        this.negYVelocity = inputM.negYVelocity;
        this.posYVelocity = inputM.posYVelocity;
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
    public void update(int masterScrollPos)
    {
        // Update this sprite's scroll position
        this.scrollPos = masterScrollPos;

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

            // Increment jumps if y velocity changed
            if(negYVelocity && posYVelocity)
            {
                numJumps++;
            }

            // Reset y velocity change flags
            negYVelocity = false;
            posYVelocity = false;
        }

        negYVelocity = true;
        this.jumpFrames++;// keep counting to get jump velocity
        //-------------------------------------------------------------------------------------------------------------
        // Mario collides with: Brick, coin block; count number of coins
        //-------------------------------------------------------------------------------------------------------------
        for(int i = 0; i < model.sprites.size(); i++)
        {
            Sprite s = model.sprites.get(i);

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
                model.setMasterScrollSpeed(SCROLL_CONST);
                s.isHit = false;
            }
        }

        model.setMasterScrollPos(this.scrollPos);
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

    //=================================================================================================================
    // Sprite gets out of sprite
    // Note: may need to adjust with other sprite types that are not the player.
    //=================================================================================================================
    private void getOutOfSprite(Sprite that)
    {
        // Given sprite A (this) and sprite B (that)
        // Condition 1: check if right-side of sprite A overlaps with left side of sprite B
        // Condition 2: confirm sprite A was coming from the left by offsetting current position left

        // Left side of that: *same logic applies to other sides
        if
        (
                (this.x + this.w >= that.x - this.scrollPos) &&
                (this.x + this.w - LOCATION_OFFSET < that.x - this.scrollPos)
        )
        {
            model.setMasterScrollSpeed(0);

            // position = difference between overlapping entities
            this.scrollPos = that.x - (this.x + this.w);
            model.setMasterScrollPos(this.scrollPos);

        }
        //-------------------------------------------------------------------------------------------------------------
        // Right side of that
        else if
        (
                (this.x <= that.x + that.w - model.getMasterScrollPos()) &&
                (this.x + LOCATION_OFFSET > that.x + that.w - model.getMasterScrollPos())
        )
        {
            //this.scrollSpeed = 0;
            model.setMasterScrollSpeed(0);

            this.scrollPos = that.x + that.w - this.x + 1;// adding 1 to re-positioning will make do
            model.setMasterScrollPos(this.scrollPos);
        }
        //-------------------------------------------------------------------------------------------------------------
        // Bottom of that
        else if
        (
                (this.y <= that.y + that.h) &&
                (this.y + this.h > that.y + that.h)
        )
        {
            this.yVelocity = 0.0;// reset downward velocity for sprite A
            this.y = that.y + that.h + 1;// reposition sprite A

            // Increment number of coins
            if(that.isCoinBlock() && (((CoinBlock) that).coinLimit > 0))
            {
                coins++;
            }
        }
        //-------------------------------------------------------------------------------------------------------------
        // Top of that
        else if
        (
                (this.y + this.h >= that.y) &&
                (this.y - LOCATION_OFFSET < that.y)
        )
        {
            this.yVelocity = 0.0;
            this.jumpFrames = 0;
            this.y = that.y - this.h;

            if(negYVelocity && posYVelocity)
            {
                numJumps++;
            }

            negYVelocity = false;
            posYVelocity = false;
        }
    }
}