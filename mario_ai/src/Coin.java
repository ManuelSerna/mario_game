import java.awt.*;

public class Coin extends Sprite
{
    // Vertical velocity components
    private final double DOWN_ACCEL = 2.0;
    private double maxYVelocity = 6.5;
    private double minYVelocity = 3.2;

    // Horizontal velocity components
    private double maxXVelocity = 2.5;
    private double minXVelocity = 1.7;
    private double xVelocity = 0;

    //=================================================================================================================
    // Initializer block: set image array index and strength of coin's jump
    //=================================================================================================================
    {
        imageIndex = 10;
        jumpFrames = 5;
    }

    //=================================================================================================================
    // Constructor: load coordinates and launch coin in a random direction
    //=================================================================================================================
    Coin(int inputX, int inputY, int index)
    {
        super(inputX, inputY, 20, 31);

        if(index % 2 == 0)
        {
            xVelocity = -randomizeCoinVelocity(minXVelocity, maxXVelocity);// coin launches left
        }
        else
        {
            xVelocity = randomizeCoinVelocity(minXVelocity, maxXVelocity);// coin launches right
        }
    }

    Coin(Coin inputCn, int index)
    {
        super(inputCn);

        if(index % 2 == 0)
        {
            xVelocity = -randomizeCoinVelocity(minXVelocity, maxXVelocity);// coin launches left
        }
        else
        {
            xVelocity = randomizeCoinVelocity(minXVelocity, maxXVelocity);// coin launches right
        }
    }

    //=================================================================================================================
    // Check sprite type
    //=================================================================================================================
    @Override
    public boolean isCoin()
    {
        return true;
    }

    //=================================================================================================================
    // Update: coin mechanics
    //=================================================================================================================
    @Override
    public void update(int masterScrollPos)
    {
        // Update this sprite's scroll position
        this.scrollPos = masterScrollPos;

        // Add constant x velocity
        this.x += xVelocity;

        // Gravity
        if(jumpFrames > 0)
        {
            this.yVelocity -= randomizeCoinVelocity(minYVelocity, maxYVelocity);
        }

        this.yVelocity += DOWN_ACCEL;
        this.y += yVelocity;

        jumpFrames--;
    }

    //=================================================================================================================
    // Draw coin
    //=================================================================================================================
    @Override
    void draw(Graphics g)
    {
        g.drawImage(images[imageIndex], this.x - scrollPos, y, w, h, null);
    }

    //=================================================================================================================
    // Random number generator with a max and min
    //=================================================================================================================
    private double randomizeCoinVelocity(double min, double max)
    {
        return (Math.random() * ((max - min) + 1) + min);
    }
}
