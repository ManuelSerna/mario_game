package manuel.mariogame.app;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Brick extends Sprite
{
    Brick(float startX, float startY)
    {
        super(startX, startY);
    }

    @Override
    void update(int masterScrollPos)
    {
        // Update scroll position
        spritePos = masterScrollPos;
    }

    @Override
    void drawSprite(Canvas canvas, Paint paint)
    {

    }
}
