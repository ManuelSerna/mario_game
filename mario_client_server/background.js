// Screen width and height (of the game, NOT the entire web page)
let screenWidth = 2500;
let screenHeight = 500;

let screenStartPos = -200;

//=====================================================================================================================
// Background "class"
//=====================================================================================================================
class Background extends Sprite
{
    constructor(inputX, inputY)
    {
        super(inputX, inputY, screenWidth, screenHeight);

        this.image1 = new Image();
        this.image1.src = "background.png";

        this.type = "background";
    }

    //=================================================================================================================
    // Update background mechanics
    //=================================================================================================================
    update()
    {
        this.scrollPos = masterScrollPos;
    }

    //=================================================================================================================
    // Draw the background
    //=================================================================================================================
    draw(context)
    {
        context.drawImage(this.image1, this.xPos - masterScrollPos, this.yPos, this.width, this.height);
    }
}