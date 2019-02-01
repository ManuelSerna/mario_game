//=====================================================================================================================
// Model: put sprites in an array
//=====================================================================================================================
const MASTER_SCROLL_SPEED = 6;

let masterScrollPos = 0;
let masterScrollSpeed = MASTER_SCROLL_SPEED;

// Player characters
let mario;
let otherPlayer;// will be initialized when getting data from server for the first time

class Model
{
    constructor()
    {
        // Make an array called sprites, meant to hold all sprites
        this.sprites = [];

        // Object map contains a simple map
        let map = {
            "sprites":
                [
                    {"type":"background","x":-250,"y":0,"w":2500,"h":650},
                    {"type":"mario","x":500,"y":342,"w":60,"h":95},
                    {"type":"brick","x":583,"y":365,"w":68,"h":70},
                    {"type":"brick","x":649,"y":288,"w":68,"h":86},
                    {"type":"brick","x":718,"y":210,"w":59,"h":91},
                    {"type":"brick","x":650,"y":374,"w":71,"h":57},
                    {"type":"brick","x":714,"y":284,"w":70,"h":89},
                    {"type":"brick","x":718,"y":374,"w":69,"h":59},
                    {"type":"brick","x":161,"y":373,"w":68,"h":56},
                    {"type":"coin_block","x":159,"y":144,"w":64,"h":64},
                    {"type":"coin_block","x":791,"y":212,"w":64,"h":64}
                ]
        };

        // Add all sprites from the hard-coded map
        for(let i = 0; i < map.sprites.length; i++)
        {
            if(map.sprites[i].type === "background")
            {
                //console.log("found background");
                this.background = new Background(map.sprites[i].x, map.sprites[i].y);
                this.sprites.push(this.background);
            }
            if(map.sprites[i].type === "mario")
            {
                //console.log("* player found!");
                mario = new Mario(map.sprites[i].x, playerFloor, this);
                this.sprites.push(mario);
            }
            if(map.sprites[i].type === "brick")
            {
                //console.log("found brick");
                this.brick = new Brick(map.sprites[i].x, map.sprites[i].y, map.sprites[i].w, map.sprites[i].h);
                this.sprites.push(this.brick);
            }
            if(map.sprites[i].type === "coin_block")
            {
                //console.log("found coin block");
                this.coinBlock = new CoinBlock(map.sprites[i].x, map.sprites[i].y, this);
                this.sprites.push(this.coinBlock);
            }
        }
    }

    //=================================================================================================================
    // Iterate through sprite array and update all sprites
    //=================================================================================================================
    update()
    {
        for(let i = 0; i < this.sprites.length; i++)
        {
            this.sprites[i].update();
        }
    }

    //=================================================================================================================
    // Place brick: user clicks and drags mouse to create bricks of varying sizes
    //=================================================================================================================
    placeBrick(startX, startY, startW, startH)
    {
        this.brick = new Brick(startX, startY, startW, startH);
        this.sprites.push(this.brick);
    }

    //=================================================================================================================
    // Place coin block: user simply clicks where they want the block to be at
    //=================================================================================================================
    placeCoinBlock(startX, startY)
    {
        this.coinBlock = new CoinBlock(startX, startY, this);
        this.sprites.push(this.coinBlock);
    }
}