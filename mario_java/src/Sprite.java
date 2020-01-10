//*********************************************************************************************************************
// Sprite class: contains all the basic information for a sprite
//*********************************************************************************************************************
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

abstract class Sprite
{
    /*
    Store all images in this image array
    Make it static so it's only instantiated once across all sprites

    Sprite Locations:
    - Backgrounds
    0: world0 - world one

    - Mario Images
    1: mario0 - still image
    2: mario1 - run right 1
    3: mario2 - run right 2
    4: mario3 - run left 1
    5: mario4 - run left 2
    6: mario5 - jump frame

    - Block Images
    7: brick
    8: coin_block
    9: inert_coin_block

    - Coin
    10: coin

    - Koopas
    11: Green Koopa
    */
    private static final int TOT_NUM_IMAGES = (1 + 6 + 3 + 1 + 1);// total number of sprites
    public static Image[] images = null;

    // Static variables - these will help with movement of all sprites (not entities)
    static final int SCROLL_CONST = 6;// set scroll speed back to this

    /*
    Use location offset constant to calculate from which direction sprite A collides with sprite B,
    it offsets current x and y to detect sprite A's previous position.
    The condition below must stay true

    locationOffset > scrollConstant, or collision detection will break
    */
    static final int LOCATION_OFFSET = SCROLL_CONST + 1;

    static int scrollPos   = 0;// the position of everything relative to the window display
    static int scrollSpeed = 0;// (variable) base speed at which objects move

    int imageIndex = 0;// used by each child class to call the proper range in images array

    double yVelocity;// sprite's velocity going down
    int animationFrame;// frame counter used to cycle through animations
    int jumpFrames;// used to determine a sprite's jump "strength"

    public boolean isHit = false;// was this sprite collided with? May want to

    // Position of top-left corner of sprite
    int x;
    int y;

