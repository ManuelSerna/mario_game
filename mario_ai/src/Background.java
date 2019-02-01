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
    Background(Background inputBg) { super(inputBg); }

    @Override
    public boolean isBackground()
    {
        return true;
    }

    @Override
    public void update(int masterScrollPos)
    {
        // Update this sprite's scroll position
        this.scrollPos = masterScrollPos;
    }

    @Override
    void draw(Graphics g)
    {
        if(isBackground())
        {
            g.drawImage(images[0], this.x - this.scrollPos / 2, this.y, null);
        }
    }
}
