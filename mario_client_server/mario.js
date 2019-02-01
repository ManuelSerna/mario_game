// Mario's position on the floor
let playerFloor = screenHeight - 63 - 95;// total height - floor - mario's height

// Downward acceleration
let downAcceleration = 1.9;//2.2;

//=====================================================================================================================
// Mario "class"
//=====================================================================================================================
class Mario extends Sprite
{
    constructor(inputX, inputY, inputModel)
    {
        super(inputX, inputY, 60, 95);

        // Be able to access model to check for collisions
        this.model = inputModel;

        this.yVelocity = 0.0;// vertical velocity
        this.jumpFrames = 0;// number of frames in the air, used to determine if Mario can jump

        // Direction flags and frame counters
        this.goingRight = false;
        this.rightCounter = 0;
        this.goingLeft = false;
        this.leftCounter = 0;

        // Offset location to see which way the player sprite moved
        this.locationOffset = MASTER_SCROLL_SPEED + 1;
        
        // Target image that draw frames
        this.targetImage = new Image();

        let src1;
        let src2;
        let src3;
        let src4;
        let src5;

        // Define the type of this sprite
        this.type = "mario";
        this.playerID = -1;// assign playerID to allow control of proper sprite (not another client's)
    }

    //=================================================================================================================
    // Update mechanics: gravity and collisions
    //=================================================================================================================
    update()
    {
        this.scrollPos = masterScrollPos;

        // Update gravity
        if(this.playerID === myID)
        {
            this.yVelocity += downAcceleration;
            this.yPos += this.yVelocity;
        }

        // If Mario hits the ground, keep him from falling on the floor
        if (this.yPos >= playerFloor)
        {
            this.jumpFrames = 0;
            this.yVelocity = 0.0;
            this.yPos = playerFloor;
        }

        this.jumpFrames++;

        // Handle collisions
        for(let i = 0; i < this.model.sprites.length; i++)
        {
            let s = this.model.sprites[i];

            // Check for collision with anything not itself and the background
            if (this.collision(s) && (s.type !== "background"))
            {
                if (s.type === "coin_block")
                {
                    s.isHit = true;
                }

                // Only handle getting out of sprite for blocks
                if(s.type === "brick" || s.type === "coin_block" || (s.type === "mario" && s !== this))
                {
                    this.getOut(s);
                }
            }
            else
            {
                masterScrollSpeed = MASTER_SCROLL_SPEED;

                if (s.type === "coin_block")
                {
                    s.isHit = false;
                }
            }
        }

        // Adjust the master scrolling position
        if (this.playerID === myID)
        {
            masterScrollPos = this.scrollPos;
        }
    }

    //=================================================================================================================
    // Draw the Mario sprite, change frames according to user input
    //=================================================================================================================
    draw(context)
    {
        if(this.goingRight)
        {
            if(this.rightCounter % 9 === 0)
            {
                this.targetImage.src = this.src2;
            }
            else if(this.rightCounter % 9 === 4)
            {
                this.targetImage.src = this.src3;
            }

            this.rightCounter++;
        }
        else if(this.goingLeft)
        {
            if(this.leftCounter % 9 === 0)
            {
                this.targetImage.src = this.src4;
            }
            else if (this.leftCounter % 9 === 4)
            {
                this.targetImage.src = this.src5;
            }

            this.leftCounter++;
        }
        else
        {
            this.rightCounter = 0;
            this.leftCounter = 0;

            this.targetImage.src = this.src1;
        }


        if(this.playerID === myID)
        {
            context.drawImage(this.targetImage, this.xPos, this.yPos, this.width, this.height);
        }
        // Make other client's sprite stationary
        else
        {
            context.drawImage(this.targetImage, this.xPos - masterScrollPos, this.yPos, this.width, this.height);
        }
    }

    //=================================================================================================================
    // If the player collides with an object in-game, make sure player sprite does not go through said object
    //=================================================================================================================
    getOut(that)
    {
        // Given this sprite and "that" sprite
        // Condition 1: check if right-side of this sprite overlaps with left side of that sprite
        // Condition 2: confirm that this sprite was coming from the left by offsetting current position left

        // LEFT side of that sprite
        if(
            (this.xPos + this.width >= that.xPos - this.scrollPos) &&
            (this.xPos + this.width - this.locationOffset < that.xPos - this.scrollPos)
        )
        {
            if(this.playerID === myID)
                masterScrollSpeed = 0;

            // Position = difference between overlapping entities
            this.scrollPos = that.xPos - (this.xPos + this.width);

            if(this.playerID === myID)
                masterScrollPos = this.scrollPos;
        }

        // RIGHT side of that
        else if(
            (this.xPos <= that.xPos + that.width - masterScrollPos) &&
            (this.xPos + this.locationOffset > that.xPos + that.width - masterScrollPos)
        )
        {
            if(this.playerID === myID)
                masterScrollSpeed = 0;

            this.scrollPos = that.xPos + that.width - this.xPos;// adding to to re-position will do

            if(this.playerID === myID)
                masterScrollPos = this.scrollPos;
        }

        // BOTTOM of that
        else if(
            (this.yPos <= that.yPos + that.height) &&
            (this.yPos + this.height > that.yPos + that.height)
        )
        {
            this.yVelocity = 0.0;// reset down velocity for this
            this.yPos = that.yPos + that.height + 1;// reposition this sprite
        }

        // TOP of that
        else if(
            (this.yPos + this.height >= that.yPos) &&
            (this.yPos - this.locationOffset < that.yPos)
        )
        {
            this.yVelocity = 0.0;
            this.jumpFrames = 0;
            this.yPos = that.yPos - this.height;
        }
    }
}
