package manuel.mariogame.app;

/*
 It should have a Mario that can run and jump. It should also have bricks that scroll by.
 However collision detection is not required for this assignment, so don't worry about
 interactions with bricks. You do not need to support map editing or variable jumping heights.
*/

import android.content.res.Resources;
import java.util.ArrayList;

class Model
{
    // Hold every sprite in a collection
    ArrayList<Sprite> sprites;

    // Redundant Mario reference (makes referencing variables much easier)
    Mario mario;

    Brick b1;
    Brick b2;
    Brick b3;

    final static private int MASTER_SCROLL_SPEED = 6;

    private int masterScrollPos;
    private int masterScrollSpeed;

    Model()
    {
        // Instantiate the sprite list
        sprites = new ArrayList<>();

        // Set initial position of model
        masterScrollPos = 0;
        masterScrollSpeed = MASTER_SCROLL_SPEED;

        //Create instances of sprites
        mario = new Mario(300.0f, 300.0f, this);

        b1 = new Brick(500.0f, 50.f);
        b2 = new Brick(700.0f, 450.f);
        b3 = new Brick(200.0f, 99.f);

        // Add instances to list
        sprites.add(mario);
        sprites.add(b1);
        sprites.add(b2);
        sprites.add(b3);
    }

    //==============================================================================================
    // Setters/Adders: add to the existing value of master scrolling values
    //==============================================================================================
    void setMasterScrollPos(int input)
    {
        masterScrollPos = input;
    }

    void setMasterScrollSpeed(int input)
    {
        masterScrollSpeed = input;
    }

    //==============================================================================================
    // Getters
    //==============================================================================================
    int getMasterScrollPos()
    {
        return masterScrollPos;
    }

    int getMasterScrollSpeed()
    {
        return masterScrollSpeed;
    }

    public static int getScreenWidth()
    {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight()
    {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    //==============================================================================================
    // Update sprites, regardless of type
    //==============================================================================================
    void update()
    {
        for(int i = 0; i < sprites.size(); i++)
        {
            Sprite s = sprites.get(i);
            s.update(masterScrollPos);
        }
    }
}