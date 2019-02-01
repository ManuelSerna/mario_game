package manuel.mariogame.app;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Mario extends Sprite
{
    Model model;

    static final int JUMP_FRAMES = 5;

    boolean jump;
    private float yVelocity;
    int jumpFrames;

    // Flags for whichever direction Mario moves in
    boolean isGoingLeft;
    int leftCounter;
    boolean isGoingRight;
    int rightCounter;

    Mario(float startX, float startY, Model inputModel)
    {
        super(startX, startY);
        model = inputModel;

        yVelocity = -32.0f;
        jump = false;
        jumpFrames = 0;

        isGoingLeft = false;
        leftCounter = 0;
        isGoingRight = false;
        rightCounter = 0;
    }

    @Override
    void update(int masterScrollPos)
    {
        // Update scroll position
        spritePos = masterScrollPos;

        // Update gravity
        yVelocity += 1.8f;
        yPos += yVelocity;

        // Jump up
        if(jump && jumpFrames < JUMP_FRAMES)
        {
                yVelocity -= 9.81f;
        }
        if(jumpFrames >= JUMP_FRAMES)
        {
            jump = false;
        }

        // Stand on the floor
        if(yPos > FLOOR)
        {
            yPos = FLOOR;
            jumpFrames = 0;
        }

        // Keep increasing jump frames if not on any ground
        jumpFrames++;

        //For when collision detection and get-out methods are implemented
        //model.setMasterScrollPos(spritePos);
    }

    @Override
    void drawSprite(Canvas canvas, Paint paint)
    {
    }
}
