//=====================================================================================================================
// Coin Block "class"
//=====================================================================================================================
class CoinBlock extends Sprite
{
    constructor(startX, startY, inputModel)
    {
        super(startX, startY, 64, 64);

        this.model = inputModel;

        // Max number of coins this block can hold
        this.coinLimit = 5;

        // Has a distinct coin block instance been hit?
        this.isHit = false;

        this.targetImage = new Image();

        this.image1 = new Image();
        this.image1.src = "coin_block.png";
        this.targetImage = this.image1;

        this.image2 = new Image();
        this.image2.src = "empty_coin_block.png";

        this.type = "coin_block";
    }

    //=================================================================================================================
    // Update coin block position
    //=================================================================================================================
    update()
    {
        this.scrollPos = masterScrollPos;

        for(let i = 0; i < this.model.sprites.length; i++)
        {
            let s = this.model.sprites[i];

            if(
                (s.type === "mario") &&
                (this.yPos + this.height <= s.yPos) &&
                (this.yPos + this.height + 2 > s.yPos) &&
                (this.isHit === true)
            )
            {
                // Add a coin to the sprites list
                if(this.coinLimit > 0)
                {
                    this.coin = new Coin(this.xPos + this.width/4, this.yPos + this.height/4, this.model.sprites.length);
                    this.model.sprites.push(this.coin);
                    this.coinLimit--;
                }
            }

            // Replace image to indicate that no more coins will pop out
            if(this.coinLimit === 0)
            {
                this.targetImage = this.image2;
            }
        }

        // Remove coins from array list when they get out of bounds. Go down the list since the size will most likely decrease with deletions.
        for(let i = this.model.sprites.length; i > 0; i--)
        {
            // Get i-1 element, otherwise you will be out of bounds when starting
            let s = this.model.sprites[i-1];

            if((s.type === "coin") && (s.yPos > screenHeight))
            {
                 this.model.sprites.splice(i - 1, 1);
            }
        }
    }

    //=================================================================================================================
    // Draw the coin block
    //=================================================================================================================
    draw(context)
    {
        context.drawImage(this.targetImage, this.xPos - masterScrollPos, this.yPos, this.width, this.height);
    }
}