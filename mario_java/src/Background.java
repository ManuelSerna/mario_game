//**********************************************************************************************************************
// Background
//**********************************************************************************************************************
import java.awt.*;

public class Background extends Sprite
{
    /*
    x offset: somewhere behind model's x = 0, so you can move left a bit
    y offset: 0
    background sprite width: (width of .png)
    background sprite height: (height of .png)
    */
    Background() { super(-250, 0, 2500, 650); }
    Background(Json j) { super(j); }

    @Override
    public boolean isBackground()
    {
        return true;
    }

    @Override
    void update() { }

    @Override
    void draw(Graphics g)
    {
        if(isBackground())
        {
            g.drawImage(images[0], this.x - Sprite.scrollPos / 2, this.y, null);
        }
    }
}
