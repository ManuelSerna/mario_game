//=====================================================================================================================
// Controller "class"
//=====================================================================================================================
class Controller
{
    constructor(model, view)
    {
        this.model = model;
        this.view = view;

        this.key_right = false;
        this.key_left = false;
        this.key_space = false;

        // Coordinates for placing items in-game
        let x1 = 0;
        let x2 = 0;
        let y1 = 0;
        let y2 = 0;

        let self = this;

        // Mouse Listeners
        view.canvas.addEventListener("click", function(event) {});

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

    //=================================================================================================================
    // Key is pressed
    //=================================================================================================================
    keyDown(event)
    {
        if((event.keyCode === 39) )
        {
            this.key_right = true;
        }
        if((event.keyCode === 37) )
        {
            this.key_left  = true;
        }
        if((event.keyCode === 32) )
        {
            this.key_space = true;
        }

        masterScrollSpeed = MASTER_SCROLL_SPEED;
    }

    //=================================================================================================================
    // Key is released
    //=================================================================================================================
    keyUp(event)
    {
        if(event.keyCode === 39)
        {
            this.key_right = false;
            mario.goingRight = false;
        }
        if(event.keyCode === 37)
        {
            this.key_left  = false;
            mario.goingLeft = false;
        }
        if(event.keyCode === 32)
        {
            this.key_space = false;
        }
    }

    //=================================================================================================================
    // Contact server: prepare data necessary to send to server
    //=================================================================================================================
    contactServer()
    {
        // Make a JSON object
        let ob = {};
        ob.ID = myID;// send ID to initialize and act as an identifier
        ob.xPos = mario.xPos;
        ob.yPos = mario.yPos;
        ob.masterScrollPos = masterScrollPos;

        // Send JSON object as a string (can only send as string)
        let json_string = JSON.stringify(ob);

        let url = "placeholder.html";// let url be a dummy string (to satisfy http parameters)

        // Send the JSON blob to the server
        httpPost(url, json_string, cb);
    }

    //=================================================================================================================
    // Update controller
    //=================================================================================================================
    update()
    {
        if(this.key_right)
        {
            masterScrollPos += masterScrollSpeed;
            mario.goingRight = true;
        }
        if(this.key_left)
        {
            masterScrollPos -= masterScrollSpeed;
            mario.goingLeft = true;
        }
        if(this.key_space)
        {
            // Count number of frames for mario, if it is below a certain num, keep subtracting yVelocity
            if(mario.playerID === myID)// TODO: fix vertical relocation here?
            {
                if (mario.jumpFrames < 4)
                {
                    mario.yVelocity -= 10.1;
                }
            }
        }

        // Keep a counter to reduce the number of times we contact the server
        // if(this.frameCounter === undefined)
        // {
        //     this.frameCounter = 0;
        // }
        //
        // this.frameCounter++;
        //
        // // 1000
        // if(this.frameCounter > 1)
        // {
        //    this.frameCounter = 0;
        //
        //     //contact server less often that what onTimer does
        //
        //     this.contactServer();
        // }

        this.contactServer();
    }
}