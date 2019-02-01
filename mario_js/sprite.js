/*
A note for classes in JS ES6

Class syntax for ES6 is a lot cleaner and similar to C-type languages.
Nevertheless, it's just another way of writing: ClassName.prototype.methodName = function() {}
*/

//=====================================================================================================================
// Sprite "class": hold basic information and methods for sprites in this game
//=====================================================================================================================
class Sprite
{
    constructor(inputX, inputY, inputW, inputH)
    {
        // Positions and dimensions
        this.xPos = inputX;
        this.yPos = inputY;
        this.width = inputW;
        this.height = inputH;

        // Scrolling position for this sprite, adjust when handling collisions and sprite overlapping
        this.scrollPos = 0;
        this.scrollSpeed = 0;

        // Sprite "type", can be Mario, Brick, Koopa, etc.
        this.type = "";
    }

    //=================================================================================================================
    // Detect collision between this sprite and another sprite
    // Logic: if this sprite is not outside of that sprite, then this sprite must be inside of that sprite
    //=================================================================================================================
    collision(that)
    {
        return(
            !(this.xPos + this.width <= that.xPos - this.scrollPos) &&
            !(this.xPos >= that.xPos + that.width - this.scrollPos) &&
            !(this.yPos + this.height <= that.yPos) &&
            !(this.yPos >= that.yPos + that.height)
        )
    }
}
