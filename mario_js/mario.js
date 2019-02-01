// Mario's position on the floor
let playerFloor = screenHeight - 63 - 95;// total height - floor - mario's height

// Downward acceleration
let downAcceleration = 2.2;

// Direction flags for Mario
let goingRight = false;
let rightCounter = 0;

let goingLeft = false;
let leftCounter = 0;

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

        // Offset location to see which way the player sprite moved
        this.locationOffset = MASTER_SCROLL_SPEED + 1;

        /*
        To optimize image loading:
        
        Create an array if images that holds each  in the constructor
        
        this.image = new Image();
        this.image.src = imageFileName;
        this.images = [];
        
        this.images.push(new Image());// do this repeatedly
        
        ...
        
        In draw iterate through the image array
        
        (counter)%numFrames
        */
        
        // Image assets
        this.targetImage = new Image();

        this.image1 = new Image();
        this.image1.src = "mario1.png";// still

        this.image2 = new Image();
        this.image2.src = "mario2.png";// right frame 1
        this.image3 = new Image();
        this.image3.src = "mario3.png";// right frame 2

        this.image4 = new Image();
        this.image4.src = "mario4.png";// left frame 1
        this.image5 = new Image();
        this.image5.src = "mario5.png";// left frame 2

        // Define the type of this sprite
        this.type = "mario";
    }

    //=================================================================================================================
    // Update mechanics: gravity and collisions
    //=================================================================================================================
    update()
    {
        this.scrollPos = masterScrollPos;

        // Update gravity
        this.yVelocity += downAcceleration;
        this.yPos += this.yVelocity;

        // If Mario hits the ground, keep him from falling on the floor
        if(this.yPos >= playerFloor)
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
            if(this.collision(s) && (s.type !== "mario") && (s.type !== "background"))
            {
                if(s.type === "coin_block")
                {
                    s.isHit = true;
                }

                // Only handle getting out of sprite for blocks
                if(s.type === "brick" || s.type === "coin_block")
                {
                    this.getOut(s);
                }
            }
            else
            {
                masterScrollSpeed = MASTER_SCROLL_SPEED;

                if(s.type === "coin_block")
                {
                    s.isHit = false;
                }
            }
        }

        // Adjust the master scrolling position
        masterScrollPos = this.scrollPos;
    }

    //=================================================================================================================
    // Draw the Mario sprite, change frames according to user input
    //=================================================================================================================
    draw(context)
    {
        if(goingRight)
        {
            if(rightCounter % 9 === 0)
            {
                this.targetImage = this.image2;
            }
            else if(rightCounter % 9 === 4)
            {
                this.targetImage = this.image3;
            }

            context.drawImage(this.targetImage, this.xPos, this.yPos);
            rightCounter++;
        }
        else if (goingLeft)
        {
            if(leftCounter % 9 === 0)
            {
                this.targetImage = this.image4;
            }
            else if (leftCounter % 9 === 4)
            {
                this.targetImage = this.image5;
            }

            context.drawImage(this.targetImage, this.xPos, this.yPos);
            leftCounter++;
        }
        else
        {
            rightCounter = 0;
            leftCounter = 0;
            context.drawImage(this.image1, this.xPos, this.yPos);
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
            masterScrollSpeed = 0;

            // Position = difference between overlapping entities
            this.scrollPos = that.xPos - (this.xPos + this.width);

            masterScrollPos = this.scrollPos;
        }

        // RIGHT side of that
        else if(
            (this.xPos <= that.xPos + that.width - masterScrollPos) &&
            (this.xPos + this.locationOffset > that.xPos + that.width - masterScrollPos)
        )
        {
            masterScrollSpeed = 0;
            this.scrollPos = that.xPos + that.width - this.xPos;// adding to to re-position will do
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
