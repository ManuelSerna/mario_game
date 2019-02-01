class Coin extends Sprite
{
    constructor(inputX, inputY, index)
    {
        super(inputX, inputY, 20, 31);

        // Vertical velocity components
        this.yVelocity = 0.0;
        this.down_accel = 2.0;
        this.maxYVelocity = 6.5;
        this.minYVelocity = 3.2;

        // Horizontal velocity components
        this.maxXVelocity = 2.5;
        this.minXVelocity = 1.7;
        this.xVelocity = 0.0;

        this.jumpFrames = 5;

        this.image1 = new Image();
        this.image1.src = "coin.png";

        this.type = "coin";

        if(index % 2 === 0)
        {
            this.xVelocity = -this.randomizeCoinVelocity(this.minXVelocity, this.maxXVelocity);// coin launches left
        }
        else
        {
            this.xVelocity = this.randomizeCoinVelocity(this.minXVelocity, this.maxXVelocity);// coin launches right
        }
    }

    update()
    {
        this.scrollPos = masterScrollPos;

        // Add constant x velocity
        this.xPos += this.xVelocity;

        // Gravity mechanic, start with random y velocity
        if(this.jumpFrames > 0)
        {
            this.yVelocity -= this.randomizeCoinVelocity(this.minYVelocity, this.maxYVelocity);
        }

        this.yVelocity += this.down_accel;
        this.yPos += this.yVelocity;

        this.jumpFrames--;
    }

    draw(context)
    {
        context.drawImage(this.image1, this.xPos - masterScrollPos, this.yPos, this.width, this.height);
    }

    randomizeCoinVelocity(min, max)
    {
        return (Math.random() * ((max - min) + 1) + min);
    }
}