    // Width and height of sprite, used to get other three corners
    int w;
    int h;
    //=================================================================================================================
    // Initializer block: load images when Sprite is first instantiated
    //=================================================================================================================
    {
        if(images == null)
        {
            images = new Image[TOT_NUM_IMAGES];

            try
            {
                // Load backgrounds
                images[0] = loadImage("world0.png");

                // Load Mario images
                images[1] = loadImage("mario1.png");
                images[2] = loadImage("mario2.png");
                images[3] = loadImage("mario3.png");
                images[4] = loadImage("mario4.png");
                images[5] = loadImage("mario5.png");
                images[6] = loadImage("mario6.png");

                // Load blocks
                images[7] = loadImage("brick.png");
                images[8] = loadImage("coin_block.png");
                images[9] = loadImage("inert_coin_block.png");

                // Load coin
                images[10] = loadImage("coin.png");

                // Load koopas
                //images[11] = loadImage("green_koopa.png");
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    //=================================================================================================================
    // Constructors: one with x, y, w, and h, the other takes a Json object with the same fields
    //=================================================================================================================
    Sprite(int inputX, int inputY, int inputW, int inputH)
    {
        x = inputX;
        y = inputY;
        w = inputW;
        h = inputH;

        animationFrame = 0;// set default animation frame to first element
        yVelocity = 0.0;
        jumpFrames = 0;

        // Condition must remain false
        if(LOCATION_OFFSET < SCROLL_CONST)
        {
            System.out.println("WARNING: collision detection will break!");
        }
    }

    Sprite (Json j)
    {
        x = (int)j.getLong("x");
        y = (int)j.getLong("y");
        w = (int)j.getLong("w");
        h = (int)j.getLong("h");

        animationFrame = 0;// set default animation frame to first element
        yVelocity = 0.0;
        jumpFrames = 0;
    }

    //=================================================================================================================
    // Automate the image loading process
    //=================================================================================================================
    private Image loadImage(String imageName)
    {
        BufferedImage image = null;

        try
        {
            image = ImageIO.read(new File(imageName));
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            System.exit(1);
        }

        return image;
    }

    //=================================================================================================================
    // Collision detection
    //=================================================================================================================
    /*
    - Check for collisions in different scenarios:
        - stationary with moving
        - moving with stationary
        - stationary with stationary
    - Logic: if this sprite is not outside of that sprite, then this sprite must be inside of that sprite
    */
    public boolean collision(Sprite that)
    {
        // Case: this = mario, that = not mario
        if( this.isMario() && !(that.isMario()) )
        {
            return
            (
                    !(this.x + this.w <= that.x - scrollPos) &&
                    !(this.x >= that.x + that.w - scrollPos) &&
                    !(this.y + this.h <= that.y) &&
                    !(this.y >= that.y + that.h)
            );
        }
        // Case: this = not mario, that = is mario
        else if( !(this.isMario()) && that.isMario() )
        {
            return
            (
                    !(this.x + this.w - scrollPos <= that.x) &&
                    !(this.x - scrollPos >= that.x + that.w) &&
                    !(this.y + this.h <= that.y) &&
                    !(this.y >= that.y + that.h)
            );
        }
        // Case: this = not mario, that = not mario
        else if(!(this.isMario()) && !(that.isMario()))
        {
            return
            (
                    !(this.x + this.w - scrollPos <= that.x - scrollPos) &&
                    !(this.x - scrollPos >= that.x + that.w - scrollPos) &&
                    !(this.y + this.h <= that.y) &&
                    !(this.y >= that.y + that.h)
            );
        }
        else
        {
            return false;
        }
    }

    //=================================================================================================================
    // Sprite gets out of sprite
    // Note: may need to adjust with other sprite types that are not the player.
    //=================================================================================================================
    void getOutOfSprite(Sprite that)
    {
        //-------------------------------------------------------------------------------------------------------------
        // Given sprite A (this) and sprite B (that)
        // Condition 1: check if right-side of sprite A overlaps with left side of sprite B
        // Condition 2: confirm sprite A was coming from the left by offsetting current position left

        // Left side: *same logic applies to other sides
        if
        (
                (this.x + this.w >= that.x - scrollPos) &&
                (this.x + this.w - LOCATION_OFFSET < that.x - scrollPos)
        )
        {
            scrollSpeed = 0;// stop player sprite

            // position = difference between overlapping entities
            scrollPos = that.x - (this.x + this.w);
        }
        //-------------------------------------------------------------------------------------------------------------
        // Right side
        else if
        (
                (this.x <= that.x + that.w - scrollPos) &&
                (this.x + LOCATION_OFFSET > that.x + that.w - scrollPos)
        )
        {
            scrollSpeed = 0;
            scrollPos = that.x + that.w - this.x + 1;// adding 1 to re-positioning will make do
        }
        //-------------------------------------------------------------------------------------------------------------
        // Bottom
        else if
        (
                (this.y <= that.y + that.h) &&
                (this.y + this.h > that.y + that.h)
        )
        {
            this.yVelocity = 0.0;// reset downward velocity for sprite A
            this.y = that.y + that.h + 1;// reposition sprite A
        }
        //-------------------------------------------------------------------------------------------------------------
        // Top
        else if
        (
                (this.y + this.h >= that.y) &&
                (this.y - LOCATION_OFFSET < that.y)
        )
        {
            this.yVelocity = 0.0;
            this.jumpFrames = 0;
            this.y = that.y - this.h;
        }
    }

    //=================================================================================================================
    // Marshalling method: take all objects in model and put them into Json objects, write to a file
    Json spriteMarshall()
    {
        Json ob = Json.newObject();

        if(isBackground())      { ob.add("type", "background"); }
        else if(isMario())      { ob.add("type", "mario"); }
        else if(isBrick())      { ob.add("type", "brick"); }
        else if(isCoinBlock())  { ob.add("type", "coin_block"); }
        else if(isGKoopa())     { ob.add("type", "green_koopa"); }
        else                    { throw new RuntimeException("Sprite type does not exist"); }

        ob.add("x", x);
        ob.add("y", y);
        ob.add("w", w);
        ob.add("h", h);

        return ob;
    }

    //=================================================================================================================
    // Check type of sprite
    //=================================================================================================================
    public boolean isBackground() { return false; }
    public boolean isMario()      { return false; }
    public boolean isBrick()      { return false; }
    public boolean isCoinBlock()  { return false; }
    public boolean isCoin()       { return false; }
    public boolean isGKoopa()     { return false; }

    //=================================================================================================================
    // Abstract methods
    //=================================================================================================================
    abstract void update();// update the sprite
    abstract void draw(Graphics g);// draw the sprite that was updated
}
