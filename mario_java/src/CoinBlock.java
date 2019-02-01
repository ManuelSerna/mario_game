import java.awt.*;
import java.util.ArrayList;

public class CoinBlock extends Sprite
{
    private ArrayList<Sprite> modelSprites;// get model sprite list to check for collisions
    private int coinLimit = 0;// how many coins instances of coin blocks may have

    //=================================================================================================================
    // Initializer block: set image array index
    //=================================================================================================================
    {
        imageIndex = 8;
    }

    //=================================================================================================================
    // Coin Block coordinate and JSON constructors
    //=================================================================================================================
    CoinBlock(int inputX, int inputY, ArrayList<Sprite> inputList)
    {
        super(inputX, inputY, 64, 64);
        this.modelSprites = inputList;
        coinLimit = 5;
    }

    CoinBlock(Json ob, ArrayList<Sprite> inputList)
    {
        super(ob);
        this.modelSprites = inputList;
        coinLimit = 5;
    }

    //=================================================================================================================
    // Check sprite type
    //=================================================================================================================
    @Override
    public boolean isCoinBlock()
    {
        return true;
    }

    //=================================================================================================================
    // Update: check for collisions, launch coins when hit from the bottom
    //=================================================================================================================
    @Override
    void update()
    {
        // Check if a Mario sprite collides from the bottom, make a coin fly out of the block when it's hit
        for(int i = 0; i < modelSprites.size(); i++)
        {
            Sprite s = modelSprites.get(i);

            if(s.isMario() && (this.y + this.h <= s.y) && (this.y + this.h + 2 > s.y) && this.isHit)
            {
                // Add a coin to the sprites list
                if(coinLimit > 0)
                {
                    Coin coin = new Coin(this.x + this.w / 4, this.y + this.h / 4, modelSprites.size());
                    modelSprites.add(coin);
                    coinLimit--;
                }
            }

            // Replace image to indicate that no more coins will pop out
            if(coinLimit == 0)
            {
                imageIndex = 9;
            }
        }

        // Remove coins from array list when they get out of bounds. Go down the list since the size will
        // most likely decrease with deletions.
        for(int i = modelSprites.size(); i > 0; i--)
        {
            Sprite s = modelSprites.get(i-1);

            if(s.isCoin() && s.y > 1000)
            {
                modelSprites.remove(i-1);
            }
        }
    }

    //=================================================================================================================
    // Update image: full coin block if there's still coins, change image if no more coins will pop out
    //=================================================================================================================
    @Override
    void draw(Graphics g)
    {
        g.drawImage(images[imageIndex], this.x - scrollPos, y, w, h, null);
    }
}
