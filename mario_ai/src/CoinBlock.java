import java.awt.*;

public class CoinBlock extends Sprite
{
    //private ArrayList<Sprite> modelSprites;// get model sprite list to check for collisions

    private Model model;// reference model that contains the coin block that instantiated this coin  object
    int coinLimit;// how many coins instances of coin blocks may have

    //=================================================================================================================
    // Initializer block: set image array index
    //=================================================================================================================
    {
        imageIndex = 8;
    }

    //=================================================================================================================
    // Coin Block coordinate and JSON constructors
    //=================================================================================================================
    CoinBlock(int inputX, int inputY, Model inputModel)
    {
        super(inputX, inputY, 64, 64);
        model = inputModel;
        coinLimit = 5;
    }

    CoinBlock(Json ob, Model inputModel)
    {
        super(ob);
        model = inputModel;
        coinLimit = 5;
    }

    CoinBlock(CoinBlock inputCb, Model inputModel)
    {
        super(inputCb);
        model = inputModel;
        coinLimit = inputCb.coinLimit;
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
    public void update(int masterScrollPos)
    {
        // Update this sprite's scroll position
        this.scrollPos = masterScrollPos;

        // Check if a Mario sprite collides from the bottom, make a coin fly out of the block when it's hit
        for(int i = 0; i < model.sprites.size(); i++)
        {
            //Sprite s = modelSprites.get(i);
            Sprite s = model.sprites.get(i);

            if(s.isMario() && (this.y + this.h <= s.y) && (this.y + this.h + 2 > s.y) && this.isHit)
            {
                // Add a coin to the sprites list
                if(coinLimit > 0)
                {
                    Coin coin = new Coin(this.x + this.w / 4, this.y + this.h / 4, model.sprites.size());
                    model.sprites.add(coin);
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
        for(int i = model.sprites.size(); i > 0; i--)
        {
            Sprite s = model.sprites.get(i-1);

            if(s.isCoin() && s.y > 1000)
            {
                model.sprites.remove(i-1);
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
