package manuel.mariogame.app;

import android.graphics.Canvas;
import android.graphics.Paint;

abstract public class Sprite
{
    // The floor, Mario will stand at this y
    final float FLOOR = 615.0f;

    // Sprite position, controlled by master scrolling position
    int spritePos;

    float xPos;
    float yPos;

    int jumpFrames;

    Sprite()
    {
    }

    Sprite(float startX, float startY)
    {
        spritePos = 0;
        xPos = startX;
        yPos = startY;
        jumpFrames = 0;
    }

    // Update mechanics of a certain sprite
    abstract void update(int masterScrollPos);

    // draw a certain sprite using the canvas
    abstract void drawSprite(Canvas canvas, Paint paint);
}
