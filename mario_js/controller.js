//=====================================================================================================================
// Objectives:

// TODO: (later) be able to load and save from/to a JSON file

// TODO: complete assignment by adding meaningful comments

//=====================================================================================================================
function Controller(model, view)
{
    this.model = model;
    this.view = view;

    this.key_right = false;
    this.key_left = false;
    this.key_space = false;

    let x1 = 0;
    let x2 = 0;
    let y1 = 0;
    let y2 = 0;

    let self = this;

    // Mouse Listeners
    view.canvas.addEventListener("click", function(event)
    {
        //self.onClick(event, view.canvas);
    });

    view.canvas.addEventListener("mousedown",function(event)
    {
        // console.log("Start coordinates");
        // console.log("X: " + event.pageX + " " + "Y: " + event.pageY);

        x1 = event.pageX - view.canvas.offsetLeft;// + masterScrollPos;
        y1 = event.pageY - view.canvas.offsetTop;
    });

    view.canvas.addEventListener("mouseup", function(event)
    {
        // console.log("End Coordinates");
        // console.log("X: " + event.pageX + " " + "Y: " + event.pageY);

        x2 = event.pageX - view.canvas.offsetLeft;// + masterScrollPos;
        y2 = event.pageY - view.canvas.offsetTop;

        // Get corners
        let left = Math.min(x1, x2);
        let right = Math.max(x1, x2);
        let top = Math.min(y1, y2);
        let bottom = Math.max(y1, y2);

        // Place a decently-sized brick on the screen
        if((right - left > 20) && (bottom - top > 20))
        {
            model.placeBrick(left + masterScrollPos, top, right - left, bottom - top);
        }
        else
        {
            model.placeCoinBlock(x1 + masterScrollPos, y1);
        }
    });

    // Key Listeners
    document.addEventListener('keydown', function(event) { self.keyDown(event); }, false);
    document.addEventListener('keyup', function(event) { self.keyUp(event); }, false);
}

// Controller.prototype.onClick = function(event, canvas)
// {
//     // Drag the mouse to draw a brick
//     let startX = event.pageX - canvas.offsetLeft + masterScrollPos;
//     let startY = event.pageY - canvas.offsetTop;
//
//     this.model.placeCoinBlock(startX, startY);
// };

//=====================================================================================================================
// A key is pressed
//=====================================================================================================================
Controller.prototype.keyDown = function(event)
{
    if(event.keyCode === 39) this.key_right = true;
    if(event.keyCode === 37) this.key_left  = true;
    if(event.keyCode === 32) this.key_space = true;

    masterScrollSpeed = MASTER_SCROLL_SPEED;
};

//=====================================================================================================================
// A key is released
//=====================================================================================================================
Controller.prototype.keyUp = function(event)
{
    if(event.keyCode === 39)
    {
        this.key_right = false;
        goingRight = false;
    }
    if(event.keyCode === 37)
    {
        this.key_left  = false;
        goingLeft = false;
    }
    if(event.keyCode === 32)
    {
        this.key_space = false;
    }
};

//=====================================================================================================================
// Update Controller
//=====================================================================================================================
Controller.prototype.update = function()
{
    if(this.key_right)
    {
        masterScrollPos += masterScrollSpeed;
        goingRight = true;
    }
    if(this.key_left)
    {
        masterScrollPos -= masterScrollSpeed;
        goingLeft = true;
    }
    if(this.key_space)
    {
        // Count number of frames for mario, if it is below a certain num, keep subtracting yVelocity
        if(mario.jumpFrames < 4)
        {
            mario.yVelocity -= 10.1;
        }
    }
};
